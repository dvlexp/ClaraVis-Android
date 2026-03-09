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

    // ── Labels COCO (80 classes) — modelo padrão ──
    private val cocoLabels = arrayOf(
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

    // ── Labels ClaraVis fine-tuned (36 classes) — treino #1 ──
    private val claravisLabels = arrayOf(
        "cama", "bicicleta", "lixeira", "livro", "cadeira", "cômoda", "escada descendo",
        "flor", "notebook", "papel", "pessoa", "vaso", "buraco", "sapato", "chinelo",
        "sofá", "escada", "mesa", "torneira", "árvore", "tronco", "caminhão",
        "escada subindo", "mochila", "bicicleta", "carro", "gato", "cachorro", "porta",
        "lixeira grande", "bolsa", "folhas", "celular", "moto", "sofá"
    )

    // ── Labels ClaraVis v2 (37 classes) — treino #2 multi-dataset ──
    private val claravisV2Labels = arrayOf(
        "pessoa", "carro", "moto", "bicicleta", "ônibus", "caminhão", "cachorro", "gato",
        "escada", "buraco", "bueiro", "meio-fio", "poste", "faixa de pedestre", "calçada",
        "poça", "semáforo", "ponto de ônibus", "hidrante", "placa de pare", "bollard",
        "árvore", "banco", "patinete", "barreira", "poste de luz", "tampa de esgoto",
        "dano no pavimento", "porta", "cadeira", "mesa", "sofá", "cama", "mochila",
        "bolsa", "lixeira", "tronco"
    )

    // ── Classificação ClaraVis v2 → categoria de navegação ──
    private val claravisV2CategoryMap = mapOf(
        0 to ObjectCategory.PERSON,    // pessoa
        // Veículos
        1 to ObjectCategory.VEHICLE,   // carro
        2 to ObjectCategory.VEHICLE,   // moto
        3 to ObjectCategory.VEHICLE,   // bicicleta
        4 to ObjectCategory.VEHICLE,   // ônibus
        5 to ObjectCategory.VEHICLE,   // caminhão
        23 to ObjectCategory.VEHICLE,  // patinete
        // Animais
        6 to ObjectCategory.ANIMAL,    // cachorro
        7 to ObjectCategory.ANIMAL,    // gato
        // Obstáculos — TUDO que importa para navegação
        8 to ObjectCategory.OBSTACLE,  // escada ⚠️
        9 to ObjectCategory.OBSTACLE,  // buraco ⚠️
        10 to ObjectCategory.OBSTACLE, // bueiro ⚠️
        11 to ObjectCategory.OBSTACLE, // meio-fio ⚠️
        12 to ObjectCategory.OBSTACLE, // poste
        13 to ObjectCategory.OBSTACLE, // faixa de pedestre
        14 to ObjectCategory.OBSTACLE, // calçada
        15 to ObjectCategory.OBSTACLE, // poça ⚠️
        16 to ObjectCategory.OBSTACLE, // semáforo
        17 to ObjectCategory.OBSTACLE, // ponto de ônibus
        18 to ObjectCategory.OBSTACLE, // hidrante
        19 to ObjectCategory.OBSTACLE, // placa de pare
        20 to ObjectCategory.OBSTACLE, // bollard
        21 to ObjectCategory.OBSTACLE, // árvore
        22 to ObjectCategory.OBSTACLE, // banco
        24 to ObjectCategory.OBSTACLE, // barreira ⚠️
        25 to ObjectCategory.OBSTACLE, // poste de luz
        26 to ObjectCategory.OBSTACLE, // tampa de esgoto ⚠️
        27 to ObjectCategory.OBSTACLE, // dano no pavimento ⚠️
        28 to ObjectCategory.OBSTACLE, // porta
        29 to ObjectCategory.OBSTACLE, // cadeira
        30 to ObjectCategory.OBSTACLE, // mesa
        31 to ObjectCategory.OBSTACLE, // sofá
        32 to ObjectCategory.OBSTACLE, // cama
        33 to ObjectCategory.OBSTACLE, // mochila
        34 to ObjectCategory.OBSTACLE, // bolsa
        35 to ObjectCategory.OBSTACLE, // lixeira
        36 to ObjectCategory.OBSTACLE  // tronco
    )

    // Labels ativas — selecionadas automaticamente pelo modelo carregado
    private var labels = cocoLabels

    // ── Classificação COCO → categoria de navegação ──
    private val cocoCategoryMap = mapOf(
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

    // ── Classificação ClaraVis fine-tuned → categoria de navegação ──
    private val claravisCategoryMap = mapOf(
        10 to ObjectCategory.PERSON,    // pessoa
        // Veículos
        1 to ObjectCategory.VEHICLE,    // bicicleta
        21 to ObjectCategory.VEHICLE,   // caminhão
        24 to ObjectCategory.VEHICLE,   // bicicleta (2)
        25 to ObjectCategory.VEHICLE,   // carro
        33 to ObjectCategory.VEHICLE,   // moto
        // Animais
        26 to ObjectCategory.ANIMAL,    // gato
        27 to ObjectCategory.ANIMAL,    // cachorro
        // Obstáculos — TUDO que importa para navegação
        0 to ObjectCategory.OBSTACLE,   // cama
        2 to ObjectCategory.OBSTACLE,   // lixeira
        4 to ObjectCategory.OBSTACLE,   // cadeira
        5 to ObjectCategory.OBSTACLE,   // cômoda
        6 to ObjectCategory.OBSTACLE,   // escada descendo ⚠️
        7 to ObjectCategory.OBSTACLE,   // flor
        11 to ObjectCategory.OBSTACLE,  // vaso
        12 to ObjectCategory.OBSTACLE,  // buraco ⚠️
        15 to ObjectCategory.OBSTACLE,  // sofá
        16 to ObjectCategory.OBSTACLE,  // escada ⚠️
        17 to ObjectCategory.OBSTACLE,  // mesa
        18 to ObjectCategory.OBSTACLE,  // torneira
        19 to ObjectCategory.OBSTACLE,  // árvore
        20 to ObjectCategory.OBSTACLE,  // tronco ⚠️
        22 to ObjectCategory.OBSTACLE,  // escada subindo ⚠️
        23 to ObjectCategory.OBSTACLE,  // mochila
        28 to ObjectCategory.OBSTACLE,  // porta
        29 to ObjectCategory.OBSTACLE,  // lixeira grande
        30 to ObjectCategory.OBSTACLE,  // bolsa
        31 to ObjectCategory.OBSTACLE,  // folhas
        34 to ObjectCategory.OBSTACLE   // sofá (2)
    )

    // Mapa ativo — selecionado automaticamente
    private var categoryMap = cocoCategoryMap

    // ── Filtros de plausibilidade por classe ──
    // ── Filtros COCO (não se aplicam ao modelo ClaraVis fine-tuned) ──
    // Classes que são geralmente PEQUENAS — se o box ocupa >40% da tela, é falso positivo
    private val cocoSmallObjectClasses = setOf(
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

    // ClaraVis v1: objetos pequenos — sapato, chinelo, celular, papel, flor, folhas
    private val claravisSmallObjectClasses = setOf(
        3,   // livro
        7,   // flor
        9,   // papel
        13,  // sapato
        14,  // chinelo
        18,  // torneira
        31,  // folhas
        32   // celular
    )

    // ClaraVis v2: objetos tipicamente pequenos
    private val claravisV2SmallObjectClasses = setOf(
        20,  // bollard
        18,  // hidrante
        26,  // tampa de esgoto
    )

    private var smallObjectClasses = cocoSmallObjectClasses

    // Classes de eletrodomésticos grandes que são confundidas com superfícies planas
    private val cocoConfusionProneClasses = setOf(
        62, // televisão
        72, // geladeira
        57, // sofá
        59, // cama
        60, // mesa
        68, // micro-ondas
        69, // forno
    )

    // ClaraVis v1: cama, sofá, mesa podem ser confundidos com pisos/paredes
    private val claravisConfusionProneClasses = setOf(
        0,   // cama
        5,   // cômoda
        15,  // sofá
        34   // sofá (2)
    )

    // ClaraVis v2: classes que podem ser confundidas
    private val claravisV2ConfusionProneClasses = setOf(
        14,  // calçada (pode cobrir tela inteira)
        27,  // dano no pavimento
        31,  // sofá
        32,  // cama
    )

    private var confusionProneClasses = cocoConfusionProneClasses

    // Classes irrelevantes para navegação se muito pequenas
    private val cocoIrrelevantTinyClasses = setOf(
        63, 64, 65, 66, 67, 27, 29, 30, 31, 33, 34, 35, 38, 40, 42, 43, 44, 73, 76, 78, 79
    )

    // ClaraVis: todas as classes são relevantes para navegação, poucas irrelevantes
    private val claravisIrrelevantTinyClasses = setOf(
        3,   // livro
        9,   // papel
        32   // celular
    )

    private var irrelevantTinyClasses = cocoIrrelevantTinyClasses
    private val MIN_RELEVANT_AREA = 0.02f  // Objetos irrelevantes < 2% da tela = ignorar

    // ── Classes com poucas amostras no treino #1 — alto risco de falso positivo ──
    // Essas classes tinham <50 imagens no dataset e geram muitos erros
    private val claravisUnreliableClasses = setOf(
        7,   // flor (6 imagens no treino)
        9,   // papel (poucas, confunde com qualquer superfície clara)
        6,   // escada descendo (16 imagens — confunde com subindo)
        31,  // folhas (poucas, confunde com texturas)
        14,  // chinelo
        11,  // vaso
    )
    // Threshold mais alto para classes não-confiáveis
    private val UNRELIABLE_CLASS_THRESHOLD = 0.60f

    companion object {
        private const val TAG = "ClaraVis-Detector"
        private const val CONFIDENCE_THRESHOLD = 0.35f   // Baixo para detectar mais, TTS rápido compensa
        private const val HIGH_CONF_THRESHOLD = 0.50f    // Para classes propensas a confusão
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
            "claravis_v2_model_416.tflite" to "asset", // ClaraVis v2 — 37 classes, treino #2
            "claravis_model_416.tflite" to "asset",    // ClaraVis v1 — 36 classes, fallback
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
                    detectModelType(interp)
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
                detectModelType(interp)
                Log.i(TAG, "Loaded model from assets: $filename, inputSize=$inputSize")
                logModelInfo(interp)
                return interp
            } catch (e: Exception) {
                Log.d(TAG, "Model $filename not found in assets, trying next...")
            }
        }

        throw RuntimeException("Nenhum modelo YOLO encontrado!")
    }

    /**
     * Auto-detecta o modelo pelo output shape: [1, 4+numClasses, numBoxes]
     * ClaraVis v2: 4+37=41, ClaraVis v1: 4+36=40, COCO: 4+80=84
     */
    private fun detectModelType(interp: Interpreter) {
        val outputShape = interp.getOutputTensor(0).shape()
        val dim1 = outputShape[1]
        val dim2 = outputShape[2]
        val numClassesPlusFour = minOf(dim1, dim2)

        val numClasses = numClassesPlusFour - 4
        Log.i(TAG, "Detected $numClasses classes in model (output shape: ${outputShape.contentToString()})")

        when (numClasses) {
            37 -> {
                labels = claravisV2Labels
                categoryMap = claravisV2CategoryMap
                smallObjectClasses = claravisV2SmallObjectClasses
                confusionProneClasses = claravisV2ConfusionProneClasses
                irrelevantTinyClasses = emptySet()
                Log.i(TAG, "Using ClaraVis v2 labels (37 classes, treino #2)")
            }
            36 -> {
                labels = claravisLabels
                categoryMap = claravisCategoryMap
                smallObjectClasses = claravisSmallObjectClasses
                confusionProneClasses = claravisConfusionProneClasses
                irrelevantTinyClasses = claravisIrrelevantTinyClasses
                Log.i(TAG, "Using ClaraVis v1 labels (36 classes, treino #1)")
            }
            else -> {
                labels = cocoLabels
                categoryMap = cocoCategoryMap
                smallObjectClasses = cocoSmallObjectClasses
                confusionProneClasses = cocoConfusionProneClasses
                irrelevantTinyClasses = cocoIrrelevantTinyClasses
                Log.i(TAG, "Using COCO labels ($numClasses classes)")
            }
        }
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

            if (maxScore < CONFIDENCE_THRESHOLD * 0.7f) continue  // Pre-filter rápido

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

            // ── Filtro 0: Box minúsculo = ruído ──
            if (boxArea < 0.003f) continue  // < 0.3% da tela = certamente ruído

            // ── Filtro 0b: Classes não-confiáveis precisam confiança muito alta ──
            if (labels === claravisLabels && maxClassId in claravisUnreliableClasses && maxScore < UNRELIABLE_CLASS_THRESHOLD) {
                continue
            }

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
