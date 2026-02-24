package com.claravis.app

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * LocalVLM — Análise de cena local usando SmolVLM-500M via llama.cpp
 *
 * Executa inteiramente no dispositivo, sem necessidade de internet.
 * Ideal para uso em aviões, áreas sem cobertura, ou privacidade total.
 *
 * Modelo: SmolVLM-500M-Instruct (HuggingFace/ggml-org)
 * Runtime: llama.cpp compilado para ARM64
 * Benchmark no Helio G85: ~73ms/token (Q8), ~4-6s para descrição curta
 *
 * Armazenamento:
 * - Modelo texto: SmolVLM-500M-Instruct-Q8_0.gguf (~417MB)
 * - Modelo visão: mmproj-SmolVLM-500M-Instruct-Q8_0.gguf (~104MB)
 * - Total: ~521MB (pode ficar no SD card)
 *
 * Locais de busca dos modelos (em ordem):
 * 1. /sdcard/ClaraVis/models/
 * 2. /sdcard/Download/
 * 3. App internal files dir
 */
class LocalVLM(private val context: Context) {

    companion object {
        private const val TAG = "ClaraVis-LocalVLM"
        private const val MODEL_FILENAME = "SmolVLM-500M-Instruct-Q8_0.gguf"
        private const val MMPROJ_FILENAME = "mmproj-SmolVLM-500M-Instruct-Q8_0.gguf"
        private const val BINARY_NAME = "llama-mtmd-cli"
        private const val NATIVE_LIB_BINARY_NAME = "libllama_mtmd.so"
        private const val MAX_TOKENS = 80
        private const val TIMEOUT_SECONDS = 90L
    }

    private val executor = Executors.newSingleThreadExecutor()

    private var modelPath: String? = null
    private var mmprojPath: String? = null
    private var binaryPath: String? = null
    private var initialized = false

    var onSceneDescription: ((String) -> Unit)? = null

    init {
        findModelFiles()
        findBinary()
    }

    private fun findModelFiles() {
        val searchPaths = listOf(
            "/data/local/tmp/claravis",        // Para testes via ADB
            context.filesDir.absolutePath,      // App internal storage
            context.getExternalFilesDir(null)?.absolutePath ?: "",  // App external
            "/sdcard/ClaraVis/models",          // SD card
            "/sdcard/Download"                  // Downloads
        )

        for (dir in searchPaths) {
            if (dir.isEmpty()) continue
            val modelFile = File(dir, MODEL_FILENAME)
            val mmprojFile = File(dir, MMPROJ_FILENAME)

            if (modelFile.exists() && modelFile.length() > 1000 &&
                mmprojFile.exists() && mmprojFile.length() > 1000) {
                modelPath = modelFile.absolutePath
                mmprojPath = mmprojFile.absolutePath
                Log.i(TAG, "Models found at: $dir")
                Log.i(TAG, "  Text model: ${modelFile.length() / 1024 / 1024}MB")
                Log.i(TAG, "  Vision model: ${mmprojFile.length() / 1024 / 1024}MB")
                return
            }
        }

        Log.i(TAG, "Models not found. Local VLM disabled.")
        Log.i(TAG, "To enable: place $MODEL_FILENAME and $MMPROJ_FILENAME in /data/local/tmp/claravis/")
    }

    private fun findBinary() {
        // Buscar o binário do llama.cpp
        // Prioridade 1: nativeLibraryDir (empacotado como .so em jniLibs, tem permissão de execução)
        // Prioridade 2: /data/local/tmp/claravis (para testes via ADB)
        // Prioridade 3: outros locais
        val nativeLibDir = context.applicationInfo.nativeLibraryDir
        val candidates = listOf(
            File(nativeLibDir, NATIVE_LIB_BINARY_NAME),  // jniLibs: tem execute permission
            File("/data/local/tmp/claravis", BINARY_NAME),
            File(nativeLibDir, BINARY_NAME),
            File(context.filesDir, BINARY_NAME),
            File("/data/local/tmp", BINARY_NAME)
        )

        for (file in candidates) {
            if (file.exists() && file.canExecute()) {
                binaryPath = file.absolutePath
                Log.i(TAG, "Binary found: ${file.absolutePath}")
                initialized = true
                return
            }
            // Tentar marcar como executável
            if (file.exists()) {
                file.setExecutable(true)
                if (file.canExecute()) {
                    binaryPath = file.absolutePath
                    Log.i(TAG, "Binary found (set executable): ${file.absolutePath}")
                    initialized = true
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
                Log.i(TAG, "Binary extracted from assets: ${assetBinary.absolutePath}")
                initialized = true
                return
            }
        } catch (e: Exception) {
            Log.d(TAG, "No binary in assets: ${e.message}")
        }

        Log.i(TAG, "llama.cpp binary not found. Local VLM disabled.")
        Log.i(TAG, "To enable: place $BINARY_NAME in /data/local/tmp/ (via: adb push)")
    }

    fun isAvailable(): Boolean {
        return modelPath != null && mmprojPath != null && binaryPath != null
    }

    /**
     * Analisa a cena localmente usando SmolVLM.
     * O bitmap é salvo como JPEG temporário e passado ao llama.cpp.
     * Resultado entregue via callback onSceneDescription.
     */
    fun analyzeScene(
        bitmap: Bitmap,
        yoloDetections: List<Detection>,
        cameraOrientation: CameraOrientation = CameraOrientation.LOOKING_FORWARD
    ) {
        if (!isAvailable()) return

        executor.execute {
            var tempImage: File? = null
            try {
                // Salvar bitmap como JPEG temporário
                tempImage = File(context.cacheDir, "vlm_frame.jpg")
                FileOutputStream(tempImage).use { fos ->
                    // Redimensionar para 384x384 para o SmolVLM
                    val scaled = Bitmap.createScaledBitmap(bitmap, 384, 384, true)
                    scaled.compress(Bitmap.CompressFormat.JPEG, 80, fos)
                    scaled.recycle()
                }
                bitmap.recycle()

                // Contexto do YOLO
                val yoloInfo = if (yoloDetections.isNotEmpty()) {
                    val items = yoloDetections.take(5).joinToString(", ") { it.label }
                    "Objects detected: $items. "
                } else ""

                // Prompt adaptado à orientação — em inglês para o SmolVLM (melhor accuracy)
                val orientationPrompt = when (cameraOrientation) {
                    CameraOrientation.LOOKING_DOWN ->
                        "The camera is pointing DOWN at the floor. Focus on: stairs, steps, holes, obstacles on the ground, floor type, curbs."
                    CameraOrientation.LOOKING_FORWARD ->
                        "The camera is pointing FORWARD at eye level. Focus on: path ahead, doors, corridors, signs, people approaching, stairs ahead."
                    CameraOrientation.LOOKING_UP ->
                        "The camera is pointing UP. Focus on: signs, labels, traffic lights, overhead obstacles."
                }

                val prompt = "${yoloInfo}${orientationPrompt} " +
                    "Briefly describe what you see for a visually impaired person. Focus on hazards and navigation. Max 2 sentences in Portuguese."

                val result = runInference(tempImage.absolutePath, prompt)
                if (result != null && result.isNotBlank()) {
                    Log.i(TAG, "Local VLM [${cameraOrientation.name}]: $result")
                    onSceneDescription?.invoke(result)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Local VLM error: ${e.message}")
            } finally {
                tempImage?.delete()
            }
        }
    }

    private fun runInference(imagePath: String, prompt: String): String? {
        val binary = binaryPath ?: return null
        val model = modelPath ?: return null
        val mmproj = mmprojPath ?: return null

        // Diretório das libs: priorizar nativeLibraryDir (onde Android instala jniLibs)
        val nativeLibDir = context.applicationInfo.nativeLibraryDir
        val binaryParentDir = File(binary).parent ?: "/data/local/tmp/claravis"
        val libDir = "$nativeLibDir:$binaryParentDir"

        val command = listOf(
            binary,
            "-m", model,
            "--mmproj", mmproj,
            "--image", imagePath,
            "-p", prompt,
            "-n", MAX_TOKENS.toString(),
            "--temp", "0.3",
            "-t", "4",              // 4 threads (2x A75 + 2x A55)
            "--ctx-size", "512"     // Contexto pequeno para economizar RAM
        )

        Log.d(TAG, "Running inference with image: $imagePath")

        val startTime = System.currentTimeMillis()

        try {
            val processBuilder = ProcessBuilder(command)
            processBuilder.redirectErrorStream(false)  // Separar stderr de stdout
            processBuilder.directory(File(binaryParentDir))
            processBuilder.environment()["LD_LIBRARY_PATH"] = libDir

            val process = processBuilder.start()

            // Ler stderr em thread separada para evitar deadlock
            val stderrThread = Thread {
                try {
                    val stderr = process.errorStream.bufferedReader().readText().trim()
                    if (stderr.isNotBlank()) {
                        Log.d(TAG, "stderr (first 300): ${stderr.take(300)}")
                    }
                } catch (_: Exception) {}
            }
            stderrThread.start()

            val completed = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)

            if (!completed) {
                process.destroyForcibly()
                stderrThread.interrupt()
                Log.w(TAG, "Inference timed out after ${TIMEOUT_SECONDS}s")
                return null
            }

            stderrThread.join(2000)

            val output = process.inputStream.bufferedReader().readText().trim()
            val elapsed = System.currentTimeMillis() - startTime

            Log.i(TAG, "Inference completed in ${elapsed}ms, stdout length=${output.length}")

            return if (process.exitValue() == 0 && output.isNotBlank()) {
                cleanOutput(output)
            } else {
                Log.w(TAG, "Process exit=${process.exitValue()}, stdout: ${output.take(200)}")
                null
            }

        } catch (e: Exception) {
            Log.e(TAG, "Process execution failed: ${e.message}")
            return null
        }
    }

    private fun cleanOutput(raw: String): String {
        // Filtrar linhas de log do llama.cpp que possam vazar para stdout
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
        return lines.joinToString(" ")
            .replace(Regex("<[^>]+>"), "")     // Remove HTML/XML tags
            .replace(Regex("\\[.*?\\]"), "")    // Remove [tokens]
            .replace(Regex("<\\|.*?\\|>"), "")  // Remove special tokens
            .replace(Regex("^\\s*Assistant:?\\s*", RegexOption.IGNORE_CASE), "")
            .trim()
            .take(300)
    }

    fun getStatus(): String {
        return when {
            isAvailable() -> "VLM local pronto ($MODEL_FILENAME)"
            modelPath == null -> "Modelo não encontrado"
            binaryPath == null -> "llama.cpp não encontrado"
            else -> "Não disponível"
        }
    }
}
