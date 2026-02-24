package com.claravis.app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var detections: List<Detection> = emptyList()
    var overlayEnabled = true
    var labelSize = 40f
        set(value) {
            field = value
            textPaint.textSize = value
        }
    var showFps = true
    var fps: Float = 0f
    var objectCount: Int = 0
    var orientationLabel: String = ""

    // Aspect ratio da câmera (width/height no modo portrait, após rotação)
    // Exemplo: câmera 640x480 → rotação 90° → 480x640 → ratio = 480/640 = 0.75
    var cameraAspectRatio = 0.75f  // 3:4 padrão

    // Cores por categoria
    private val colorPerson = Color.parseColor("#00E676")       // Verde brilhante
    private val colorObstacle = Color.parseColor("#FF1744")     // Vermelho
    private val colorVehicle = Color.parseColor("#448AFF")      // Azul
    private val colorAnimal = Color.parseColor("#FFD600")       // Amarelo
    private val colorOther = Color.parseColor("#B0BEC5")        // Cinza

    private fun categoryColor(cat: ObjectCategory): Int = when (cat) {
        ObjectCategory.PERSON -> colorPerson
        ObjectCategory.OBSTACLE -> colorObstacle
        ObjectCategory.VEHICLE -> colorVehicle
        ObjectCategory.ANIMAL -> colorAnimal
        ObjectCategory.OTHER -> colorOther
    }

    private val boxPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
    }

    private val fillPaint = Paint().apply {
        style = Paint.Style.FILL
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 40f
        isAntiAlias = true
        isFakeBoldText = true
    }

    private val fpsPaint = Paint().apply {
        color = Color.parseColor("#FF00E676")
        textSize = 28f
        isAntiAlias = true
    }

    private val fpsBgPaint = Paint().apply {
        color = Color.parseColor("#99000000")
        style = Paint.Style.FILL
    }

    fun updateDetections(newDetections: List<Detection>) {
        detections = newDetections
        objectCount = newDetections.size
        postInvalidate()
    }

    /**
     * Converte coordenadas normalizadas da câmera (0.0-1.0) para coordenadas do OverlayView,
     * compensando o crop do PreviewView com scaleType="fillCenter".
     *
     * fillCenter escala a imagem para COBRIR todo o view, cortando o excesso.
     * Se a câmera tem aspect ratio maior que o view (mais larga), recorta horizontalmente.
     */
    private fun cameraToViewRect(det: Detection): RectF {
        val viewW = width.toFloat()
        val viewH = height.toFloat()
        val viewRatio = viewW / viewH  // ex: 1080/2340 ≈ 0.462
        val camRatio = cameraAspectRatio   // ex: 480/640 = 0.75

        val sx: Float  // scale X: multiplica normalizedX para obter pixels no view
        val sy: Float
        val ox: Float  // offset X em pixels
        val oy: Float

        if (camRatio > viewRatio) {
            // Câmera é mais larga que o view → recorta horizontalmente
            // fillCenter escala pela altura (height fits perfectly)
            val displayedCamW = camRatio * viewH  // largura da câmera escalada
            sx = displayedCamW
            sy = viewH
            ox = (displayedCamW - viewW) / 2f  // quanto recorta de cada lado
            oy = 0f
        } else {
            // Câmera é mais alta que o view → recorta verticalmente
            val displayedCamH = viewW / camRatio
            sx = viewW
            sy = displayedCamH
            ox = 0f
            oy = (displayedCamH - viewH) / 2f
        }

        return RectF(
            det.boundingBox.left * sx - ox,
            det.boundingBox.top * sy - oy,
            det.boundingBox.right * sx - ox,
            det.boundingBox.bottom * sy - oy
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!overlayEnabled) {
            if (showFps) drawFps(canvas)
            return
        }

        for (det in detections) {
            val color = categoryColor(det.category)
            val rect = cameraToViewRect(det)

            // Pular se fora da tela (recortado pelo fillCenter)
            if (rect.right < 0 || rect.left > width || rect.bottom < 0 || rect.top > height) continue

            // Clampar aos limites da tela
            rect.left = rect.left.coerceAtLeast(0f)
            rect.top = rect.top.coerceAtLeast(0f)
            rect.right = rect.right.coerceAtMost(width.toFloat())
            rect.bottom = rect.bottom.coerceAtMost(height.toFloat())

            // Fill translúcido
            fillPaint.color = (color and 0x00FFFFFF) or 0x20000000
            canvas.drawRect(rect, fillPaint)

            // Contorno
            boxPaint.color = color
            boxPaint.strokeWidth = if (det.category == ObjectCategory.OBSTACLE) 8f else 5f
            canvas.drawRect(rect, boxPaint)

            // Cantos reforçados (estilo moderno)
            drawCorners(canvas, rect, color)

            // Label
            val label = "${det.label} ${(det.confidence * 100).toInt()}%"
            val tw = textPaint.measureText(label)
            val th = textPaint.textSize + 8f
            val padding = 6f

            val ly = if (rect.top > th + padding * 2 + 4) rect.top - th - padding * 2 else rect.top
            val labelBg = RectF(rect.left, ly, rect.left + tw + padding * 2, ly + th + padding * 2)

            // Fundo do label com cor da categoria
            val labelBgPaint = Paint().apply {
                this.color = (color and 0x00FFFFFF) or 0xCC000000.toInt()
                style = Paint.Style.FILL
            }
            canvas.drawRoundRect(labelBg, 4f, 4f, labelBgPaint)
            canvas.drawText(label, rect.left + padding, ly + th, textPaint)
        }

        if (showFps) drawFps(canvas)
    }

    private fun drawCorners(canvas: Canvas, rect: RectF, color: Int) {
        val cornerLen = minOf(24f, rect.width() / 3, rect.height() / 3)
        if (cornerLen < 4f) return

        val p = Paint().apply {
            this.color = color
            style = Paint.Style.STROKE
            strokeWidth = 10f
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
        }

        // Top-left
        canvas.drawLine(rect.left, rect.top, rect.left + cornerLen, rect.top, p)
        canvas.drawLine(rect.left, rect.top, rect.left, rect.top + cornerLen, p)
        // Top-right
        canvas.drawLine(rect.right, rect.top, rect.right - cornerLen, rect.top, p)
        canvas.drawLine(rect.right, rect.top, rect.right, rect.top + cornerLen, p)
        // Bottom-left
        canvas.drawLine(rect.left, rect.bottom, rect.left + cornerLen, rect.bottom, p)
        canvas.drawLine(rect.left, rect.bottom, rect.left, rect.bottom - cornerLen, p)
        // Bottom-right
        canvas.drawLine(rect.right, rect.bottom, rect.right - cornerLen, rect.bottom, p)
        canvas.drawLine(rect.right, rect.bottom, rect.right, rect.bottom - cornerLen, p)
    }

    private fun drawFps(canvas: Canvas) {
        val orientInfo = if (orientationLabel.isNotEmpty()) " | $orientationLabel" else ""
        val text = "${fps.toInt()} FPS | $objectCount obj$orientInfo"
        val tw = fpsPaint.measureText(text) + 16f
        canvas.drawRoundRect(
            width - tw - 8f, 4f, width.toFloat() - 4f, 38f,
            8f, 8f, fpsBgPaint
        )
        canvas.drawText(text, width - tw, 28f, fpsPaint)
    }
}
