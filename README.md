# ClaraVis - Assistente Visual para Pessoas com Deficiencia Visual

**ClaraVis** e um aplicativo Android open-source que transforma o celular em um assistente de navegacao para pessoas com baixa visao ou deficiencia visual. Utiliza IA embarcada (on-device) e na nuvem para detectar obstaculos, ler placas, descrever ambientes e alertar sobre perigos em tempo real por voz.

## Funcionalidades

### Deteccao de Objetos em Tempo Real (YOLOv8s)
- Deteccao de 80 classes de objetos (pessoas, veiculos, obstaculos, animais)
- Bounding boxes coloridos por categoria:
  - **Verde**: Pessoas
  - **Vermelho**: Obstaculos (cadeiras, mochilas, hidrantes, etc.)
  - **Azul**: Veiculos
  - **Amarelo**: Animais
  - **Laranja pulsante**: Objetos se aproximando (super destaque)
- Filtros inteligentes contra falsos positivos
- Sistema adaptativo que aprende e melhora com o uso
- Deteccao de movimento: objetos com bounding box crescente recebem prioridade maxima

### Analise de Cena por IA (VLM Local)
- **InternVL3-1B** (primario): Modelo de visao-linguagem com 1B parametros, Q8_0, ~960MB total
  - Respostas em portugues brasileiro
  - Descreve ambiente, piso, obstaculos, placas, pessoas
  - ~66s de inferencia com YOLO pausado (Helio G85)
- **SmolVLM-500M** (fallback): Mais rapido, menor, ~520MB total
- Ambos executados via llama.cpp multimodal CLI (llama-mtmd-cli)
- YOLO pausa automaticamente durante inferencia VLM para liberar CPU

### Interacao por Voz com a Clara
- Botao de microfone (🎤) para fazer perguntas a IA
- Reconhecimento de fala em portugues (Android SpeechRecognizer)
- Exemplos: "Voce esta vendo o controle?", "Qual e a placa do carro?"
- Captura frame da camera + pergunta → VLM analisa → responde por TTS

### Controles de Camera
- **Lanterna** (🔦): Liga/desliga flash da camera
- **Zoom**: Botoes +/- na lateral (ate 10x)
- **Modo Noturno**: Exposicao maxima + ganho digital + contraste
- **Brilho e Contraste**: Ajustaveis nas configuracoes

### Orientacao por Acelerometro
O app detecta automaticamente para onde a camera esta apontando:
- **Olhando para o CHAO**: Foca em escadas, buracos, degraus, obstaculos no piso
- **Olhando para FRENTE**: Descreve caminho, portas, corredores, placas, pessoas
- **Olhando para CIMA**: Identifica sinalizacoes, placas (banheiro, saida, elevador)

### Leitura de Placas (OCR)
- Reconhecimento de texto via Google ML Kit (on-device)
- Anuncia automaticamente placas relevantes: banheiro, saida, elevador, perigo, etc.
- Texto detectado e enviado como contexto para a IA

### Sistema Adaptativo (Machine Learning)
- Aprende padroes de deteccao com o uso
- Aumenta thresholds para classes com alta taxa de falso positivo
- Confirma deteccoes quando objetos relacionados aparecem juntos
- Ajusta confianca baseado na orientacao da camera
- Persiste aprendizado entre sessoes

### Feedback por Voz (TTS)
- Text-to-Speech em portugues brasileiro
- Prioridade de anuncio: objetos se aproximando > obstaculos > pessoas > veiculos > animais
- Posicao relativa: "pessoa a frente", "cadeira a esquerda, perto"
- Descricoes de cena VLM tem prioridade sobre deteccoes YOLO
- Intervalo configuravel

## Arquitetura

```
Camera (CameraX) --> Frame --> [Pipeline paralelo]
                                  |
                    +-------------+-------------+
                    |             |              |
              YOLOv8s TFLite  ML Kit OCR  Acelerometro
              (deteccao)      (placas)    (orientacao)
                    |             |              |
                    +------+------+------+------+
                           |             |
                    Adaptive Filter   Context Builder
                           |             |
                    +------+------+------+
                    |                    |
              Overlay Engine      Scene Analyzer (VLM)
              (bounding boxes)   (InternVL3-1B / SmolVLM-500M)
                    |                    |
              Tela + HUD           TTS (voz)
                    |
              Movement Detector
              (objetos se aproximando)
                    |
              Voice Interaction
              (SpeechRecognizer → VLM → TTS)
```

## Requisitos

### Hardware
- Android 7.0+ (API 24)
- Camera traseira
- Acelerometro/giroscopio
- Recomendado: 4GB+ RAM, processador octa-core

### Testado em
- Xiaomi Redmi Note 9 (Helio G85, 4GB RAM, Android 12)

### Para VLM Local (opcional mas recomendado)
Modelos em `/data/local/tmp/claravis/` via ADB:

**InternVL3-1B (primario, ~960MB):**
- `InternVL3-1B-Instruct-Q8_0.gguf` (~644MB)
- `mmproj-InternVL3-1B-Instruct-Q8_0.gguf` (~317MB)
- Fonte: `ggml-org/InternVL3-1B-Instruct-GGUF` no HuggingFace

**SmolVLM-500M (fallback, ~520MB):**
- `SmolVLM-500M-Instruct-Q8_0.gguf` (~417MB)
- `mmproj-SmolVLM-500M-Instruct-Q8_0.gguf` (~104MB)

### Para Analise Cloud (opcional, desativado por padrao)
- Chave API do Google Gemini (gratuita em https://aistudio.google.com/apikey)
- Salvar em `/sdcard/Download/claravis_api_key.txt`

## Build

```bash
# Clonar o repositorio
git clone https://github.com/dvlexp/ClaraVis-Android.git
cd ClaraVis-Android

# Build debug
./gradlew assembleDebug

# Instalar no dispositivo (MIUI pode exigir workaround)
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Workaround MIUI (se install falhar):
adb push app/build/outputs/apk/debug/app-debug.apk /data/local/tmp/claravis_debug.apk
adb shell pm install -r /data/local/tmp/claravis_debug.apk
```

## Estrutura do Projeto

```
app/src/main/java/com/claravis/app/
  MainActivity.kt          # Activity principal — camera, TTS, voz, controles
  ObjectDetector.kt         # YOLOv8s/n TFLite — deteccao de objetos
  OverlayView.kt            # Bounding boxes, HUD, super destaque, status de voz
  SceneAnalyzer.kt          # Gemini Cloud AI — analise de cena
  LocalVLM.kt               # InternVL3-1B / SmolVLM local via llama.cpp
  CameraAngleDetector.kt    # Acelerometro — orientacao da camera
  AdaptiveConfidence.kt     # Machine learning adaptativo
  SettingsSheet.kt          # Configuracoes (BottomSheet)

app/src/main/assets/
  yolov8s_float32.tflite    # Modelo YOLOv8s (43MB, 320x320)
  yolov8n_float32.tflite    # Modelo YOLOv8n fallback (13MB, 320x320)

app/src/main/jniLibs/arm64-v8a/
  libllama_mtmd.so           # llama.cpp multimodal (ARM64, static)
```

## Stack Tecnico

| Componente | Tecnologia |
|-----------|-----------|
| Linguagem | Kotlin |
| Camera | CameraX 1.3.1 |
| Deteccao | YOLOv8s via TensorFlow Lite 2.14 |
| OCR | Google ML Kit Text Recognition 16.0 |
| VLM Local | InternVL3-1B / SmolVLM-500M via llama.cpp (ARM64) |
| VLM Cloud | Google Gemini 2.5 Flash Lite (desativado) |
| TTS | Android TextToSpeech (pt-BR) |
| Voice Input | Android SpeechRecognizer (pt-BR) |
| Sensores | SensorManager (Rotation Vector / Accelerometer) |
| UI | Material Design + Custom OverlayView |

## Roadmap

- [x] Camera com night boost
- [x] YOLOv8n deteccao em tempo real
- [x] YOLOv8s para melhor precisao
- [x] TTS em portugues
- [x] Gemini Cloud AI para analise de cena
- [x] SmolVLM local (offline) via llama.cpp
- [x] InternVL3-1B como VLM primario (melhor qualidade)
- [x] OCR para leitura de placas
- [x] Acelerometro para orientacao contextual
- [x] Sistema adaptativo de confianca
- [x] Lanterna e controle de zoom
- [x] Deteccao de objetos se aproximando (movement tracking)
- [x] Super destaque visual para alertas
- [x] Interacao por voz (perguntar a Clara)
- [x] Pausa inteligente do YOLO durante inferencia VLM
- [ ] MiDaS para estimativa de profundidade
- [ ] Modelo custom treinado para escadas/degraus
- [ ] Modo de gravacao para demo/crowdfunding
- [ ] Publicacao na Play Store

## Sobre o Projeto ClaraVis

ClaraVis e um projeto de tecnologia assistiva que visa criar dispositivos vestiveis para pessoas com deficiencia visual. Este app Android e o pre-MVP que demonstra as capacidades da tecnologia usando apenas um celular comum.

**Desenvolvido por:** Daniel Valladares — Resultados Exponenciais

## Licenca

MIT License — Veja [LICENSE](LICENSE) para detalhes.
