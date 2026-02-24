# CLAUDE.md

This file provides guidance to Claude Code when working with code in this repository.

## Project Overview

**ClaraVis-Android** is an assistive technology Android app for visually impaired people. It uses on-device AI (YOLOv8, SmolVLM, ML Kit OCR) and optional cloud AI (Google Gemini) to detect obstacles, read signs, describe scenes, and provide voice navigation guidance.

Language: Brazilian Portuguese (pt-BR) for UI, TTS, and user-facing content.

## Build & Deploy

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Launch
adb shell am start -n com.claravis.app/.MainActivity

# View logs
adb logcat --pid=$(adb shell pidof -s com.claravis.app) | grep ClaraVis
```

## Architecture

- **MainActivity.kt** — Central orchestrator. Integrates camera, detector, VLM, OCR, TTS, accelerometer, and adaptive system.
- **ObjectDetector.kt** — YOLOv8s/n TFLite inference. Auto-detects coord scale, applies confidence filters, NMS, and category classification.
- **OverlayView.kt** — Custom View for rendering bounding boxes, labels, FPS counter, and orientation indicator.
- **SceneAnalyzer.kt** — Google Gemini API integration with context-aware prompts based on camera orientation.
- **LocalVLM.kt** — SmolVLM-500M via llama.cpp ProcessBuilder. Runs the static ARM64 binary from nativeLibraryDir.
- **CameraAngleDetector.kt** — SensorManager with rotation vector/accelerometer. Classifies LOOKING_DOWN/FORWARD/UP.
- **AdaptiveConfidence.kt** — Learning system that adjusts per-class thresholds based on detection patterns. Persists via SharedPreferences.
- **SettingsSheet.kt** — BottomSheetDialogFragment for overlay, camera, and TTS settings.

## Key Technical Details

### Model Files (NOT in repo — too large)
- `yolov8s_float32.tflite` (43MB) — Primary detection model, placed in `app/src/main/assets/`
- `yolov8n_float32.tflite` (13MB) — Fallback model, placed in `app/src/main/assets/`
- `libllama_mtmd.so` (5.8MB) — Static ARM64 llama.cpp binary, placed in `app/src/main/jniLibs/arm64-v8a/`
- SmolVLM GGUF models (521MB total) — Pushed to device via ADB at `/data/local/tmp/claravis/`

### SELinux Workaround
The llama.cpp binary is packaged as `libllama_mtmd.so` in jniLibs so Android extracts it to nativeLibraryDir with execute permission. Requires `extractNativeLibs=true` and `useLegacyPackaging=true`.

### Target Device
Xiaomi Redmi Note 9 — Helio G85, Mali-G52 MC2, 3.6GB RAM, Android 12
ADB serial: 05df114d0405

### MIUI Issues
- Screen timeout aggressive — `FLAG_KEEP_SCREEN_ON` + `PARTIAL_WAKE_LOCK` + `showWhenLocked` applied
- ADB install needs user approval on phone (INSTALL_FAILED_USER_RESTRICTED)
- Input injection blocked by SELinux on locked screen

## Key Constants

- Detection confidence threshold: 0.35 (adaptive range: 0.25-0.70)
- Camera orientation thresholds: DOWN < -45°, UP > -15°
- TTS interval: 3s (configurable)
- Scene analysis: 8s (cloud), 10s (local VLM)
- OCR interval: 5s
- Local VLM timeout: 90s
- Max tokens (VLM): 80
