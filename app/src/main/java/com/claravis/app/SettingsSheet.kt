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
    }

    private fun simpleSeekListener(onChange: (Int) -> Unit) = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) { onChange(progress) }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }
}
