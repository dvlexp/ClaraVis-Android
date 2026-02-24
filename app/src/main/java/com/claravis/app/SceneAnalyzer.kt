package com.claravis.app

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

/**
 * SceneAnalyzer — Integração com Google Gemini Flash para análise de cena.
 *
 * Complementa o YOLOv8 com compreensão visual avançada:
 * - Detecta escadas, buracos, degraus, desníveis (YOLO não consegue)
 * - Lê textos e placas
 * - Descreve o ambiente (interno/externo, iluminação)
 * - Identifica perigos que o YOLO não cobre
 *
 * Uso: Similar aos óculos Meta Quest/Apple Vision Pro que usam IA na nuvem
 * para compreensão de cena, combinada com detecção on-device para tempo real.
 *
 * Para configurar:
 * 1. Obtenha uma chave gratuita em https://aistudio.google.com/apikey
 * 2. Salve no arquivo /sdcard/Download/claravis_api_key.txt
 *    OU defina a variável GEMINI_API_KEY no SharedPreferences
 */
class SceneAnalyzer(private val context: Context) {

    companion object {
        private const val TAG = "ClaraVis-Scene"
        private const val GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-lite:generateContent"
        private const val PREF_NAME = "claravis_prefs"
        private const val PREF_API_KEY = "gemini_api_key"
    }

    private val executor = Executors.newSingleThreadExecutor()
    private var apiKey: String? = null
    var onSceneDescription: ((String) -> Unit)? = null
    var onError: (() -> Unit)? = null

    init {
        loadApiKey()
    }

    private fun loadApiKey() {
        // Tentar SharedPreferences primeiro
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        apiKey = prefs.getString(PREF_API_KEY, null)

        if (apiKey.isNullOrBlank()) {
            // Tentar arquivo no sdcard
            try {
                val file = java.io.File("/sdcard/Download/claravis_api_key.txt")
                if (file.exists()) {
                    apiKey = file.readText().trim()
                    // Salvar no SharedPreferences para próximas vezes
                    prefs.edit().putString(PREF_API_KEY, apiKey).apply()
                    Log.i(TAG, "API key loaded from file")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Could not read API key file: ${e.message}")
            }
        }

        if (apiKey.isNullOrBlank()) {
            Log.i(TAG, "No Gemini API key configured. Cloud AI disabled. " +
                "To enable: save key to /sdcard/Download/claravis_api_key.txt")
        } else {
            Log.i(TAG, "Gemini API key configured. Cloud AI enabled.")
        }
    }

    fun setApiKey(key: String) {
        apiKey = key
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().putString(PREF_API_KEY, key).apply()
        Log.i(TAG, "API key updated")
    }

    fun isConfigured(): Boolean = !apiKey.isNullOrBlank()

    /**
     * Analisa a cena de forma assíncrona usando Gemini Flash.
     * O bitmap é redimensionado para economizar bandwidth.
     * O prompt é adaptado baseado na orientação da câmera (acelerômetro).
     * O resultado é entregue via callback onSceneDescription.
     */
    fun analyzeScene(
        bitmap: Bitmap,
        currentDetections: List<Detection>,
        cameraOrientation: CameraOrientation = CameraOrientation.LOOKING_FORWARD,
        ocrText: String? = null
    ) {
        if (!isConfigured()) return

        executor.execute {
            try {
                // Redimensionar para 480px de largura (economizar banda)
                val scale = 480f / bitmap.width
                val scaledBitmap = Bitmap.createScaledBitmap(
                    bitmap,
                    480,
                    (bitmap.height * scale).toInt(),
                    true
                )

                // Converter para JPEG base64
                val baos = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
                scaledBitmap.recycle()
                bitmap.recycle()
                val imageBase64 = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)

                // Contexto do YOLO para o prompt
                val yoloContext = if (currentDetections.isNotEmpty()) {
                    val detList = currentDetections.take(5).joinToString(", ") {
                        "${it.label} (${(it.confidence * 100).toInt()}%)"
                    }
                    "O detector YOLO já identificou: $detList. "
                } else {
                    "O detector YOLO não identificou objetos neste frame. "
                }

                // Contexto de OCR
                val ocrContext = if (!ocrText.isNullOrBlank()) {
                    "Texto detectado na imagem: \"$ocrText\". "
                } else ""

                // Prompt adaptado à orientação da câmera
                val orientationContext = when (cameraOrientation) {
                    CameraOrientation.LOOKING_DOWN -> """A câmera está apontada para o CHÃO/PÉS do usuário.
FOCO PRINCIPAL: Identifique o que está no piso/chão:
- ESCADAS, degraus, desníveis, rampas (PRIORIDADE MÁXIMA)
- Buracos, rachaduras, piso irregular, poças, piso molhado
- Objetos no caminho: pedras, lixo, fios, raízes
- Tipo de piso: calçada, terra, grama, cerâmica, asfalto
- Guias/meio-fio, faixas táteis, limites de calçada"""

                    CameraOrientation.LOOKING_FORWARD -> """A câmera está apontada para FRENTE, na altura dos olhos.
FOCO PRINCIPAL: Descreva o caminho e o ambiente:
- Caminho: livre ou bloqueado, largura, direção
- PORTAS (abertas/fechadas), corredores, passagens
- Possibilidade de virar à DIREITA ou ESQUERDA
- Subir ou descer escadas/rampas à frente
- Pessoas se aproximando, obstáculos na altura do corpo
- PLACAS e sinalizações (banheiro, saída, elevador, etc.)"""

                    CameraOrientation.LOOKING_UP -> """A câmera está apontada para CIMA.
FOCO PRINCIPAL: Identifique sinalizações e elementos aéreos:
- PLACAS: banheiro, saída, elevador, número de sala/andar
- Semáforos, sinais de trânsito
- Obstáculos aéreos: galhos, toldos, andaimes
- Letreiros de lojas, nomes de ruas"""
                }

                val prompt = """Você é Clara, assistente visual do ClaraVis, ajudando uma pessoa com deficiência visual a se locomover com segurança.

$orientationContext

$yoloContext$ocrContext

REGRAS:
- Responda em português brasileiro
- Máximo 30 palavras, 1-2 frases CURTAS e DIRETAS
- Se houver ESCADAS ou DEGRAUS, SEMPRE mencione primeiro
- Se houver perigo, alerte PRIMEIRO. Se não, diga "caminho livre" e descreva o ambiente
- Mencione placas/textos legíveis se houver"""

                val description = callGeminiApi(imageBase64, prompt)
                if (description != null) {
                    Log.i(TAG, "Scene [${cameraOrientation.name}]: $description")
                    onSceneDescription?.invoke(description)
                } else {
                    onError?.invoke()
                }

            } catch (e: Exception) {
                Log.e(TAG, "Scene analysis error: ${e.message}")
                onError?.invoke()
            }
        }
    }

    private val modelEndpoints = listOf(
        "gemini-2.5-flash-lite",
        "gemini-2.0-flash-lite",
        "gemini-2.0-flash"
    )
    private var currentModelIndex = 0

    private fun callGeminiApi(imageBase64: String, prompt: String): String? {
        val key = apiKey ?: return null

        val requestBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                        put(JSONObject().apply {
                            put("inline_data", JSONObject().apply {
                                put("mime_type", "image/jpeg")
                                put("data", imageBase64)
                            })
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("maxOutputTokens", 100)
                put("temperature", 0.3)
            })
        }

        // Tentar cada modelo até um funcionar
        for (attempt in modelEndpoints.indices) {
            val modelIdx = (currentModelIndex + attempt) % modelEndpoints.size
            val model = modelEndpoints[modelIdx]
            val url = URL("https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$key")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.connectTimeout = 10000
            conn.readTimeout = 15000
            conn.doOutput = true

            try {
                conn.outputStream.use { os ->
                    os.write(requestBody.toString().toByteArray())
                }

                val responseCode = conn.responseCode
                if (responseCode == 200) {
                    currentModelIndex = modelIdx  // Lembrar qual modelo funcionou
                    val response = conn.inputStream.bufferedReader().readText()
                    val json = JSONObject(response)
                    val candidates = json.getJSONArray("candidates")
                    if (candidates.length() > 0) {
                        val content = candidates.getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text")
                        Log.i(TAG, "Success with model: $model")
                        return content.trim()
                    }
                } else if (responseCode == 429) {
                    Log.w(TAG, "Rate limited on $model, backing off")
                    return null  // Não tentar outros modelos — respeitar rate limit
                } else {
                    val errorBody = conn.errorStream?.bufferedReader()?.readText() ?: "unknown"
                    Log.e(TAG, "Gemini API error $responseCode ($model): ${errorBody.take(200)}")
                    if (responseCode == 403 || responseCode == 400) continue  // Tentar próximo modelo
                }
            } catch (e: Exception) {
                Log.e(TAG, "Request failed ($model): ${e.message}")
                continue
            } finally {
                conn.disconnect()
            }
        }

        Log.w(TAG, "All models failed")
        return null
    }
}
