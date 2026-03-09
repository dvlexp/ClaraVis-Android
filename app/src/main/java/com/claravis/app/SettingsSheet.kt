package com.claravis.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsSheet : BottomSheetDialogFragment() {

    var onOverlayToggle: ((Boolean) -> Unit)? = null
    var onFpsToggle: ((Boolean) -> Unit)? = null
    var onFontSizeChange: ((Float) -> Unit)? = null
    var onExposureChange: ((Int) -> Unit)? = null
    var onBrightnessChange: ((Int) -> Unit)? = null
    var onContrastChange: ((Int) -> Unit)? = null
    var onNightModeClick: (() -> Unit)? = null
    var onTtsToggle: ((Boolean) -> Unit)? = null
    var onTtsIntervalChange: ((Int) -> Unit)? = null
    var onSpeechRateChange: ((Float) -> Unit)? = null
    var onUrgentCooldownChange: ((Long) -> Unit)? = null
    var onConfidenceChange: ((Float) -> Unit)? = null
    var onConfusionThresholdChange: ((Float) -> Unit)? = null

    // Estado atual (setado pelo caller)
    var overlayEnabled = true
    var fpsEnabled = true
    var currentFontSize = 40
    var exposureMin = 0
    var exposureMax = 0
    var currentExposure = 0
    var currentBrightness = 100
    var currentContrast = 100
    var nightModeOn = false
    var ttsEnabled = true
    var ttsInterval = 3
    var speechRate = 20       // x10 (20 = 2.0x)
    var urgentCooldown = 8    // x100ms (8 = 0.8s)
    var confidence = 35       // % (35 = 0.35)
    var confusionThreshold = 50  // % (50 = 0.50)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sheet_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Overlay
        val switchOverlay = view.findViewById<SwitchMaterial>(R.id.switchOverlay)
        switchOverlay.isChecked = overlayEnabled
        switchOverlay.setOnCheckedChangeListener { _, checked -> onOverlayToggle?.invoke(checked) }

        val switchFps = view.findViewById<SwitchMaterial>(R.id.switchFps)
        switchFps.isChecked = fpsEnabled
        switchFps.setOnCheckedChangeListener { _, checked -> onFpsToggle?.invoke(checked) }

        val labelFontSize = view.findViewById<TextView>(R.id.labelFontSize)
        val seekFontSize = view.findViewById<SeekBar>(R.id.seekFontSize)
        seekFontSize.progress = currentFontSize
        labelFontSize.text = "Tamanho da fonte: $currentFontSize"
        seekFontSize.setOnSeekBarChangeListener(simpleSeekListener { progress ->
            labelFontSize.text = "Tamanho da fonte: $progress"
            onFontSizeChange?.invoke(progress.toFloat())
        })

        // Câmera
        val nightBtn = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnNightSettings)
        nightBtn.text = if (nightModeOn) "NIGHT MODE: ON" else "NIGHT MODE: OFF"
        nightBtn.setOnClickListener {
            onNightModeClick?.invoke()
            nightModeOn = !nightModeOn
            nightBtn.text = if (nightModeOn) "NIGHT MODE: ON" else "NIGHT MODE: OFF"
        }

        val labelExposure = view.findViewById<TextView>(R.id.labelExposure)
        val seekExposure = view.findViewById<SeekBar>(R.id.seekExposure)
        val expRange = exposureMax - exposureMin
        seekExposure.max = if (expRange > 0) expRange else 1
        seekExposure.progress = currentExposure - exposureMin
        labelExposure.text = "Exposição: ${if (currentExposure > 0) "+" else ""}$currentExposure"
        seekExposure.setOnSeekBarChangeListener(simpleSeekListener { progress ->
            val value = exposureMin + progress
            labelExposure.text = "Exposição: ${if (value > 0) "+" else ""}$value"
            onExposureChange?.invoke(value)
        })

        val labelBrightness = view.findViewById<TextView>(R.id.labelBrightness)
        val seekBrightness = view.findViewById<SeekBar>(R.id.seekBrightness)
        seekBrightness.progress = currentBrightness
        labelBrightness.text = "Brilho: $currentBrightness%"
        seekBrightness.setOnSeekBarChangeListener(simpleSeekListener { progress ->
            labelBrightness.text = "Brilho: $progress%"
            onBrightnessChange?.invoke(progress)
        })

        val labelContrast = view.findViewById<TextView>(R.id.labelContrast)
        val seekContrast = view.findViewById<SeekBar>(R.id.seekContrast)
        seekContrast.progress = currentContrast
        labelContrast.text = "Contraste: $currentContrast%"
        seekContrast.setOnSeekBarChangeListener(simpleSeekListener { progress ->
            labelContrast.text = "Contraste: $progress%"
            onContrastChange?.invoke(progress)
        })

        // Detecção
        val labelConfidence = view.findViewById<TextView>(R.id.labelConfidence)
        val seekConfidence = view.findViewById<SeekBar>(R.id.seekConfidence)
        seekConfidence.progress = confidence
        labelConfidence.text = "Sensibilidade: $confidence%"
        seekConfidence.setOnSeekBarChangeListener(simpleSeekListener { progress ->
            val p = progress.coerceAtLeast(10)
            labelConfidence.text = "Sensibilidade: $p%"
            onConfidenceChange?.invoke(p / 100f)
        })

        val labelConfusion = view.findViewById<TextView>(R.id.labelConfusionThreshold)
        val seekConfusion = view.findViewById<SeekBar>(R.id.seekConfusionThreshold)
        seekConfusion.progress = confusionThreshold
        labelConfusion.text = "Filtro confusão: $confusionThreshold%"
        seekConfusion.setOnSeekBarChangeListener(simpleSeekListener { progress ->
            val p = progress.coerceAtLeast(20)
            labelConfusion.text = "Filtro confusão: $p%"
            onConfusionThresholdChange?.invoke(p / 100f)
        })

        // Voz
        val switchTts = view.findViewById<SwitchMaterial>(R.id.switchTts)
        switchTts.isChecked = ttsEnabled
        switchTts.setOnCheckedChangeListener { _, checked -> onTtsToggle?.invoke(checked) }

        val labelInterval = view.findViewById<TextView>(R.id.labelTtsInterval)
        val seekInterval = view.findViewById<SeekBar>(R.id.seekTtsInterval)
        seekInterval.progress = ttsInterval
        labelInterval.text = "Intervalo: ${ttsInterval}s"
        seekInterval.setOnSeekBarChangeListener(simpleSeekListener { progress ->
            labelInterval.text = "Intervalo: ${progress}s"
            onTtsIntervalChange?.invoke(progress)
        })

        val labelRate = view.findViewById<TextView>(R.id.labelSpeechRate)
        val seekRate = view.findViewById<SeekBar>(R.id.seekSpeechRate)
        seekRate.progress = speechRate
        labelRate.text = "Velocidade da fala: ${"%.1f".format(speechRate / 10f)}x"
        seekRate.setOnSeekBarChangeListener(simpleSeekListener { progress ->
            val p = progress.coerceAtLeast(5)
            labelRate.text = "Velocidade da fala: ${"%.1f".format(p / 10f)}x"
            onSpeechRateChange?.invoke(p / 10f)
        })

        val labelUrgent = view.findViewById<TextView>(R.id.labelUrgentCooldown)
        val seekUrgent = view.findViewById<SeekBar>(R.id.seekUrgentCooldown)
        seekUrgent.progress = urgentCooldown
        labelUrgent.text = "Cooldown urgente: ${"%.1f".format(urgentCooldown / 10f)}s"
        seekUrgent.setOnSeekBarChangeListener(simpleSeekListener { progress ->
            val p = progress.coerceAtLeast(3)
            labelUrgent.text = "Cooldown urgente: ${"%.1f".format(p / 10f)}s"
            onUrgentCooldownChange?.invoke(p * 100L)
        })
    }

    private fun simpleSeekListener(onChange: (Int) -> Unit) = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) { onChange(progress) }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }
}
