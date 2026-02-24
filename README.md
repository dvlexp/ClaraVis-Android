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
- Filtros inteligentes contra falsos positivos
- Sistema adaptativo que aprende e melhora com o uso

### Analise de Cena por IA
- **Local (offline)**: SmolVLM-500M via llama.cpp — funciona sem internet
- **Nuvem**: Google Gemini 2.5 Flash Lite — analise avancada quando online
- Prompts adaptativos baseados na orientacao da camera

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
- Prioridade de anuncio: obstaculos > pessoas > veiculos > animais
- Posicao relativa: "pessoa a frente", "cadeira a esquerda"
- Distancia estimada pelo tamanho do bounding box
- Intervalo configuravel

### Modo Noturno
- Exposicao maxima + ganho digital
- Controle de brilho e contraste
- Amplia visibilidade em ambientes escuros

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
              Overlay Engine      Scene Analyzer
              (bounding boxes)   (VLM local / Gemini cloud)
                    |                    |
              Tela + HUD           TTS (voz)
```

## Requisitos

### Hardware
- Android 7.0+ (API 24)
- Camera traseira
- Acelerometro/giroscopio
- Recomendado: 4GB+ RAM, processador octa-core

### Testado em
- Xiaomi Redmi Note 9 (Helio G85, 4GB RAM, Android 12)

### Para VLM Local (opcional)
- ~521MB de espaco para modelos GGUF:
  - `SmolVLM-500M-Instruct-Q8_0.gguf` (~417MB)
  - `mmproj-SmolVLM-500M-Instruct-Q8_0.gguf` (~104MB)
- Colocar em `/data/local/tmp/claravis/` via ADB

### Para Analise Cloud (opcional)
- Chave API do Google Gemini (gratuita em https://aistudio.google.com/apikey)
- Salvar em `/sdcard/Download/claravis_api_key.txt`

## Build

```bash
# Clonar o repositorio
git clone https://github.com/dvlexp/ClaraVis-Android.git
cd ClaraVis-Android

# Build debug
./gradlew assembleDebug

# Instalar no dispositivo
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Estrutura do Projeto

```
app/src/main/java/com/claravis/app/
  MainActivity.kt          # Activity principal — integra todos os componentes
  ObjectDetector.kt         # YOLOv8s/n TFLite — deteccao de objetos
  OverlayView.kt            # Renderizacao de bounding boxes e HUD
  SceneAnalyzer.kt          # Gemini Cloud AI — analise de cena
  LocalVLM.kt               # SmolVLM local via llama.cpp
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
| VLM Local | SmolVLM-500M via llama.cpp (ARM64) |
| VLM Cloud | Google Gemini 2.5 Flash Lite |
| TTS | Android TextToSpeech (pt-BR) |
| Sensores | SensorManager (Rotation Vector / Accelerometer) |
| UI | Material Design + Custom OverlayView |

## Roadmap

- [x] Camera com night boost
- [x] YOLOv8n deteccao em tempo real
- [x] YOLOv8s para melhor precisao
- [x] TTS em portugues
- [x] Gemini Cloud AI para analise de cena
- [x] SmolVLM local (offline) via llama.cpp
- [x] OCR para leitura de placas
- [x] Acelerometro para orientacao contextual
- [x] Sistema adaptativo de confianca
- [ ] MiDaS para estimativa de profundidade
- [ ] Modelo custom treinado para escadas/degraus
- [ ] Modo de gravacao para demo/crowdfunding
- [ ] Publicacao na Play Store

## Sobre o Projeto ClaraVis

ClaraVis e um projeto de tecnologia assistiva que visa criar dispositivos vestiveis para pessoas com deficiencia visual. Este app Android e o pre-MVP que demonstra as capacidades da tecnologia usando apenas um celular comum.

**Desenvolvido por:** Daniel Valladares — Resultados Exponenciais

## Licenca

MIT License — Veja [LICENSE](LICENSE) para detalhes.
