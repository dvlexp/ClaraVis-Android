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

    // Indices de detecções que estão se aproximando (super destaque)
    var approachingIndices: Set<Int> = emptySet()

    // Aspect ratio da câmera (width/height no modo portrait, após rotação)
    var cameraAspectRatio = 0.75f

    // Cores por categoria
    private val colorPerson = Color.parseColor("#00E676")
    private val colorObstacle = Color.parseColor("#FF1744")
    private val colorVehicle = Color.parseColor("#448AFF")
    private val colorAnimal = Color.parseColor("#FFD600")
    private val colorOther = Color.parseColor("#B0BEC5")
    private val colorApproaching = Color.parseColor("#FF6D00")  // Laranja intenso para "se aproximando"

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

    // Paint para glow de objetos se aproximando
    private val glowPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    // Status de escuta de voz
    var listeningStatus: String? = null

    fun updateDetections(newDetections: List<Detection>) {
        detections = newDetections
        objectCount = newDetections.size
        postInvalidate()
    }

    private fun cameraToViewRect(det: Detection): RectF {
        val viewW = width.toFloat()
        val viewH = height.toFloat()
        val viewRatio = viewW / viewH
        val camRatio = cameraAspectRatio

        val sx: Float
        val sy: Float
        val ox: Float
        val oy: Float

        if (camRatio > viewRatio) {
            val displayedCamW = camRatio * viewH
            sx = displayedCamW
            sy = viewH
            ox = (displayedCamW - viewW) / 2f
            oy = 0f
        } else {
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
            drawListeningStatus(canvas)
            return
        }

        for ((index, det) in detections.withIndex()) {
            val isApproaching = index in approachingIndices
            val color = if (isApproaching) colorApproaching else categoryColor(det.category)
            val rect = cameraToViewRect(det)

            // Pular se fora da tela
            if (rect.right < 0 || rect.left > width || rect.bottom < 0 || rect.top > height) continue

            // Clampar aos limites da tela
            rect.left = rect.left.coerceAtLeast(0f)
            rect.top = rect.top.coerceAtLeast(0f)
            rect.right = rect.right.coerceAtMost(width.toFloat())
            rect.bottom = rect.bottom.coerceAtMost(height.toFloat())

            if (isApproaching) {
                // ═══ SUPER DESTAQUE para objetos se aproximando ═══

                // Glow externo (halo brilhante)
                glowPaint.color = (color and 0x00FFFFFF) or 0x60000000
                glowPaint.strokeWidth = 20f
                canvas.drawRect(rect, glowPaint)

                // Fill mais intenso
                fillPaint.color = (color and 0x00FFFFFF) or 0x40000000
                canvas.drawRect(rect, fillPaint)

                // Contorno grosso
                boxPaint.color = color
                boxPaint.strokeWidth = 12f
                canvas.drawRect(rect, boxPaint)

                // Cantos reforçados extra grossos
                drawCorners(canvas, rect, color, 16f)

                // Label com alerta
                val label = "⚠ ${det.label} ${(det.confidence * 100).toInt()}%"
                drawLabel(canvas, rect, label, color, true)

            } else {
                // ═══ Desenho normal ═══

                // Fill translúcido
                fillPaint.color = (color and 0x00FFFFFF) or 0x20000000
                canvas.drawRect(rect, fillPaint)

                // Contorno
                boxPaint.color = color
                boxPaint.strokeWidth = if (det.category == ObjectCategory.OBSTACLE) 8f else 5f
                canvas.drawRect(rect, boxPaint)

                // Cantos reforçados
                drawCorners(canvas, rect, color, 10f)

                // Label
                val label = "${det.label} ${(det.confidence * 100).toInt()}%"
                drawLabel(canvas, rect, label, color, false)
            }
        }

        if (showFps) drawFps(canvas)
        drawListeningStatus(canvas)
    }

    private fun drawLabel(canvas: Canvas, rect: RectF, label: String, color: Int, isAlert: Boolean) {
        val paint = if (isAlert) {
            Paint(textPaint).apply { textSize = textPaint.textSize * 1.2f }
        } else textPaint

        val tw = paint.measureText(label)
        val th = paint.textSize + 8f
        val padding = 6f

        val ly = if (rect.top > th + padding * 2 + 4) rect.top - th - padding * 2 else rect.top
        val labelBg = RectF(rect.left, ly, rect.left + tw + padding * 2, ly + th + padding * 2)

        val bgAlpha = if (isAlert) 0xEE000000.toInt() else 0xCC000000.toInt()
        val labelBgPaint = Paint().apply {
            this.color = (color and 0x00FFFFFF) or bgAlpha
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(labelBg, 4f, 4f, labelBgPaint)
        canvas.drawText(label, rect.left + padding, ly + th, paint)
    }

    private fun drawCorners(canvas: Canvas, rect: RectF, color: Int, strokeW: Float = 10f) {
        val cornerLen = minOf(24f, rect.width() / 3, rect.height() / 3)
        if (cornerLen < 4f) return

        val p = Paint().apply {
            this.color = color
            style = Paint.Style.STROKE
            strokeWidth = strokeW
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
        }

        canvas.drawLine(rect.left, rect.top, rect.left + cornerLen, rect.top, p)
        canvas.drawLine(rect.left, rect.top, rect.left, rect.top + cornerLen, p)
        canvas.drawLine(rect.right, rect.top, rect.right - cornerLen, rect.top, p)
        canvas.drawLine(rect.right, rect.top, rect.right, rect.top + cornerLen, p)
        canvas.drawLine(rect.left, rect.bottom, rect.left + cornerLen, rect.bottom, p)
        canvas.drawLine(rect.left, rect.bottom, rect.left, rect.bottom - cornerLen, p)
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

    private fun drawListeningStatus(canvas: Canvas) {
        val status = listeningStatus ?: return
        val paint = Paint().apply {
            color = Color.WHITE
            textSize = 48f
            isAntiAlias = true
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        val bgPaint = Paint().apply {
            color = Color.parseColor("#DD000000")
            style = Paint.Style.FILL
        }
        val tw = paint.measureText(status) + 40f
        val cx = width / 2f
        val cy = height * 0.4f
        canvas.drawRoundRect(cx - tw / 2, cy - 40f, cx + tw / 2, cy + 20f, 12f, 12f, bgPaint)
        canvas.drawText(status, cx, cy, paint)
    }
}
