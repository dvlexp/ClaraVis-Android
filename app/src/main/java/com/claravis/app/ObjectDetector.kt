package com.claravis.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

enum class ObjectCategory {
    PERSON,     // Verde
    OBSTACLE,   // Vermelho — objetos no chão, mobília
    VEHICLE,    // Azul
    ANIMAL,     // Amarelo
    OTHER       // Cinza
}

data class Detection(
    val boundingBox: RectF,  // Coordenadas normalizadas 0.0-1.0
    val label: String,
    val classId: Int,
    val confidence: Float,
    val category: ObjectCategory
)

class ObjectDetector(private val context: Context) {

    private var interpreter: Interpreter
    var inputSize = 320
        private set
    private val numChannels = 3

    // Flag para auto-detectar se as coordenadas são pixel (0-inputSize) ou normalizadas (0-1)
    private var coordScale = -1f

    // Nome do modelo atual
    private var currentModelName = ""

    private val labels = arrayOf(
        "pessoa", "bicicleta", "carro", "moto", "avião", "ônibus", "trem", "caminhão",
        "barco", "semáforo", "hidrante", "placa de pare", "parquímetro", "banco",
        "pássaro", "gato", "cachorro", "cavalo", "ovelha", "vaca", "elefante",
        "urso", "zebra", "girafa", "mochila", "guarda-chuva", "bolsa", "gravata",
        "mala", "frisbee", "esqui", "snowboard", "bola", "pipa", "taco de beisebol",
        "luva", "skate", "prancha de surfe", "raquete", "garrafa",
        "taça de vinho", "copo", "garfo", "faca", "colher", "tigela", "banana",
        "maçã", "sanduíche", "laranja", "brócolis", "cenoura", "cachorro-quente",
        "pizza", "rosquinha", "bolo", "cadeira", "sofá", "vaso de planta", "cama",
        "mesa de jantar", "vaso sanitário", "televisão", "notebook", "mouse", "controle remoto",
        "teclado", "celular", "micro-ondas", "forno", "torradeira", "pia",
        "geladeira", "livro", "relógio", "vaso de flor", "tesoura", "urso de pelúcia",
        "secador de cabelo", "escova de dente"
    )

    // ── Classificação COCO → categoria de navegação ──
    private val categoryMap = mapOf(
        0 to ObjectCategory.PERSON,
        // Veículos (mobilidade)
        1 to ObjectCategory.VEHICLE, 2 to ObjectCategory.VEHICLE, 3 to ObjectCategory.VEHICLE,
        4 to ObjectCategory.VEHICLE, 5 to ObjectCategory.VEHICLE, 6 to ObjectCategory.VEHICLE,
        7 to ObjectCategory.VEHICLE, 8 to ObjectCategory.VEHICLE,
        // Animais
        14 to ObjectCategory.ANIMAL, 15 to ObjectCategory.ANIMAL, 16 to ObjectCategory.ANIMAL,
        17 to ObjectCategory.ANIMAL, 18 to ObjectCategory.ANIMAL, 19 to ObjectCategory.ANIMAL,
        20 to ObjectCategory.ANIMAL, 21 to ObjectCategory.ANIMAL, 22 to ObjectCategory.ANIMAL,
        23 to ObjectCategory.ANIMAL,
        // Obstáculos de chão / mobília / objetos no caminho
        9 to ObjectCategory.OBSTACLE,   // semáforo
        10 to ObjectCategory.OBSTACLE,  // hidrante
        11 to ObjectCategory.OBSTACLE,  // placa de pare
        12 to ObjectCategory.OBSTACLE,  // parquímetro
        13 to ObjectCategory.OBSTACLE,  // banco
        24 to ObjectCategory.OBSTACLE,  // mochila
        25 to ObjectCategory.OBSTACLE,  // guarda-chuva
        26 to ObjectCategory.OBSTACLE,  // bolsa
        28 to ObjectCategory.OBSTACLE,  // mala
        32 to ObjectCategory.OBSTACLE,  // bola
        36 to ObjectCategory.OBSTACLE,  // skate
        39 to ObjectCategory.OBSTACLE,  // garrafa
        56 to ObjectCategory.OBSTACLE,  // cadeira
        57 to ObjectCategory.OBSTACLE,  // sofá
        58 to ObjectCategory.OBSTACLE,  // vaso planta
        59 to ObjectCategory.OBSTACLE,  // cama
        60 to ObjectCategory.OBSTACLE,  // mesa
        61 to ObjectCategory.OBSTACLE,  // vaso sanitário
        72 to ObjectCategory.OBSTACLE   // geladeira
    )

    // ── Filtros de plausibilidade por classe ──
    // Classes que são geralmente PEQUENAS — se o box ocupa >40% da tela, é falso positivo
    private val smallObjectClasses = setOf(
        9, 10, 11, 12,       // semáforo, hidrante, placa, parquímetro
        14, 15, 16,           // pássaro, gato, cachorro
        24, 25, 26, 28,      // mochila, guarda-chuva, bolsa, mala
        32, 36,               // bola, skate
        39, 40, 41,           // garrafa, taça, copo
        42, 43, 44, 45,      // garfo, faca, colher, tigela
        46, 47, 48, 49, 50, 51, 52, 53, 54, 55, // alimentos
        63, 64, 65, 66, 67, // mouse, controle, teclado, celular
        68, 69, 70, 71,      // eletrodomésticos pequenos
        73, 74, 75, 76, 77, 78, 79  // livro, relógio, etc.
    )

    // Classes de eletrodomésticos grandes que são confundidas com superfícies planas
    // Esses precisam de confiança EXTRA alta quando o box é grande
    private val confusionProneClasses = setOf(
        62, // televisão — confundida com janelas, quadros, paredes
        72, // geladeira — confundida com paredes, portas, armários
        57, // sofá — confundido com camas, bancos
        59, // cama — confundida com pisos, tapetes
        60, // mesa — confundida com pisos, bancadas
        68, // micro-ondas — confundido com quadros
        69, // forno — confundido com gavetas
    )

    // Classes de objetos pequenos que NÃO são relevantes para navegação
    // Se aparecem com área minúscula, são ruído (ex: "mouse" com 1% da tela é noise)
    private val irrelevantTinyClasses = setOf(
        63, // notebook — irrelevante para navegação se minúsculo
        64, // mouse — irrelevante para navegação
        65, // controle remoto — irrelevante
        66, // teclado — irrelevante se pequeno
        67, // celular — irrelevante se pequeno
        27, // gravata — irrelevante
        29, // frisbee — raro
        30, // esqui — raro
        31, // snowboard — raro
        33, // pipa — raro
        34, // taco — raro
        35, // luva — irrelevante
        38, // raquete — raro
        40, // taça — irrelevante
        42, // garfo — irrelevante
        43, // faca — irrelevante
        44, // colher — irrelevante
        73, // livro — irrelevante
        76, // tesoura — irrelevante
        78, // secador — irrelevante
        79, // escova de dente — irrelevante
    )
    private val MIN_RELEVANT_AREA = 0.02f  // Objetos irrelevantes < 2% da tela = ignorar

    companion object {
        private const val TAG = "ClaraVis-Detector"
        private const val CONFIDENCE_THRESHOLD = 0.35f   // Subiu de 0.25 para 0.35
        private const val HIGH_CONF_THRESHOLD = 0.55f    // Para classes propensas a confusão
        private const val IOU_THRESHOLD = 0.45f
        private const val MAX_BOX_AREA = 0.70f           // Box > 70% da tela = provavelmente fundo
        private const val SMALL_OBJ_MAX_AREA = 0.35f     // Objetos pequenos > 35% = falso positivo
    }

    init {
        // Tentar carregar o melhor modelo disponível
        interpreter = loadBestModel()
    }

    private var gpuDelegateCloseable: AutoCloseable? = null

    private fun loadBestModel(): Interpreter {
        val options = Interpreter.Options()
        options.setNumThreads(4)

        // Tentar GPU delegate se hardware suportar (Mali G52 no Helio G85)
        try {
            val compatList = CompatibilityList()
            if (compatList.isDelegateSupportedOnThisDevice) {
                // Usar reflection para evitar problemas de versão de API
                val gpuDelegateClass = Class.forName("org.tensorflow.lite.gpu.GpuDelegate")
                val delegate = gpuDelegateClass.getDeclaredConstructor().newInstance()
                val addDelegateMethod = Interpreter.Options::class.java.getMethod("addDelegate", Class.forName("org.tensorflow.lite.Delegate"))
                addDelegateMethod.invoke(options, delegate)
                gpuDelegateCloseable = delegate as? AutoCloseable
                Log.i(TAG, "GPU delegate enabled")
            } else {
                Log.i(TAG, "GPU not supported, using CPU with 4 threads")
            }
        } catch (e: Exception) {
            Log.w(TAG, "GPU delegate unavailable, CPU fallback: ${e.message}")
        }

        // Ordem de preferência: float16 (menor, quase mesma qualidade) > float32 > nano
        // Também verifica SD card para modelos customizados (fine-tuned)
        val modelCandidates = listOf(
            "yolov8s_416_float16.tflite" to "asset",  // Fine-tuned 416x416
            "yolov8s_float16.tflite" to "asset",
            "yolov8s_float32.tflite" to "asset",
            "yolov8n_float32.tflite" to "asset"
        )

        // Verificar modelos no SD card (permite upgrade sem reinstalar)
        val sdModels = listOf(
            "/sdcard/Download/claravis_model.tflite",
            "/sdcard/ClaraVis/model.tflite"
        )

        for (sdPath in sdModels) {
            val file = File(sdPath)
            if (file.exists() && file.length() > 1000) {
                try {
                    val fis = FileInputStream(file)
                    val channel = fis.channel
                    val model = channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length())
                    val interp = Interpreter(model, options)
                    currentModelName = file.name
                    val inShape = interp.getInputTensor(0).shape()
                    inputSize = inShape[1]  // [1, H, W, 3]
                    Log.i(TAG, "Loaded model from SD: $sdPath, inputSize=$inputSize")
                    logModelInfo(interp)
                    return interp
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to load SD model $sdPath: ${e.message}")
                }
            }
        }

        for ((filename, _) in modelCandidates) {
            try {
                val model = loadModelFile(context, filename)
                val interp = Interpreter(model, options)
                currentModelName = filename
                val inShape = interp.getInputTensor(0).shape()
                inputSize = inShape[1]
                Log.i(TAG, "Loaded model from assets: $filename, inputSize=$inputSize")
                logModelInfo(interp)
                return interp
            } catch (e: Exception) {
                Log.d(TAG, "Model $filename not found in assets, trying next...")
            }
        }

        throw RuntimeException("Nenhum modelo YOLO encontrado!")
    }

    private fun logModelInfo(interp: Interpreter) {
        val inputShape = interp.getInputTensor(0).shape()
        val outputShape = interp.getOutputTensor(0).shape()
        Log.i(TAG, "Model: $currentModelName | Input: ${inputShape.contentToString()}, Output: ${outputShape.contentToString()}")
    }

    private fun loadModelFile(context: Context, filename: String): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd(filename)
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun detect(bitmap: Bitmap): List<Detection> {
        val resized = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)

        val inputBuffer = ByteBuffer.allocateDirect(1 * inputSize * inputSize * numChannels * 4)
        inputBuffer.order(ByteOrder.nativeOrder())
        inputBuffer.rewind()

        val pixels = IntArray(inputSize * inputSize)
        resized.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)

        // Calcular min/max de luminância para auto-contraste adaptativo
        var minLum = 255f
        var maxLum = 0f
        for (pixel in pixels) {
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF
            val lum = 0.299f * r + 0.587f * g + 0.114f * b
            if (lum < minLum) minLum = lum
            if (lum > maxLum) maxLum = lum
        }

        // Aplicar auto-contraste se a imagem tiver range dinâmico baixo (ex: pouca luz)
        val range = maxLum - minLum
        val applyContrast = range > 10f && range < 200f  // Imagem com pouco contraste
        val scale = if (applyContrast) 255f / range else 1f
        val offset = if (applyContrast) minLum else 0f

        for (pixel in pixels) {
            var r = ((pixel shr 16) and 0xFF).toFloat()
            var g = ((pixel shr 8) and 0xFF).toFloat()
            var b = (pixel and 0xFF).toFloat()
            if (applyContrast) {
                r = ((r - offset) * scale).coerceIn(0f, 255f)
                g = ((g - offset) * scale).coerceIn(0f, 255f)
                b = ((b - offset) * scale).coerceIn(0f, 255f)
            }
            inputBuffer.putFloat(r / 255.0f)
            inputBuffer.putFloat(g / 255.0f)
            inputBuffer.putFloat(b / 255.0f)
        }
        resized.recycle()

        val outputShape = interpreter.getOutputTensor(0).shape()
        val outputArray = Array(1) { Array(outputShape[1]) { FloatArray(outputShape[2]) } }

        interpreter.run(inputBuffer, outputArray)

        return parseOutput(outputArray[0], outputShape)
    }

    private fun parseOutput(output: Array<FloatArray>, shape: IntArray): List<Detection> {
        val numAttributes = shape[1]
        val numDetections = shape[2]
        val rawDetections = mutableListOf<Detection>()

        // Auto-detectar escala de coordenadas na primeira execução
        if (coordScale < 0f) {
            var maxVal = 0f
            for (i in 0 until minOf(numDetections, 100)) {
                for (attr in 0..3) {
                    val v = output[attr][i]
                    if (v > maxVal) maxVal = v
                }
            }
            coordScale = if (maxVal > 2.0f) inputSize.toFloat() else 1.0f
            Log.i(TAG, "Auto-detected coord scale: maxVal=$maxVal → dividing by $coordScale")
        }

        var logCount = 0

        for (i in 0 until numDetections) {
            var maxScore = 0f
            var maxClassId = 0
            for (c in 4 until numAttributes) {
                if (output[c][i] > maxScore) {
                    maxScore = output[c][i]
                    maxClassId = c - 4
                }
            }

            if (maxScore < CONFIDENCE_THRESHOLD * 0.8f) continue  // Pre-filter rápido (threshold base * 0.8)

            val cx = output[0][i] / coordScale
            val cy = output[1][i] / coordScale
            val w = output[2][i] / coordScale
            val h = output[3][i] / coordScale

            val left = (cx - w / 2).coerceIn(0f, 1f)
            val top = (cy - h / 2).coerceIn(0f, 1f)
            val right = (cx + w / 2).coerceIn(0f, 1f)
            val bottom = (cy + h / 2).coerceIn(0f, 1f)

            val boxW = right - left
            val boxH = bottom - top
            val boxArea = boxW * boxH

            // ── Filtro 1: Box muito grande = provavelmente fundo, não objeto ──
            if (boxArea > MAX_BOX_AREA) {
                if (false) Log.d(TAG, "Filtered (too large): class=$maxClassId area=${"%.2f".format(boxArea)} conf=${"%.2f".format(maxScore)}")
                continue
            }

            // ── Filtro 2: Objetos pequenos com box grande = falso positivo ──
            if (maxClassId in smallObjectClasses && boxArea > SMALL_OBJ_MAX_AREA) {
                if (false) Log.d(TAG, "Filtered (small obj, big box): class=$maxClassId area=${"%.2f".format(boxArea)}")
                continue
            }

            // ── Filtro 3: Classes propensas a confusão precisam confiança extra ──
            if (maxClassId in confusionProneClasses) {
                // Confiança base mais alta
                val requiredConf = if (boxArea > 0.25f) HIGH_CONF_THRESHOLD + 0.1f else HIGH_CONF_THRESHOLD
                if (maxScore < requiredConf) {
                    if (false) Log.d(TAG, "Filtered (confusion-prone): class=$maxClassId conf=${"%.2f".format(maxScore)} < ${"%.2f".format(requiredConf)}")
                    continue
                }
            }

            // ── Filtro 4: Aspect ratio impossível ──
            val aspectRatio = if (boxH > 0.01f) boxW / boxH else 0f
            // Pessoa deve ser mais alta que larga (aspect < 1.2 geralmente)
            if (maxClassId == 0 && aspectRatio > 3.0f) {
                if (false) Log.d(TAG, "Filtered (person too wide): aspect=${"%.2f".format(aspectRatio)}")
                continue
            }

            // ── Filtro 5: Objetos irrelevantes para navegação com área muito pequena ──
            if (maxClassId in irrelevantTinyClasses && boxArea < MIN_RELEVANT_AREA) {
                continue  // Silencioso — esses são ruído constante
            }

            // Log das primeiras detecções válidas
            if (logCount < 2) {
                val labelText = if (maxClassId < labels.size) labels[maxClassId] else "?"
                Log.i(TAG, "Det[$logCount]: $labelText conf=${"%.2f".format(maxScore)} " +
                    "box=[${"%.3f".format(left)}, ${"%.3f".format(top)}, ${"%.3f".format(right)}, ${"%.3f".format(bottom)}] " +
                    "area=${"%.2f".format(boxArea)}")
                logCount++
            }

            val labelText = if (maxClassId < labels.size) labels[maxClassId] else "objeto"
            val category = categoryMap[maxClassId] ?: ObjectCategory.OTHER

            rawDetections.add(Detection(
                boundingBox = RectF(left, top, right, bottom),
                label = labelText,
                classId = maxClassId,
                confidence = maxScore,
                category = category
            ))
        }

        val result = nms(rawDetections)
        if (result.isNotEmpty()) {
            Log.i(TAG, "[$currentModelName] Detections: ${result.size} (raw: ${rawDetections.size})")
        }
        return result
    }

    private fun nms(detections: List<Detection>): List<Detection> {
        val sorted = detections.sortedByDescending { it.confidence }.toMutableList()
        val result = mutableListOf<Detection>()
        while (sorted.isNotEmpty()) {
            val best = sorted.removeAt(0)
            result.add(best)
            sorted.removeAll { iou(best.boundingBox, it.boundingBox) > IOU_THRESHOLD }
        }
        return result
    }

    private fun iou(a: RectF, b: RectF): Float {
        val interLeft = maxOf(a.left, b.left)
        val interTop = maxOf(a.top, b.top)
        val interRight = minOf(a.right, b.right)
        val interBottom = minOf(a.bottom, b.bottom)
        val interArea = maxOf(0f, interRight - interLeft) * maxOf(0f, interBottom - interTop)
        val aArea = (a.right - a.left) * (a.bottom - a.top)
        val bArea = (b.right - b.left) * (b.bottom - b.top)
        return interArea / (aArea + bArea - interArea + 1e-5f)
    }

    /** Retorna posição textual para TTS baseado no centro X normalizado */
    fun getPosition(det: Detection): String {
        val centerX = (det.boundingBox.left + det.boundingBox.right) / 2
        val horizontal = when {
            centerX < 0.33f -> "à esquerda"
            centerX > 0.67f -> "à direita"
            else -> "à frente"
        }
        val boxHeight = det.boundingBox.bottom - det.boundingBox.top
        val distance = when {
            boxHeight > 0.6f -> "muito perto"
            boxHeight > 0.3f -> "perto"
            boxHeight > 0.15f -> ""
            else -> "ao longe"
        }
        return if (distance.isNotEmpty()) "$horizontal, $distance" else horizontal
    }

    fun getModelName(): String = currentModelName

    fun close() {
        interpreter.close()
        gpuDelegateCloseable?.close()
    }
}
