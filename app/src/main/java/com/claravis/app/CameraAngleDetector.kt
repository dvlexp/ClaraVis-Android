package com.claravis.app

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlin.math.atan2
import kotlin.math.sqrt

enum class CameraOrientation {
    LOOKING_DOWN,    // Camera apontando para o chão (pitch < -45°)
    LOOKING_FORWARD, // Camera apontando para frente (-45° a -15°)
    LOOKING_UP       // Camera apontando para cima (placas, teto)
}

class CameraAngleDetector(context: Context) : SensorEventListener {

    companion object {
        private const val TAG = "ClaraVis-Angle"
        private const val SMOOTHING_FACTOR = 0.15f  // EMA — suaviza jitter
        private const val DOWN_THRESHOLD = -45f     // graus
        private const val UP_THRESHOLD = -15f       // graus
    }

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var rotationSensor: Sensor? = null
    private var accelerometer: Sensor? = null
    private var useRotationVector = false

    // Pitch suavizado (graus). -90 = apontando direto para baixo, 0 = horizontal, 90 = para cima
    @Volatile
    var smoothedPitch = -30f  // Default: olhando ligeiramente para frente
        private set

    @Volatile
    var orientation = CameraOrientation.LOOKING_FORWARD
        private set

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    init {
        // Preferir rotation vector (mais estável)
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (rotationSensor != null) {
            useRotationVector = true
            Log.i(TAG, "Using rotation vector sensor")
        } else {
            // Fallback: accelerometer
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            Log.i(TAG, "Fallback to accelerometer sensor")
        }
    }

    fun start() {
        if (useRotationVector) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        } else if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val rawPitch = when (event.sensor.type) {
            Sensor.TYPE_ROTATION_VECTOR -> {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                SensorManager.getOrientation(rotationMatrix, orientationAngles)
                Math.toDegrees(orientationAngles[1].toDouble()).toFloat()
            }
            Sensor.TYPE_ACCELEROMETER -> {
                // Estimativa simples de pitch via acelerômetro
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val pitch = atan2(-y.toDouble(), sqrt((x * x + z * z).toDouble()))
                Math.toDegrees(pitch).toFloat()
            }
            else -> return
        }

        // Suavizar com EMA (Exponential Moving Average)
        smoothedPitch = smoothedPitch * (1f - SMOOTHING_FACTOR) + rawPitch * SMOOTHING_FACTOR

        // Classificar orientação
        orientation = when {
            smoothedPitch < DOWN_THRESHOLD -> CameraOrientation.LOOKING_DOWN
            smoothedPitch > UP_THRESHOLD -> CameraOrientation.LOOKING_UP
            else -> CameraOrientation.LOOKING_FORWARD
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    /** Retorna descrição textual da orientação para logs */
    fun getOrientationLabel(): String = when (orientation) {
        CameraOrientation.LOOKING_DOWN -> "CHÃO (${smoothedPitch.toInt()}°)"
        CameraOrientation.LOOKING_FORWARD -> "FRENTE (${smoothedPitch.toInt()}°)"
        CameraOrientation.LOOKING_UP -> "CIMA (${smoothedPitch.toInt()}°)"
    }
}
