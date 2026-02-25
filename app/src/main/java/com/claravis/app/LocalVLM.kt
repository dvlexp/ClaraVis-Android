package com.claravis.app

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * LocalVLM — Análise de cena local usando modelos VLM via llama.cpp
 *
 * Suporta múltiplos modelos com prioridade automática:
 * 1. InternVL3-1B (melhor qualidade, ~1.1GB total)
 * 2. SmolVLM-500M (mais rápido, menor, fallback)
 *
 * Cada modelo é executado via llama-mtmd-cli (llama.cpp multimodal CLI).
 * O processo carrega o modelo, faz inferência e sai — sem manter RAM ocupada.
 *
 * Locais de busca dos modelos: /data/local/tmp/claravis/, app filesDir, sdcard
 */
class LocalVLM(private val context: Context) {

    companion object {
        private const val TAG = "ClaraVis-LocalVLM"
        private const val BINARY_NAME = "llama-mtmd-cli"
        private const val NATIVE_LIB_BINARY_NAME = "libllama_mtmd.so"
        private const val TIMEOUT_SECONDS = 180L
    }

    @Volatile
    var isRunning = false
        private set

    /** Configuração de um modelo VLM disponível */
    data class VLMModel(
        val name: String,
        val modelFilename: String,
        val mmprojFilename: String,
        val maxTokens: Int,
        val imageSize: Int,       // Resolução para redimensionar a imagem
        val ctxSize: Int,         // Tamanho do contexto
        val promptStyle: PromptStyle
    )

    enum class PromptStyle {
        INTERNVL,    // InternVL3 — suporta prompts mais complexos, responde melhor
        SMOLVLM      // SmolVLM-500M — precisa de prompts simples e diretos
    }

    // Modelos em ordem de prioridade (melhor primeiro)
    private val modelCandidates = listOf(
        VLMModel(
            name = "InternVL3-1B",
            modelFilename = "InternVL3-1B-Instruct-Q8_0.gguf",
            mmprojFilename = "mmproj-InternVL3-1B-Instruct-Q8_0.gguf",
            maxTokens = 50,
            imageSize = 448,   // InternVL nativo 448x448 — não reduzir (mmproj espera este tamanho)
            ctxSize = 512,
            promptStyle = PromptStyle.INTERNVL
        ),
        VLMModel(
            name = "SmolVLM-500M",
            modelFilename = "SmolVLM-500M-Instruct-Q8_0.gguf",
            mmprojFilename = "mmproj-SmolVLM-500M-Instruct-Q8_0.gguf",
            maxTokens = 50,
            imageSize = 384,   // SmolVLM usa 384x384
            ctxSize = 512,
            promptStyle = PromptStyle.SMOLVLM
        )
    )

    private val executor = Executors.newSingleThreadExecutor()

    private var activeModel: VLMModel? = null
    private var modelPath: String? = null
    private var mmprojPath: String? = null
    private var binaryPath: String? = null

    var onSceneDescription: ((String) -> Unit)? = null
    var onInferenceStart: (() -> Unit)? = null
    var onInferenceEnd: (() -> Unit)? = null

    init {
        findBestModel()
        findBinary()
    }

    private val searchPaths = listOf(
        "/data/local/tmp/claravis",
        { context.filesDir.absolutePath },
        { context.getExternalFilesDir(null)?.absolutePath ?: "" },
        "/sdcard/ClaraVis/models",
        "/sdcard/Download"
    )

    private fun getSearchDirs(): List<String> {
        return listOf(
            "/data/local/tmp/claravis",
            context.filesDir.absolutePath,
            context.getExternalFilesDir(null)?.absolutePath ?: "",
            "/sdcard/ClaraVis/models",
            "/sdcard/Download"
        ).filter { it.isNotEmpty() }
    }

    /** Procura o melhor modelo disponível no dispositivo */
    private fun findBestModel() {
        val dirs = getSearchDirs()

        for (candidate in modelCandidates) {
            for (dir in dirs) {
                val modelFile = File(dir, candidate.modelFilename)
                val mmprojFile = File(dir, candidate.mmprojFilename)

                if (modelFile.exists() && modelFile.length() > 1000 &&
                    mmprojFile.exists() && mmprojFile.length() > 1000) {
                    activeModel = candidate
                    modelPath = modelFile.absolutePath
                    mmprojPath = mmprojFile.absolutePath
                    Log.i(TAG, "Best model: ${candidate.name} at $dir")
                    Log.i(TAG, "  Text: ${modelFile.length() / 1024 / 1024}MB (${candidate.modelFilename})")
                    Log.i(TAG, "  Vision: ${mmprojFile.length() / 1024 / 1024}MB (${candidate.mmprojFilename})")
                    return
                }
            }
        }

        // Log de modelos não encontrados para debug
        Log.i(TAG, "No VLM models found. Searched for:")
        for (c in modelCandidates) {
            Log.i(TAG, "  ${c.name}: ${c.modelFilename} + ${c.mmprojFilename}")
        }
        Log.i(TAG, "  In dirs: ${dirs.joinToString(", ")}")
    }

    private fun findBinary() {
        val nativeLibDir = context.applicationInfo.nativeLibraryDir
        val candidates = listOf(
            File(nativeLibDir, NATIVE_LIB_BINARY_NAME),
            File("/data/local/tmp/claravis", BINARY_NAME),
            File(nativeLibDir, BINARY_NAME),
            File(context.filesDir, BINARY_NAME),
            File("/data/local/tmp", BINARY_NAME)
        )

        for (file in candidates) {
            if (file.exists() && file.canExecute()) {
                binaryPath = file.absolutePath
                Log.i(TAG, "Binary: ${file.absolutePath}")
                return
            }
            if (file.exists()) {
                file.setExecutable(true)
                if (file.canExecute()) {
                    binaryPath = file.absolutePath
                    Log.i(TAG, "Binary (set exec): ${file.absolutePath}")
                    return
                }
            }
        }

        // Tentar extrair de assets
        try {
            val assetBinary = File(context.filesDir, BINARY_NAME)
            context.assets.open(BINARY_NAME).use { input ->
                FileOutputStream(assetBinary).use { output ->
                    input.copyTo(output)
                }
            }
            assetBinary.setExecutable(true)
            if (assetBinary.canExecute()) {
                binaryPath = assetBinary.absolutePath
                Log.i(TAG, "Binary extracted from assets")
                return
            }
        } catch (_: Exception) {}

        Log.w(TAG, "llama.cpp binary not found. Local VLM disabled.")
    }

    fun isAvailable(): Boolean {
        return activeModel != null && modelPath != null && mmprojPath != null && binaryPath != null
    }

    fun getActiveModelName(): String = activeModel?.name ?: "none"

    /**
     * Analisa a cena localmente usando o melhor modelo VLM disponível.
     */
    fun analyzeScene(
        bitmap: Bitmap,
        yoloDetections: List<Detection>,
        cameraOrientation: CameraOrientation = CameraOrientation.LOOKING_FORWARD
    ) {
        if (!isAvailable()) { bitmap.recycle(); return }
        if (isRunning) { bitmap.recycle(); return }  // Não acumular chamadas
        val model = activeModel ?: run { bitmap.recycle(); return }

        executor.execute {
            isRunning = true
            onInferenceStart?.invoke()
            var tempImage: File? = null
            try {
                tempImage = File(context.cacheDir, "vlm_frame.jpg")
                FileOutputStream(tempImage).use { fos ->
                    val scaled = Bitmap.createScaledBitmap(bitmap, model.imageSize, model.imageSize, true)
                    scaled.compress(Bitmap.CompressFormat.JPEG, 80, fos)
                    scaled.recycle()
                }
                bitmap.recycle()

                // Contexto do YOLO
                val yoloInfo = if (yoloDetections.isNotEmpty()) {
                    val items = yoloDetections.take(5).joinToString(", ") { it.label }
                    "Objetos detectados: $items. "
                } else ""

                // Prompt adaptado ao modelo e orientação
                val prompt = buildPrompt(model.promptStyle, cameraOrientation, yoloInfo)

                val result = runInference(tempImage.absolutePath, prompt, model)
                if (result != null && result.isNotBlank()) {
                    Log.i(TAG, "[${model.name}] [${cameraOrientation.name}]: $result")
                    onSceneDescription?.invoke(result)
                }

            } catch (e: Exception) {
                Log.e(TAG, "VLM error: ${e.message}")
            } finally {
                tempImage?.delete()
                isRunning = false
                onInferenceEnd?.invoke()
            }
        }
    }

    private fun buildPrompt(style: PromptStyle, orientation: CameraOrientation, yoloInfo: String): String {
        return when (style) {
            PromptStyle.INTERNVL -> {
                // InternVL3 — prompts diretos, pede resposta em português
                when (orientation) {
                    CameraOrientation.LOOKING_DOWN ->
                        "${yoloInfo}Descreva o chão: tipo de piso, obstáculos, escadas, buracos, objetos. Seja específico sobre posições."
                    CameraOrientation.LOOKING_FORWARD ->
                        "${yoloInfo}Descreva esta cena: o que há à frente, portas, corredores, pessoas, obstáculos, placas. Mencione posições (esquerda, direita, centro)."
                    CameraOrientation.LOOKING_UP ->
                        "${yoloInfo}Descreva o que está acima: placas, textos, letreiros, características do teto."
                }
            }
            PromptStyle.SMOLVLM -> {
                // SmolVLM precisa de prompts curtos e diretos
                when (orientation) {
                    CameraOrientation.LOOKING_DOWN ->
                        "${yoloInfo}What is on the floor? Are there stairs, holes, or obstacles?"
                    CameraOrientation.LOOKING_FORWARD ->
                        "${yoloInfo}Describe this room or corridor. Any doors, stairs, or signs?"
                    CameraOrientation.LOOKING_UP ->
                        "${yoloInfo}What signs or labels are visible above?"
                }
            }
        }
    }

    private fun runInference(imagePath: String, prompt: String, model: VLMModel): String? {
        val binary = binaryPath ?: return null
        val modelFile = modelPath ?: return null
        val mmproj = mmprojPath ?: return null

        val nativeLibDir = context.applicationInfo.nativeLibraryDir
        val binaryParentDir = File(binary).parent ?: "/data/local/tmp/claravis"
        val libDir = "$nativeLibDir:$binaryParentDir"

        val command = listOf(
            binary,
            "-m", modelFile,
            "--mmproj", mmproj,
            "--image", imagePath,
            "-p", prompt,
            "-n", model.maxTokens.toString(),
            "--temp", "0.3",
            "-t", "4",
            "--ctx-size", model.ctxSize.toString()
        )

        Log.d(TAG, "[${model.name}] Running inference...")

        val startTime = System.currentTimeMillis()

        try {
            val processBuilder = ProcessBuilder(command)
            processBuilder.redirectErrorStream(false)
            processBuilder.directory(File(binaryParentDir))
            processBuilder.environment()["LD_LIBRARY_PATH"] = libDir

            val process = processBuilder.start()

            val stderrThread = Thread {
                try {
                    val stderr = process.errorStream.bufferedReader().readText().trim()
                    if (stderr.isNotBlank()) {
                        Log.d(TAG, "[${model.name}] stderr: ${stderr.take(200)}")
                    }
                } catch (_: Exception) {}
            }
            stderrThread.start()

            val completed = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)

            if (!completed) {
                process.destroyForcibly()
                stderrThread.interrupt()
                Log.w(TAG, "[${model.name}] Timeout after ${TIMEOUT_SECONDS}s")
                return null
            }

            stderrThread.join(2000)

            val output = process.inputStream.bufferedReader().readText().trim()
            val elapsed = System.currentTimeMillis() - startTime

            Log.i(TAG, "[${model.name}] Done in ${elapsed}ms, output=${output.length} chars")

            return if (process.exitValue() == 0 && output.isNotBlank()) {
                cleanOutput(output)
            } else {
                Log.w(TAG, "[${model.name}] exit=${process.exitValue()}, out: ${output.take(200)}")
                null
            }

        } catch (e: Exception) {
            Log.e(TAG, "[${model.name}] Process failed: ${e.message}")
            return null
        }
    }

    private fun cleanOutput(raw: String): String {
        val lines = raw.lines().filter { line ->
            !line.startsWith("build:") &&
            !line.startsWith("common_") &&
            !line.startsWith("llama_") &&
            !line.startsWith("ggml_") &&
            !line.startsWith("gguf_") &&
            !line.contains("fitting params") &&
            !line.contains("_init_") &&
            !line.contains("_load") &&
            !line.contains("perf_") &&
            !line.startsWith("main:") &&
            !line.startsWith("clip_") &&
            !line.startsWith("encode_") &&
            line.isNotBlank()
        }
        var text = lines.joinToString(" ")
            .replace(Regex("<[^>]+>"), "")
            .replace(Regex("\\[.*?\\]"), "")
            .replace(Regex("<\\|.*?\\|>"), "")
            .replace(Regex("^\\s*Assistant:?\\s*", RegexOption.IGNORE_CASE), "")
            .replace(Regex("Camera pointing[^.]*\\.", RegexOption.IGNORE_CASE), "")
            .replace(Regex("The camera is[^.]*\\.", RegexOption.IGNORE_CASE), "")
            .replace(Regex("You are helping[^.]*\\.", RegexOption.IGNORE_CASE), "")
            .replace(Regex("Objects detected[^.]*\\.", RegexOption.IGNORE_CASE), "")
            .trim()
            .take(400)
        if (text.length < 10) {
            text = lines.joinToString(" ")
                .replace(Regex("<[^>]+>"), "")
                .replace(Regex("<\\|.*?\\|>"), "")
                .trim()
                .take(400)
        }
        return text
    }

    /**
     * Responde uma pergunta do usuário sobre o que a câmera está vendo.
     * Usado pela interação por voz ("Clara, você vê o controle?")
     */
    fun answerQuestion(
        bitmap: Bitmap,
        question: String,
        onAnswer: (String) -> Unit
    ) {
        if (!isAvailable()) { bitmap.recycle(); return }
        if (isRunning) { bitmap.recycle(); onAnswer("Aguarde, ainda estou processando..."); return }
        val model = activeModel ?: run { bitmap.recycle(); return }

        executor.execute {
            isRunning = true
            onInferenceStart?.invoke()
            var tempImage: File? = null
            try {
                tempImage = File(context.cacheDir, "vlm_question.jpg")
                FileOutputStream(tempImage).use { fos ->
                    val scaled = Bitmap.createScaledBitmap(bitmap, model.imageSize, model.imageSize, true)
                    scaled.compress(Bitmap.CompressFormat.JPEG, 85, fos)
                    scaled.recycle()
                }
                bitmap.recycle()

                // Prompt direto com a pergunta do usuário
                val prompt = "Pergunta: $question. Responda de forma curta e direta baseado na imagem."

                val result = runInference(tempImage.absolutePath, prompt, model)
                if (result != null && result.isNotBlank()) {
                    Log.i(TAG, "[${model.name}] Question: $question → $result")
                    onAnswer(result)
                } else {
                    onAnswer("Não consegui analisar a imagem.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "VLM question error: ${e.message}")
                onAnswer("Erro ao processar.")
            } finally {
                tempImage?.delete()
                isRunning = false
                onInferenceEnd?.invoke()
            }
        }
    }

    fun getStatus(): String {
        val model = activeModel
        return when {
            model != null && isAvailable() -> "VLM local: ${model.name} pronto"
            modelPath == null -> "Nenhum modelo VLM encontrado"
            binaryPath == null -> "llama.cpp não encontrado"
            else -> "Não disponível"
        }
    }
}
