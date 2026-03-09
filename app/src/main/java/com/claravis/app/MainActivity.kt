package com.claravis.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.YuvImage
import java.io.ByteArrayOutputStream
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.util.Size
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.claravis.app.databinding.ActivityMainBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.Locale
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityMainBinding
    private var camera: Camera? = null
    private var nightModeEnabled = false
    private var objectDetector: ObjectDetector? = null

    // Exposure range do dispositivo
    private var exposureMin = 0
    private var exposureMax = 0

    // Valores atuais de ajuste
    private var currentBrightness = 1.0f
    private var currentContrast = 1.0f
    private var currentExposure = 0

    // FPS tracking
    private var frameCount = 0
    private var lastFpsTime = System.currentTimeMillis()

    // TTS
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var ttsEnabled = true
    private var ttsInterval = 3000L
    private var lastTtsTime = 0L
    private var lastDetections: List<Detection> = emptyList()

    // UI
    private var buttonsVisible = true

    // Flash / Lanterna
    private var flashEnabled = false

    // Zoom
    private var currentZoom = 1.0f
    private var maxZoom = 1.0f

    // Scene Analyzer (Cloud AI — desativado por enquanto)
    private var sceneAnalyzer: SceneAnalyzer? = null
    private var lastSceneAnalysisTime = System.currentTimeMillis()
    private var sceneAnalysisInterval = 15000L
    @Volatile private var cloudFailCount = 0

    // Local VLM (offline, on-device)
    private var localVLM: LocalVLM? = null
    private var lastLocalVLMTime = System.currentTimeMillis()
    private var localVLMInterval = 30000L

    // VLM status (apenas display — YOLO sempre ativo)
    @Volatile private var vlmActive = false

    // Acelerômetro
    private var cameraAngleDetector: CameraAngleDetector? = null

    // Sistema adaptativo
    private var adaptiveConfidence: AdaptiveConfidence? = null

    // OCR
    private val textRecognizer by lazy { TextRecognition.getClient(TextRecognizerOptions.Builder().build()) }
    private var lastOcrTime = 0L
    private var lastOcrText: String? = null
    private var ocrInterval = 5000L
    private var lastOcrAnnouncedText: String? = null

    // Movement detection — detecta objetos se aproximando
    private var previousDetections: List<Detection> = emptyList()
    private var lastFrameCaptureTime = 0L

    // Voice interaction — reconhecimento de fala
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    private var lastCapturedFrame: Bitmap? = null  // Frame capturado para pergunta por voz

    private val analysisExecutor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val AUDIO_PERMISSION_CODE = 101
        private const val TAG = "ClaraVis"
    }

    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        )

        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "ClaraVis::NavigationWakeLock"
        )
        wakeLock?.acquire()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TTS
        tts = TextToSpeech(this, this)

        // Detector YOLO
        try {
            objectDetector = ObjectDetector(this)
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao carregar modelo IA: ${e.message}", Toast.LENGTH_LONG).show()
        }

        // Scene Analyzer (desativado por enquanto, mas mantido para futuro)
        sceneAnalyzer = SceneAnalyzer(this)
        sceneAnalyzer?.onSceneDescription = { description ->
            if (description.isNotBlank()) cloudFailCount = 0
            runOnUiThread { speakScene(description) }
        }
        sceneAnalyzer?.onError = { cloudFailCount++ }

        // Local VLM — DESATIVADO (consome CPU/RAM, atrapalha YOLO no Helio G85)
        // Local VLM — DESATIVADO
        // localVLM = LocalVLM(this)
        // localVLM?.onSceneDescription = { description ->
        //     runOnUiThread { speakScene(description) }
        // }
        // localVLM?.onInferenceStart = {
        //     vlmActive = true
        //     Log.i(TAG, "VLM inference started (YOLO continues)")
        // }
        // localVLM?.onInferenceEnd = {
        //     vlmActive = false
        //     lastLocalVLMTime = System.currentTimeMillis()
        //     Log.i(TAG, "VLM inference ended")
        // }
        Log.i(TAG, "Local VLM: DESATIVADO")

        // Acelerômetro
        cameraAngleDetector = CameraAngleDetector(this)
        cameraAngleDetector?.start()

        // Adaptive confidence
        adaptiveConfidence = AdaptiveConfidence(this)

        // Voice interaction
        initSpeechRecognizer()

        if (hasCameraPermission()) {
            startCamera()
        } else {
            requestCameraPermission()
        }

        setupControls()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraAngleDetector?.stop()
        objectDetector?.close()
        analysisExecutor.shutdown()
        tts?.stop()
        tts?.shutdown()
        textRecognizer.close()
        speechRecognizer?.destroy()
        lastCapturedFrame?.recycle()
        wakeLock?.let { if (it.isHeld) it.release() }
    }

    // ── TTS ────────────────────────────────────────────────

    override fun onInit(status: Int) {
        Log.i(TAG, "TTS onInit status=$status")
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("pt", "BR"))
            ttsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
            if (!ttsReady) {
                tts?.setLanguage(Locale("pt"))
                ttsReady = true
            }
            tts?.setSpeechRate(2.0f)  // Velocidade 2x para reagir antes
            Log.i(TAG, "TTS ready=$ttsReady")
            tts?.speak("Clara Vis ativada", TextToSpeech.QUEUE_FLUSH, null, "welcome")
        }
    }

    private var lastSceneSpeakTime = 0L

    // Classes de perigo imediato — bypass do cooldown normal
    private val urgentLabels = setOf(
        "escada", "buraco", "bueiro", "poça", "meio-fio",
        "dano no pavimento", "tampa de esgoto", "barreira",
        // v1 labels
        "escada descendo", "escada subindo"
    )
    private val URGENT_TTS_INTERVAL = 800L  // 0.8s para perigos
    private var lastUrgentLabel = ""

    private fun speakDetections(detections: List<Detection>, approaching: Set<Int> = emptySet()) {
        if (!ttsReady || !ttsEnabled || detections.isEmpty()) return

        val now = System.currentTimeMillis()

        // Verificar se há perigo urgente
        val hasUrgent = detections.any { it.label in urgentLabels }
        val hasApproaching = approaching.isNotEmpty()

        // Cooldown reduzido para perigos urgentes ou objetos se aproximando
        val effectiveInterval = when {
            hasUrgent || hasApproaching -> URGENT_TTS_INTERVAL
            else -> ttsInterval
        }

        if (now - lastTtsTime < effectiveInterval) return
        // Scene description bloqueia TTS normal, mas NÃO urgente
        if (!hasUrgent && now - lastSceneSpeakTime < 15000L) return
        lastTtsTime = now

        // Prioridade: perigo urgente > se aproximando > obstáculos > pessoas > veículos > animais
        val sorted = detections.withIndex().sortedWith(compareBy {
            when {
                it.value.label in urgentLabels -> -2   // Perigo imediato
                it.index in approaching -> -1          // Se aproximando
                it.value.category == ObjectCategory.OBSTACLE -> 0
                it.value.category == ObjectCategory.PERSON -> 1
                it.value.category == ObjectCategory.VEHICLE -> 2
                it.value.category == ObjectCategory.ANIMAL -> 3
                else -> 4
            }
        })

        val toAnnounce = sorted.take(3)
        val phrases = toAnnounce.map { (index, det) ->
            val position = objectDetector?.getPosition(det) ?: "à frente"
            val isUrgent = det.label in urgentLabels
            val isApproaching = index in approaching
            val prefix = when {
                isUrgent && det.label != lastUrgentLabel -> "atenção, "
                isApproaching -> "atenção, "
                else -> ""
            }
            if (isUrgent) lastUrgentLabel = det.label
            "$prefix${det.label} $position"
        }

        val speech = phrases.joinToString(". ")
        tts?.speak(speech, TextToSpeech.QUEUE_FLUSH, null, "det_$now")
    }

    private fun speakScene(description: String) {
        if (!ttsReady || !ttsEnabled || description.isBlank()) return
        lastSceneSpeakTime = System.currentTimeMillis()
        tts?.speak(description, TextToSpeech.QUEUE_FLUSH, null, "scene_${System.currentTimeMillis()}")
        Log.i(TAG, "TTS scene: ${description.take(80)}")
    }

    // ── Movement Detection ──────────────────────────────────

    private fun detectApproaching(current: List<Detection>): Set<Int> {
        val approaching = mutableSetOf<Int>()
        for ((i, det) in current.withIndex()) {
            // Procurar mesmo objeto no frame anterior (mesmo classId + IOU > 0.2)
            val prev = previousDetections.find { prev ->
                prev.classId == det.classId && iou(prev.boundingBox, det.boundingBox) > 0.2f
            }
            if (prev != null) {
                val prevArea = prev.boundingBox.width() * prev.boundingBox.height()
                val currArea = det.boundingBox.width() * det.boundingBox.height()
                // Se o box cresceu >20% e ocupa >5% da tela = se aproximando
                if (currArea > prevArea * 1.20f && currArea > 0.05f) {
                    approaching.add(i)
                }
            }
        }
        previousDetections = current
        return approaching
    }

    private fun iou(a: RectF, b: RectF): Float {
        val interLeft = maxOf(a.left, b.left)
        val interTop = maxOf(a.top, b.top)
        val interRight = minOf(a.right, b.right)
        val interBottom = minOf(a.bottom, b.bottom)
        val interArea = maxOf(0f, interRight - interLeft) * maxOf(0f, interBottom - interTop)
        val aArea = a.width() * a.height()
        val bArea = b.width() * b.height()
        return interArea / (aArea + bArea - interArea + 1e-5f)
    }

    // ── Voice Interaction ───────────────────────────────────

    private fun initSpeechRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Log.w(TAG, "Speech recognition not available")
            return
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                runOnUiThread {
                    binding.overlayView.listeningStatus = "🎤 Ouvindo..."
                    binding.overlayView.postInvalidate()
                    binding.btnMic.setTextColor(0xFFFF1744.toInt())
                }
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                runOnUiThread {
                    binding.overlayView.listeningStatus = "⏳ Processando..."
                    binding.overlayView.postInvalidate()
                }
            }
            override fun onError(error: Int) {
                isListening = false
                runOnUiThread {
                    binding.overlayView.listeningStatus = null
                    binding.overlayView.postInvalidate()
                    binding.btnMic.setTextColor(0xFF00E676.toInt())
                }
                val msg = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "Não entendi. Tente novamente."
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Não ouvi nada."
                    else -> null
                }
                if (msg != null) tts?.speak(msg, TextToSpeech.QUEUE_FLUSH, null, "voice_err")
            }
            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val spokenText = matches?.firstOrNull()
                runOnUiThread {
                    binding.overlayView.listeningStatus = null
                    binding.overlayView.postInvalidate()
                    binding.btnMic.setTextColor(0xFF00E676.toInt())
                }
                if (spokenText != null) {
                    Log.i(TAG, "Voice: $spokenText")
                    handleVoiceCommand(spokenText)
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun startListening() {
        if (isListening) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), AUDIO_PERMISSION_CODE)
            return
        }

        // Capturar frame atual para a pergunta
        lastCapturedFrame?.recycle()
        lastCapturedFrame = null

        isListening = true
        tts?.stop()  // Parar qualquer TTS em andamento

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1000L)
        }
        speechRecognizer?.startListening(intent)
    }

    private fun handleVoiceCommand(text: String) {
        Log.i(TAG, "Processing voice: $text")

        // Capturar o frame mais recente para enviar ao VLM
        val frame = lastCapturedFrame
        if (frame != null && !frame.isRecycled) {
            val copy = frame.copy(Bitmap.Config.ARGB_8888, false)
            tts?.speak("Analisando...", TextToSpeech.QUEUE_FLUSH, null, "voice_wait")
            localVLM?.answerQuestion(copy, text) { answer ->
                runOnUiThread {
                    speakScene(answer)
                }
            }
        } else {
            tts?.speak("Não consegui capturar a imagem. Tente novamente.", TextToSpeech.QUEUE_FLUSH, null, "voice_no_frame")
        }
    }

    // ── Camera ─────────────────────────────────────────────

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera()
                } else {
                    Toast.makeText(this, "Permissão de câmera necessária", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
            AUDIO_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startListening()
                }
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(binding.previewView.surfaceProvider) }

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(640, 480))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(analysisExecutor) { imageProxy ->
                processFrame(imageProxy)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)

                // Exposure range
                val exposureState = camera?.cameraInfo?.exposureState
                if (exposureState != null && exposureState.isExposureCompensationSupported) {
                    exposureMin = exposureState.exposureCompensationRange.lower
                    exposureMax = exposureState.exposureCompensationRange.upper
                }

                // Zoom range
                val zoomState = camera?.cameraInfo?.zoomState?.value
                maxZoom = zoomState?.maxZoomRatio ?: 1.0f
                Log.i(TAG, "Camera started. Exposure: $exposureMin..$exposureMax, MaxZoom: $maxZoom")

            } catch (e: Exception) {
                Toast.makeText(this, "Erro ao iniciar câmera: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private var loggedFrameInfo = false

    private fun processFrame(imageProxy: ImageProxy) {
        try {
            val bitmap = imageProxyToBitmap(imageProxy)
            if (bitmap != null) {
                val orientation = cameraAngleDetector?.orientation ?: CameraOrientation.LOOKING_FORWARD
                val now = System.currentTimeMillis()

                // Salvar frame para voice interaction (a cada 500ms para reduzir GC)
                if (now - lastFrameCaptureTime > 500) {
                    lastCapturedFrame?.recycle()
                    lastCapturedFrame = bitmap.copy(Bitmap.Config.ARGB_8888, false)
                    lastFrameCaptureTime = now
                }

                // ═══ YOLO — sempre ativo, mesmo durante VLM ═══
                val detector = objectDetector
                if (detector != null) {
                    val rawDetections = detector.detect(bitmap)

                    val detections = rawDetections.filter { det ->
                        val adaptiveThreshold = adaptiveConfidence?.getThreshold(det.classId, orientation) ?: 0.35f
                        det.confidence >= adaptiveThreshold
                    }

                    adaptiveConfidence?.recordFrame(detections)

                    // Movement detection
                    val approaching = detectApproaching(detections)

                    lastDetections = detections
                    runOnUiThread {
                        binding.overlayView.approachingIndices = approaching
                        binding.overlayView.updateDetections(detections)
                        speakDetections(detections, approaching)
                    }
                }

                // FPS tracking (sempre, mesmo com YOLO pausado)
                frameCount++
                val elapsed = now - lastFpsTime
                if (elapsed >= 1000) {
                    val fps = frameCount * 1000f / elapsed
                    val angleLabel = cameraAngleDetector?.getOrientationLabel() ?: "?"
                    val pauseLabel = if (vlmActive) " | VLM" else ""
                    runOnUiThread {
                        binding.overlayView.fps = fps
                        binding.overlayView.orientationLabel = "$angleLabel$pauseLabel"
                    }
                    frameCount = 0
                    lastFpsTime = now
                    adaptiveConfidence?.applyDecay()
                }

                // OCR — sempre ativo
                if (now - lastOcrTime > ocrInterval) {
                    lastOcrTime = now
                    val ocrCopy = bitmap.copy(Bitmap.Config.ARGB_8888, false)
                    runOCR(ocrCopy)
                }

                // VLM local — DESATIVADO (consome CPU/RAM, atrapalha YOLO)
                // val vlm = localVLM
                // if (vlm != null && vlm.isAvailable() && !vlm.isRunning && now - lastLocalVLMTime > localVLMInterval) {
                //     lastLocalVLMTime = now
                //     val copy = bitmap.copy(Bitmap.Config.ARGB_8888, false)
                //     vlm.analyzeScene(copy, lastDetections, orientation)
                // }

                bitmap.recycle()
            }
        } catch (e: Exception) {
            Log.e(TAG, "processFrame error: ${e.message}")
        } finally {
            imageProxy.close()
        }
    }

    /** OCR — reconhece texto em placas */
    private fun runOCR(bitmap: Bitmap) {
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            textRecognizer.process(image)
                .addOnSuccessListener { result ->
                    bitmap.recycle()
                    if (result.text.isNotBlank()) {
                        val cleanText = result.text.trim().replace("\n", " ").take(200)
                        lastOcrText = cleanText
                        Log.i(TAG, "OCR: $cleanText")
                        announceRelevantText(cleanText)
                    } else {
                        lastOcrText = null
                    }
                }
                .addOnFailureListener { bitmap.recycle() }
        } catch (e: Exception) {
            bitmap.recycle()
        }
    }

    private fun announceRelevantText(text: String) {
        if (!ttsReady || !ttsEnabled) return
        val lowerText = text.lowercase()
        val relevantKeywords = listOf(
            "banheiro", "wc", "saída", "saida", "exit", "entrada",
            "elevador", "escada", "stairs", "perigo", "danger", "cuidado",
            "proibido", "pare", "stop", "andar", "piso",
            "recepção", "emergência", "aberto", "fechado"
        )
        val found = relevantKeywords.filter { lowerText.contains(it) }
        if (found.isNotEmpty() && text != lastOcrAnnouncedText) {
            lastOcrAnnouncedText = text
            tts?.speak("Placa: $text", TextToSpeech.QUEUE_ADD, null, "ocr_${System.currentTimeMillis()}")
        }
    }

    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        val width = imageProxy.width
        val height = imageProxy.height

        if (!loggedFrameInfo) {
            loggedFrameInfo = true
            Log.i(TAG, "Frame: ${width}x${height}, format=${imageProxy.format}, rotation=${imageProxy.imageInfo.rotationDegrees}")
        }

        val yPlane = imageProxy.planes[0]
        val uPlane = imageProxy.planes[1]
        val vPlane = imageProxy.planes[2]

        val yBuffer = yPlane.buffer
        val uBuffer = uPlane.buffer
        val vBuffer = vPlane.buffer

        yBuffer.rewind()
        uBuffer.rewind()
        vBuffer.rewind()

        val yRowStride = yPlane.rowStride
        val uvRowStride = uPlane.rowStride
        val uvPixelStride = uPlane.pixelStride

        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val yIndex = y * yRowStride + x
                val uvIndex = (y / 2) * uvRowStride + (x / 2) * uvPixelStride

                val yVal = (yBuffer.get(yIndex).toInt() and 0xFF)
                val uVal = (uBuffer.get(uvIndex).toInt() and 0xFF) - 128
                val vVal = (vBuffer.get(uvIndex).toInt() and 0xFF) - 128

                var r = yVal + (1.370705f * vVal).toInt()
                var g = yVal - (0.337633f * uVal).toInt() - (0.698001f * vVal).toInt()
                var b = yVal + (1.732446f * uVal).toInt()

                r = r.coerceIn(0, 255)
                g = g.coerceIn(0, 255)
                b = b.coerceIn(0, 255)

                pixels[y * width + x] = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
            }
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)

        val rotation = imageProxy.imageInfo.rotationDegrees
        if (rotation != 0) {
            val matrix = Matrix()
            matrix.postRotate(rotation.toFloat())
            val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()

            val camAspect = rotated.width.toFloat() / rotated.height.toFloat()
            mainHandler.post { binding.overlayView.cameraAspectRatio = camAspect }

            return rotated
        }

        val camAspect = bitmap.width.toFloat() / bitmap.height.toFloat()
        mainHandler.post { binding.overlayView.cameraAspectRatio = camAspect }

        return bitmap
    }

    // ── UI Controls ────────────────────────────────────────

    private fun setupControls() {
        // Night mode
        binding.btnNightMode.setOnClickListener {
            nightModeEnabled = !nightModeEnabled
            if (nightModeEnabled) {
                activateNightMode()
                binding.btnNightMode.text = "NIGHT ON"
                binding.btnNightMode.setTextColor(0xFF000000.toInt())
                binding.btnNightMode.setBackgroundColor(0xFF00E676.toInt())
            } else {
                deactivateNightMode()
                binding.btnNightMode.text = "NIGHT"
                binding.btnNightMode.setTextColor(0xFF00E676.toInt())
                binding.btnNightMode.setBackgroundColor(0x00000000)
            }
        }

        // Flash / Lanterna
        binding.btnFlash.setOnClickListener {
            flashEnabled = !flashEnabled
            camera?.cameraControl?.enableTorch(flashEnabled)
            if (flashEnabled) {
                binding.btnFlash.setTextColor(0xFFFFD600.toInt())
            } else {
                binding.btnFlash.setTextColor(0xFF00E676.toInt())
            }
        }

        // TTS toggle
        binding.btnTts.setOnClickListener {
            ttsEnabled = !ttsEnabled
            if (ttsEnabled) {
                binding.btnTts.text = "VOZ"
                binding.btnTts.setTextColor(0xFF00E676.toInt())
            } else {
                binding.btnTts.text = "MUDO"
                binding.btnTts.setTextColor(0xFF888888.toInt())
                tts?.stop()
            }
        }

        // Settings
        binding.btnSettings.setOnClickListener { openSettings() }

        // Mic — interação por voz
        binding.btnMic.setOnClickListener { startListening() }

        // Zoom +/-
        binding.btnZoomIn.setOnClickListener {
            currentZoom = (currentZoom + 0.5f).coerceAtMost(maxZoom)
            camera?.cameraControl?.setZoomRatio(currentZoom)
        }
        binding.btnZoomOut.setOnClickListener {
            currentZoom = (currentZoom - 0.5f).coerceAtLeast(1.0f)
            camera?.cameraControl?.setZoomRatio(currentZoom)
        }

        // Toque na tela — ocultar/mostrar botões
        binding.previewView.setOnClickListener {
            buttonsVisible = !buttonsVisible
            val vis = if (buttonsVisible) View.VISIBLE else View.GONE
            binding.buttonsPanel.visibility = vis
            binding.btnMic.visibility = vis
            binding.zoomPanel.visibility = vis
        }
    }

    private fun openSettings() {
        val sheet = SettingsSheet()

        sheet.overlayEnabled = binding.overlayView.overlayEnabled
        sheet.fpsEnabled = binding.overlayView.showFps
        sheet.currentFontSize = binding.overlayView.labelSize.toInt()
        sheet.exposureMin = exposureMin
        sheet.exposureMax = exposureMax
        sheet.currentExposure = currentExposure
        sheet.currentBrightness = (currentBrightness * 100).toInt()
        sheet.currentContrast = (currentContrast * 100).toInt()
        sheet.nightModeOn = nightModeEnabled
        sheet.ttsEnabled = ttsEnabled
        sheet.ttsInterval = (ttsInterval / 1000).toInt()

        sheet.onOverlayToggle = { binding.overlayView.overlayEnabled = it }
        sheet.onFpsToggle = { binding.overlayView.showFps = it }
        sheet.onFontSizeChange = { binding.overlayView.labelSize = it }
        sheet.onExposureChange = { currentExposure = it; camera?.cameraControl?.setExposureCompensationIndex(it) }
        sheet.onBrightnessChange = { currentBrightness = it / 100f; applyColorFilter() }
        sheet.onContrastChange = { currentContrast = it / 100f; applyColorFilter() }
        sheet.onNightModeClick = {
            nightModeEnabled = !nightModeEnabled
            if (nightModeEnabled) {
                activateNightMode()
                binding.btnNightMode.text = "NIGHT ON"
                binding.btnNightMode.setTextColor(0xFF000000.toInt())
                binding.btnNightMode.setBackgroundColor(0xFF00E676.toInt())
            } else {
                deactivateNightMode()
                binding.btnNightMode.text = "NIGHT"
                binding.btnNightMode.setTextColor(0xFF00E676.toInt())
                binding.btnNightMode.setBackgroundColor(0x00000000)
            }
        }
        sheet.onTtsToggle = { enabled ->
            ttsEnabled = enabled
            binding.btnTts.text = if (enabled) "VOZ" else "MUDO"
            binding.btnTts.setTextColor(if (enabled) 0xFF00E676.toInt() else 0xFF888888.toInt())
            if (!enabled) tts?.stop()
        }
        sheet.onTtsIntervalChange = { ttsInterval = it * 1000L }

        sheet.show(supportFragmentManager, "settings")
    }

    // ── Night Mode ─────────────────────────────────────────

    private fun activateNightMode() {
        camera?.cameraControl?.setExposureCompensationIndex(exposureMax)
        currentExposure = exposureMax
        currentBrightness = 1.5f
        currentContrast = 1.2f
        applyColorFilter()
    }

    private fun deactivateNightMode() {
        camera?.cameraControl?.setExposureCompensationIndex(0)
        currentExposure = 0
        currentBrightness = 1.0f
        currentContrast = 1.0f
        applyColorFilter()
    }

    private fun applyColorFilter() {
        val brightnessOffset = (currentBrightness - 1.0f) * 255f
        val contrast = currentContrast
        val contrastOffset = 128f * (1f - contrast)

        val colorMatrix = ColorMatrix(
            floatArrayOf(
                contrast, 0f, 0f, 0f, brightnessOffset + contrastOffset,
                0f, contrast, 0f, 0f, brightnessOffset + contrastOffset,
                0f, 0f, contrast, 0f, brightnessOffset + contrastOffset,
                0f, 0f, 0f, 1f, 0f
            )
        )

        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        binding.previewView.setLayerType(View.LAYER_TYPE_HARDWARE, paint)
    }
}
