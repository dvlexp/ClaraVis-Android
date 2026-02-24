package com.claravis.app

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
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
import java.nio.ByteBuffer
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
    private var ttsInterval = 3000L // ms
    private var lastTtsTime = 0L
    private var lastDetections: List<Detection> = emptyList()

    // UI
    private var buttonsVisible = true

    // Scene Analyzer (Cloud AI)
    private var sceneAnalyzer: SceneAnalyzer? = null
    private var lastSceneAnalysisTime = 0L
    private var sceneAnalysisInterval = 8000L  // 8 segundos

    // Local VLM (offline, on-device)
    private var localVLM: LocalVLM? = null
    private var lastLocalVLMTime = 0L
    private var localVLMInterval = 10000L  // 10 segundos (modelo local é mais lento)

    // Acelerômetro — detecta orientação da câmera (chão/frente/cima)
    private var cameraAngleDetector: CameraAngleDetector? = null

    // Sistema adaptativo — ajusta thresholds com o uso
    private var adaptiveConfidence: AdaptiveConfidence? = null

    // OCR — reconhecimento de texto em placas
    private val textRecognizer by lazy { TextRecognition.getClient(TextRecognizerOptions.Builder().build()) }
    private var lastOcrTime = 0L
    private var lastOcrText: String? = null
    private var ocrInterval = 5000L  // OCR a cada 5 segundos
    private var lastOcrAnnouncedText: String? = null

    private val analysisExecutor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val TAG = "ClaraVis"
    }

    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Manter tela ligada e visível mesmo sobre lockscreen
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        )

        // Wake lock para manter CPU ativa (app de navegação precisa rodar continuamente)
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "ClaraVis::NavigationWakeLock"
        )
        wakeLock?.acquire()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar TTS
        tts = TextToSpeech(this, this)

        // Inicializar detector
        try {
            objectDetector = ObjectDetector(this)
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao carregar modelo IA: ${e.message}", Toast.LENGTH_LONG).show()
        }

        // Inicializar Scene Analyzer (Cloud AI)
        sceneAnalyzer = SceneAnalyzer(this)
        sceneAnalyzer?.onSceneDescription = { description ->
            runOnUiThread {
                speakScene(description)
            }
        }

        // Inicializar Local VLM (offline, on-device)
        localVLM = LocalVLM(this)
        localVLM?.onSceneDescription = { description ->
            runOnUiThread {
                speakScene(description)
            }
        }
        Log.i(TAG, "Local VLM: ${localVLM?.getStatus()}")

        // Inicializar detector de ângulo (acelerômetro)
        cameraAngleDetector = CameraAngleDetector(this)
        cameraAngleDetector?.start()

        // Inicializar sistema adaptativo de confiança
        adaptiveConfidence = AdaptiveConfidence(this)

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
        wakeLock?.let { if (it.isHeld) it.release() }
    }

    // ── TTS ────────────────────────────────────────────────

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("pt", "BR"))
            ttsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
            if (!ttsReady) {
                tts?.setLanguage(Locale("pt"))
                ttsReady = true
            }
            tts?.setSpeechRate(1.1f)
            Log.i(TAG, "TTS initialized, ready=$ttsReady")
        } else {
            Log.e(TAG, "TTS init failed: $status")
        }
    }

    private fun speakDetections(detections: List<Detection>) {
        if (!ttsReady || !ttsEnabled || detections.isEmpty()) return

        val now = System.currentTimeMillis()
        if (now - lastTtsTime < ttsInterval) return
        lastTtsTime = now

        // Prioridade: obstáculos > pessoas > veículos > animais > outros
        val sorted = detections.sortedWith(compareBy {
            when (it.category) {
                ObjectCategory.OBSTACLE -> 0
                ObjectCategory.PERSON -> 1
                ObjectCategory.VEHICLE -> 2
                ObjectCategory.ANIMAL -> 3
                ObjectCategory.OTHER -> 4
            }
        })

        val toAnnounce = sorted.take(3)
        val phrases = toAnnounce.map { det ->
            val position = objectDetector?.getPosition(det) ?: "à frente"
            "${det.label} $position"
        }

        val speech = phrases.joinToString(". ")
        tts?.speak(speech, TextToSpeech.QUEUE_FLUSH, null, "claravis_det_${now}")
    }

    private fun speakScene(description: String) {
        if (!ttsReady || !ttsEnabled || description.isBlank()) return
        // Fala a descrição da cena da IA (na fila, após detecções)
        tts?.speak(description, TextToSpeech.QUEUE_ADD, null, "claravis_scene_${System.currentTimeMillis()}")
    }

    // ── Camera ─────────────────────────────────────────────

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissão de câmera necessária", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

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
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalysis
                )

                // Ler range de exposição do hardware
                val exposureState = camera?.cameraInfo?.exposureState
                if (exposureState != null && exposureState.isExposureCompensationSupported) {
                    exposureMin = exposureState.exposureCompensationRange.lower
                    exposureMax = exposureState.exposureCompensationRange.upper
                }

                Log.i(TAG, "Camera started. Exposure range: $exposureMin..$exposureMax")

            } catch (e: Exception) {
                Toast.makeText(this, "Erro ao iniciar câmera: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private var loggedFrameInfo = false

    private fun processFrame(imageProxy: ImageProxy) {
        val detector = objectDetector ?: run {
            imageProxy.close()
            return
        }

        try {
            val bitmap = imageProxyToBitmap(imageProxy)
            if (bitmap != null) {
                // Obter orientação atual da câmera via acelerômetro
                val orientation = cameraAngleDetector?.orientation ?: CameraOrientation.LOOKING_FORWARD

                val rawDetections = detector.detect(bitmap)

                // Aplicar filtro adaptativo baseado na orientação e aprendizado
                val detections = rawDetections.filter { det ->
                    val adaptiveThreshold = adaptiveConfidence?.getThreshold(det.classId, orientation) ?: 0.35f
                    det.confidence >= adaptiveThreshold
                }

                // Registrar detecções para aprendizado adaptativo
                adaptiveConfidence?.recordFrame(detections)

                // FPS tracking
                frameCount++
                val now = System.currentTimeMillis()
                val elapsed = now - lastFpsTime
                if (elapsed >= 1000) {
                    val fps = frameCount * 1000f / elapsed
                    val angleLabel = cameraAngleDetector?.getOrientationLabel() ?: "?"
                    runOnUiThread {
                        binding.overlayView.fps = fps
                        binding.overlayView.orientationLabel = angleLabel
                    }
                    frameCount = 0
                    lastFpsTime = now

                    // Decay adaptativo a cada segundo
                    adaptiveConfidence?.applyDecay()
                }

                // Atualizar detecções e TTS
                lastDetections = detections
                runOnUiThread {
                    binding.overlayView.updateDetections(detections)
                    speakDetections(detections)
                }

                // OCR — reconhecer texto de placas/sinalizações (a cada 5s)
                if (now - lastOcrTime > ocrInterval) {
                    lastOcrTime = now
                    runOCR(bitmap)
                }

                // Análise de cena — prioridade: Local VLM > Cloud AI
                val useLocalVLM = localVLM != null && localVLM!!.isAvailable()
                val useCloudAI = !useLocalVLM && sceneAnalyzer != null && sceneAnalyzer!!.isConfigured()

                if (useLocalVLM && now - lastLocalVLMTime > localVLMInterval) {
                    lastLocalVLMTime = now
                    val copy = bitmap.copy(Bitmap.Config.ARGB_8888, false)
                    localVLM?.analyzeScene(copy, detections, orientation)
                } else if (useCloudAI && now - lastSceneAnalysisTime > sceneAnalysisInterval) {
                    lastSceneAnalysisTime = now
                    val copy = bitmap.copy(Bitmap.Config.ARGB_8888, false)
                    sceneAnalyzer?.analyzeScene(copy, detections, orientation, lastOcrText)
                }

                bitmap.recycle()
            }
        } catch (e: Exception) {
            Log.e(TAG, "processFrame error: ${e.message}")
        } finally {
            imageProxy.close()
        }
    }

    /** OCR — reconhece texto em placas e sinalizações usando ML Kit */
    private fun runOCR(bitmap: Bitmap) {
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            textRecognizer.process(image)
                .addOnSuccessListener { result ->
                    if (result.text.isNotBlank()) {
                        val cleanText = result.text.trim()
                            .replace("\n", " ")
                            .take(200)
                        lastOcrText = cleanText
                        Log.i(TAG, "OCR: $cleanText")

                        // Anunciar texto relevante por TTS (placas importantes)
                        announceRelevantText(cleanText)
                    } else {
                        lastOcrText = null
                    }
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "OCR failed: ${e.message}")
                }
        } catch (e: Exception) {
            Log.d(TAG, "OCR error: ${e.message}")
        }
    }

    /** Anuncia texto de placas relevantes para navegação */
    private fun announceRelevantText(text: String) {
        if (!ttsReady || !ttsEnabled) return

        val lowerText = text.lowercase()

        // Palavras-chave de placas relevantes para navegação
        val relevantKeywords = listOf(
            "banheiro", "wc", "toilette", "restroom",
            "saída", "saida", "exit",
            "entrada", "entrada",
            "elevador", "elevator",
            "escada", "stairs",
            "perigo", "danger", "cuidado", "atenção",
            "proibido", "pare", "stop",
            "andar", "piso", "floor",
            "recepção", "informação", "info",
            "emergência", "emergency",
            "aberto", "fechado", "open", "closed"
        )

        val found = relevantKeywords.filter { lowerText.contains(it) }
        if (found.isNotEmpty() && text != lastOcrAnnouncedText) {
            lastOcrAnnouncedText = text
            val announcement = "Placa: $text"
            tts?.speak(announcement, TextToSpeech.QUEUE_ADD, null, "claravis_ocr_${System.currentTimeMillis()}")
            Log.i(TAG, "TTS OCR: $announcement")
        }
    }

    /**
     * Converte ImageProxy YUV_420_888 para Bitmap.
     * Trata rotação do sensor e calcula aspect ratio para o OverlayView.
     */
    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        val width = imageProxy.width
        val height = imageProxy.height

        if (!loggedFrameInfo) {
            loggedFrameInfo = true
            Log.i(TAG, "Frame: ${width}x${height}, format=${imageProxy.format}, " +
                "rotation=${imageProxy.imageInfo.rotationDegrees}, " +
                "planes=${imageProxy.planes.size}")
        }

        // Converter YUV_420_888 para ARGB
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

        // Rotação para orientação portrait
        val rotation = imageProxy.imageInfo.rotationDegrees
        if (rotation != 0) {
            val matrix = Matrix()
            matrix.postRotate(rotation.toFloat())
            val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()

            val camAspect = rotated.width.toFloat() / rotated.height.toFloat()
            mainHandler.post {
                binding.overlayView.cameraAspectRatio = camAspect
            }

            return rotated
        }

        val camAspect = bitmap.width.toFloat() / bitmap.height.toFloat()
        mainHandler.post {
            binding.overlayView.cameraAspectRatio = camAspect
        }

        return bitmap
    }

    // ── UI Controls ────────────────────────────────────────

    private fun setupControls() {
        // Night mode toggle
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
        binding.btnSettings.setOnClickListener {
            openSettings()
        }

        // Toque na tela — ocultar/mostrar botões
        binding.previewView.setOnClickListener {
            buttonsVisible = !buttonsVisible
            binding.buttonsPanel.visibility = if (buttonsVisible) View.VISIBLE else View.GONE
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

        sheet.onOverlayToggle = { enabled ->
            binding.overlayView.overlayEnabled = enabled
        }
        sheet.onFpsToggle = { enabled ->
            binding.overlayView.showFps = enabled
        }
        sheet.onFontSizeChange = { size ->
            binding.overlayView.labelSize = size
        }
        sheet.onExposureChange = { value ->
            currentExposure = value
            camera?.cameraControl?.setExposureCompensationIndex(value)
        }
        sheet.onBrightnessChange = { progress ->
            currentBrightness = progress / 100f
            applyColorFilter()
        }
        sheet.onContrastChange = { progress ->
            currentContrast = progress / 100f
            applyColorFilter()
        }
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
            if (enabled) {
                binding.btnTts.text = "VOZ"
                binding.btnTts.setTextColor(0xFF00E676.toInt())
            } else {
                binding.btnTts.text = "MUDO"
                binding.btnTts.setTextColor(0xFF888888.toInt())
                tts?.stop()
            }
        }
        sheet.onTtsIntervalChange = { seconds ->
            ttsInterval = seconds * 1000L
        }

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
