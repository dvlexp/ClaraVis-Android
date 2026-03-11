# ClaraVis — Mapa Completo de Features

> **Versao:** 1.0 | **Data:** 2026-03-11
> **Objetivo:** Catalogo de TODAS as features identificadas na pesquisa de concorrentes, papers academicos e benchmarking cross-industria. Cada feature tem classificacao de viabilidade, "como fazer", e pre-selecao (ESSENCIAL / INTERESSANTE / BULLSHIT).
> **Referencia:** Todas as fontes em `REFERENCIAS.md`

---

## Legenda de Classificacao

| Icone | Classificacao | Significado |
|-------|--------------|-------------|
| **ESSENCIAL** | Core | Sem isso o ClaraVis nao se diferencia. Prioridade maxima. |
| **INTERESSANTE** | Vale explorar | Agrega valor real, viavel no roadmap 12-24 meses. |
| **FUTURO** | Longo prazo | Tecnologia promissora mas depende de hardware/custo que ainda nao esta pronto. |
| **BULLSHIT** | Descartado | Hype, impraticavel, ou nao resolve problema real do usuario. |

---

## CATEGORIA 1: DETECCAO DE OBJETOS E OBSTACULOS

### 1.1 Deteccao YOLO em tempo real (on-device)
- **O que e:** Modelo de deteccao de objetos rodando no dispositivo, sem internet, identificando obstaculos, objetos e perigos em tempo real
- **Como ClaraVis faz hoje:** YOLOv8s fine-tuned, 54 classes, 416x416 float16, TFLite no Helio G85
- **Como melhorar:**
  - Upgrade para **YOLO11n** ou **YOLOv10n** (menos parametros, mesma ou melhor acuracia) [REF-60]
  - Exportar para TensorRT no Jetson (2-3x mais rapido que TFLite)
  - Testar **RT-DETR** no Jetson (elimina NMS, pipeline mais limpo) [REF-61]
- **Referencia:** [REF-60] Ultralytics YOLO11; [REF-61] RT-DETR (CVPR 2024)
- **Quem faz:** OrCam (on-device, chip proprio), dotLumen (PAD AI), Seeing AI (on-device parcial)
- **Classificacao:** **ESSENCIAL** — core do produto, ja implementado, precisa evoluir

### 1.2 Deteccao de obstaculos especifica para cegos
- **O que e:** Modelos treinados especificamente para obstaculos de navegacao (buracos, degraus, postes, galhos, pocas, etc.)
- **Como fazer:**
  - **Nav-YOLO** (ShuffleNetv2 backbone, leve para mobile) — open-source [REF-62]
  - **YOLO-OD** (classes de obstaculos para cegos) — open-source [REF-63]
  - **YOLO-Extreme** (deteccao em neblina/chuva) [REF-64]
  - Treinar classes ClaraVis combinando datasets existentes + datasets desses papers
- **Referencia:** [REF-62] Nav-YOLO; [REF-63] YOLO-OD; [REF-64] YOLO-Extreme
- **Classificacao:** **ESSENCIAL** — diferencial vs apps genericos (Be My Eyes, Seeing AI)

### 1.3 Deteccao de semaforos para pedestres
- **O que e:** Reconhecer estado do semaforo (verde/vermelho/piscando) + faixa de pedestres
- **Como fazer:**
  - **LYTNet** — CNN leve, open-source, inclui dataset PTL, roda em iOS/Android [REF-65]
  - **FlashLightNet** — detecta estados piscantes [REF-66]
  - Integrar como modelo secundario rodando em paralelo ao YOLO
- **Referencia:** [REF-65] LYTNet/ImVisible; [REF-66] FlashLightNet
- **Quem faz:** Nenhum concorrente faz bem isso on-device
- **Classificacao:** **ESSENCIAL** — travessia de rua e uma das situacoes mais perigosas

### 1.4 Segmentacao de calcada/caminho transitavel
- **O que e:** Pixel-level: onde e seguro pisar vs. onde nao e (rua, buraco, obstaculo)
- **Como fazer:**
  - **DSC-Net** (CNN + Swin-Transformer, dual-branch) [REF-67]
  - **QPULM** (UNet-MobileNet quantizado + SODD para distancia) — Android [REF-68]
  - Treinar com datasets de calcadas brasileiras (asfalto irregular, meio-fio alto, bueiros)
- **Referencia:** [REF-67] DSC-Net; [REF-68] QPULM
- **Classificacao:** **INTERESSANTE** — melhora significativa vs. bounding boxes, mas compute pesado

### 1.5 Deteccao de pedestres otimizada (autonomous vehicles)
- **O que e:** YOLO otimizado especificamente para detectar pedestres com menos parametros
- **Como fazer:**
  - **LP-YOLO** (24-68% menos parametros, baseado em YOLOv11) [REF-69]
  - **DSR-YOLO** (baseado em YOLOv8) [REF-70]
  - Drop-in upgrade nas classes "pessoa" do modelo ClaraVis
- **Referencia:** [REF-69] LP-YOLO; [REF-70] DSR-YOLO
- **Classificacao:** **INTERESSANTE** — melhora deteccao de pessoas se aproximando

---

## CATEGORIA 2: ESTIMATIVA DE PROFUNDIDADE E DISTANCIA

### 2.1 Profundidade monocular por IA (software-only)
- **O que e:** Estimar distancia de cada pixel usando apenas 1 camera + modelo de IA
- **Como fazer:**
  - **Depth Anything V2 Small** (~25MB, ~30 FPS no Jetson, Apache 2.0) [REF-71]
  - ONNX export disponivel; TensorRT para Jetson; Qualcomm AI Hub para mobile [REF-71]
  - Rodar em paralelo ao YOLO: YOLO detecta "carro", Depth Anything diz "a 4.2 metros"
  - **MiDaS v3.1 Small** como alternativa (MIT license) [REF-72]
- **Referencia:** [REF-71] Depth Anything V2; [REF-72] MiDaS
- **Quem faz:** dotLumen (cameras stereo proprias), Glidance (stereo depth)
- **Classificacao:** **ESSENCIAL** — transforma "obstaculo detectado" em "obstaculo a X metros". Custo zero de hardware.

### 2.2 Profundidade com camera stereo (hardware)
- **O que e:** Duas cameras com baseline fisico calculam profundidade geometrica (mais preciso que monocular)
- **Como fazer:**
  - **Luxonis OAK-D Lite** ($149, 91x28mm, 4 TOPS on-device, 0.2-19m) [REF-73]
  - **Intel RealSense D435i** (~$200, 0.1-10m) — RealSense agora e empresa independente [REF-73]
  - DepthAI SDK open-source (Apache 2.0) — roda YOLO on-device no OAK-D
- **Referencia:** [REF-73] Luxonis OAK-D / RealSense
- **Classificacao:** **INTERESSANTE** — mais preciso que monocular, mas adiciona hardware. Bom para versao Jetson.

### 2.3 LiDAR chip-scale (dToF)
- **O que e:** Sensor laser time-of-flight miniaturizado (tipo iPhone LiDAR) para profundidade precisa
- **Como fazer:**
  - Sensores dToF ja existem em iPhones — viavel em glasses [REF-74]
  - **Prompt Depth Anything** usa LiDAR low-res para calibrar profundidade monocular em 4K metrico [REF-75]
- **Referencia:** [REF-74] LiDAR miniaturizado; [REF-75] Prompt Depth Anything
- **Classificacao:** **FUTURO** — excelente mas depende de hardware dedicado

### 2.4 Radar mmWave para distancia
- **O que e:** Radar 77GHz que funciona em escuridao, neblina, chuva — detecta objetos em movimento
- **Como fazer:**
  - **TI AWR1843** (10x10mm chip, ~$30, 1.5W, alcance 0.2-120m) [REF-76]
  - **Infineon BGT60TR13C** (6.5x5mm, ~$15, 0.5W, 0.2-10m) [REF-76]
  - OpenRadar SDK open-source
- **Referencia:** [REF-76] mmWave radar automotive
- **Classificacao:** **FUTURO** — fantastico para all-weather, mas complexo de integrar em glasses

---

## CATEGORIA 3: AUDIO E FEEDBACK SONORO

### 3.1 Audio espacial 3D (HRTF)
- **O que e:** Som que parece vir de posicoes 3D reais — "carro a direita", "degrau a frente" como sons posicionados no espaco
- **Como fazer:**
  - **3D Tune-In Toolkit** (GPL, C++, binaural + simulacao de aparelho auditivo) [REF-77]
  - **Steam Audio** (gratis, HRTF, ray tracing acustico, plugins Unity/FMOD) [REF-78]
  - **Spatial Audio Framework** (C/C++, open-source) [REF-79]
  - Implementar: deteccao YOLO fornece coordenadas (x,y) + Depth Anything fornece distancia → 3DTI renderiza som na posicao 3D correspondente
  - Usar fones de conducao ossea (nao bloqueia sons ambientes)
- **Referencia:** [REF-77] 3D Tune-In; [REF-78] Steam Audio; [REF-79] SAF
- **Quem faz:** Apple Vision Pro (spatial audio 3D), SoundSight (sonificacao)
- **Classificacao:** **ESSENCIAL** — muda completamente a experiencia. Em vez de TTS dizendo "carro", o usuario OUVE o alerta vindo da direcao real do carro.

### 3.2 Sonificacao de mapa de profundidade
- **O que e:** Converter o mapa de profundidade inteiro em paisagem sonora continua (nao so alertas pontuais)
- **Como fazer:**
  - **EchoSee** (~60fps sonificacao 3D em tempo real) [REF-80]
  - Tecnicas validadas: stereo panning (direcao), frequencia (distancia), loudness (tamanho), BRR (aproximacao) [REF-81]
  - The vOICe system (classico: eixo X → tempo, eixo Y → frequencia, brilho → volume) [REF-82]
- **Referencia:** [REF-80] EchoSee; [REF-81] Sonification techniques; [REF-82] The vOICe
- **Classificacao:** **INTERESSANTE** — poderoso mas requer treinamento do usuario. Modo avancado para power users.

### 3.3 Mapeamento frequencia-distancia validado empiricamente
- **O que e:** Funcoes psicofisicas que mapeiam distancia visual para frequencia sonora/vibratoria de forma intuitiva
- **Como fazer:**
  - Estudo 2025 validou mapeamentos cross-modais para substituicao sensorial [REF-83]
  - Aplicar: mais perto = pitch mais alto (ou vibracao mais rapida)
- **Referencia:** [REF-83] Auditory/tactile frequency mapping (PMC 2025)
- **Classificacao:** **INTERESSANTE** — fundamenta cientificamente o design do feedback

---

## CATEGORIA 4: FEEDBACK HAPTICO (TATO/VIBRACAO)

### 4.1 Pulseira/cinto haptico direcional
- **O que e:** 4-8 motores de vibracao em pulseira ou cinto indicam direcao e distancia de obstaculos
- **Como fazer:**
  - DIY: Arduino + 4-8 ERM ou LRA motors (~$50) [REF-84]
  - Padroes: motor que vibra = direcao do obstaculo, intensidade = proximidade
  - bHaptics TactSuit como referencia de design (40 pontos, $549) [REF-84]
- **Referencia:** [REF-84] Haptic feedback systems (gaming)
- **Quem faz:** dotLumen (puxao na testa, 100Hz), WeWalk (vibracao no cabo), Glidance (handle steering)
- **Classificacao:** **INTERESSANTE** — complemento ao audio, funciona em ambientes barulhentos

### 4.2 WOAD — Wearable Obstacle Avoidance Device
- **O que e:** Oculos (~400g) + smartphone com compressao de video assistida por profundidade + fusao cross-modal. 100% collision avoidance, <320ms resposta, 11h bateria.
- **Como fazer:**
  - Open-source completo: https://github.com/MMCNJUPT/WOAD [REF-85]
  - Nature Communications 2025 — validacao cientifica solida
  - Adaptar arquitetura: usar camera ClaraVis + profundidade → fusao → alerta haptico
- **Referencia:** [REF-85] WOAD (Nature Communications 2025)
- **Classificacao:** **INTERESSANTE** — referencia de ponta, open-source, validado cientificamente

### 4.3 Unfolding Space Glove
- **O que e:** Luva com camera ToF que converte profundidade em vibracoes na mao
- **Como fazer:**
  - Open-source completo: https://github.com/jakobkilian/unfolding-space [REF-86]
  - Testado com cegos (n=8) — funciona em todas as condicoes de iluminacao
- **Referencia:** [REF-86] Unfolding Space Glove
- **Classificacao:** **BULLSHIT para ClaraVis** — conceito interessante mas conflita com o approach de overlay visual. Luva e desconfortavel para uso diario. Melhor como acessorio opcional para cegos totais.

### 4.4 BrainPort (eletrotactil na lingua)
- **O que e:** Camera → padroes eletricos na lingua que o cerebro aprende a interpretar como visao
- **Como fazer:** Patented, FDA-approved, $7.995, requer 10h treinamento com instrutor [REF-87]
- **Referencia:** [REF-87] BrainPort Vision Pro (Wicab)
- **Classificacao:** **BULLSHIT para ClaraVis** — tecnologia fascinante mas nicho extremo. Nao e escalavel, preco absurdo, requer treinamento presencial. Nao compete com AR overlays.

---

## CATEGORIA 5: DISPLAY VISUAL / AR OVERLAY

### 5.1 Overlays visuais em tempo real (contornos, labels, alertas)
- **O que e:** Desenhar contornos de objetos detectados, labels, setas de direcao e alertas de perigo em display AR
- **Como fazer:**
  - Usar resultados do YOLO (bounding boxes) + profundidade para renderizar overlays
  - Display: Xreal Light ($699, birdbath, 1920x1080/eye) ou RayNeo X3 Pro (microLED waveguide) [REF-88]
  - Contrast enhancement + edge highlighting comprovados eficazes para baixa visao [REF-88]
- **Referencia:** [REF-88] HUD/AR display technologies
- **Quem faz:** NINGUEM faz isso para deficientes visuais em hardware acessivel. Apple Vision Pro ($3,499, 650g) e o unico com AR overlay real, mas nao e produto assistivo.
- **Classificacao:** **ESSENCIAL** — PRINCIPAL DIFERENCIAL DO CLARAVIS. Nenhum concorrente oferece overlays visuais AR otimizados para DV em hardware acessivel.

### 5.2 Magnificacao/zoom em tempo real
- **O que e:** Ampliar imagem da camera e exibir em display para baixa visao
- **Como fazer:**
  - Camera HD (12MP+) → crop digital + upscale → display OLED/microLED
  - eSight Go faz ate 24x zoom, NuEyes ate 18x [REF-89]
  - ClaraVis pode oferecer zoom variavel por gesto ou voz
- **Referencia:** [REF-89] eSight/NuEyes specs
- **Quem faz:** eSight (24x, $4.950), NuEyes (18x, ~$5.995), IrisVision (14x, descontinuado)
- **Classificacao:** **ESSENCIAL** — feature basica que todo dispositivo low-vision precisa

### 5.3 Enhancement de contraste e bordas
- **O que e:** Aumentar contraste, destacar bordas, inverter cores — ajuda residuo visual
- **Como fazer:**
  - Filtros em tempo real no pipeline de video: high-pass filter para bordas, LUT para contraste
  - Modos: alto contraste, inversao, escala de cinza, filtros para daltonismo
  - Apple Vision Pro oferece filtros de cor para daltonismo [REF-90]
- **Referencia:** [REF-90] Apple Vision Pro accessibility
- **Classificacao:** **ESSENCIAL** — baixo custo computacional, alto impacto para baixa visao

### 5.4 Night vision digital
- **O que e:** Camera com sensor de alta sensibilidade (STARVIS 2) + iluminacao NIR para ver no escuro
- **Como fazer:**
  - Sensor Sony STARVIS 2 ($50-200) + LED NIR ($5) [REF-91]
  - Camera infravermelha invisivel ao olho + display mostra imagem amplificada
  - Digital NV nao tem restricao de exportacao (diferente de tubos Gen3+)
- **Referencia:** [REF-91] Digital night vision
- **Quem faz:** Nenhum concorrente assistivo oferece night vision
- **Classificacao:** **INTERESSANTE** — caminhada noturna e extremamente perigosa para DV. Diferencial forte.

---

## CATEGORIA 6: VLM (VISUAL LANGUAGE MODELS) — DESCRICAO DE CENA

### 6.1 Descricao de cena por VLM on-device
- **O que e:** Modelo que olha a camera e descreve em linguagem natural o que ve ("voce esta em uma calcada com um poste a 2m a direita e um carro estacionado a esquerda")
- **Como fazer:**
  - **SmolVLM2-500M** — menor VLM capaz de video, potencialmente viavel no Jetson 16GB [REF-92]
  - **LLaVA-BindPW** — LLaVA com MoE otimizado para acessibilidade (7B, quantizavel) [REF-93]
  - **Llama 3.2 1B** no Snapdragon AR1+ Gen 1 — roda on-device em glasses [REF-94]
  - No Jetson: rodar SmolVLM2-500M quantizado junto com YOLO + Depth
- **Referencia:** [REF-92] SmolVLM2; [REF-93] LLaVA-BindPW; [REF-94] Snapdragon AR1+
- **Quem faz:** Envision (GPT-5 cloud), Be My Eyes (GPT-4o cloud), Seeing AI (cloud), Ray-Ban Meta (Llama cloud)
- **Classificacao:** **ESSENCIAL** (no Jetson/glasses) — todos os concorrentes top tem. ClaraVis PRECISA disso, mas OFFLINE. SmolVLM2-500M e o caminho.

### 6.2 WalkVLM — VLM especifico para caminhada
- **O que e:** Benchmark + modelo VLM otimizado para assistencia na caminhada (12K anotacoes de video, chain-of-thought hierarquico)
- **Como fazer:**
  - Dataset disponivel: https://walkvlm2024.github.io/ [REF-95]
  - WalkVLM v2 reduz redundancia de output [REF-96]
  - Fine-tune SmolVLM2 ou LLaVA no dataset WalkVLM para assistencia especifica
- **Referencia:** [REF-95] WalkVLM; [REF-96] WalkVLM v2
- **Classificacao:** **INTERESSANTE** — dataset valioso para treinar VLM especifico quando habilitar no Jetson

---

## CATEGORIA 7: OCR E LEITURA DE TEXTO

### 7.1 OCR on-device (leitura de placas, etiquetas, documentos)
- **O que e:** Reconhecer texto em imagens da camera e ler em voz alta
- **Como fazer:**
  - **Google ML Kit Text Recognition v2** — mais facil no Android, on-device, gratis [REF-97]
  - **PaddleOCR v3.0** — 9.6MB mobile model, 100+ idiomas incluindo portugues, open-source [REF-98]
  - Implementar: camera → detector de texto → OCR → TTS
- **Referencia:** [REF-97] ML Kit OCR v2; [REF-98] PaddleOCR
- **Quem faz:** OrCam (core feature), Envision, Seeing AI, Sullivan+
- **Classificacao:** **ESSENCIAL** — ler placas de rua, precos, remedios, cardapios. Todo concorrente tem.

---

## CATEGORIA 8: RECONHECIMENTO FACIAL

### 8.1 Reconhecimento facial on-device (identificar pessoas conhecidas)
- **O que e:** Registrar rostos de familiares/amigos e anunciar quando detectados
- **Como fazer:**
  - **InsightFace / InspireFace SDK** — 64MB RAM, roda em ARM/Jetson, open-source [REF-99]
  - **EdgeFace** — otimizado para edge devices [REF-100]
  - Privacy: usar datasets sinteticos para treino (GDPR-compliant) [REF-101]
  - Armazenar embeddings localmente (nunca enviar para cloud)
- **Referencia:** [REF-99] InsightFace; [REF-100] EdgeFace; [REF-101] Synthetic face datasets
- **Quem faz:** OrCam (core), Envision, Seeing AI, Sullivan+, Google Lookout
- **Classificacao:** **ESSENCIAL** — feature esperada. "Quem e essa pessoa?" e pergunta constante.

---

## CATEGORIA 9: NAVEGACAO E LOCALIZACAO

### 9.1 SLAM visual (mapeamento + localizacao indoor)
- **O que e:** Construir mapa 3D do ambiente e se localizar nele — saber onde esta dentro de um predio
- **Como fazer:**
  - **ORB-SLAM3** — visual, visual-inertial, multi-mapa, open-source (GPLv3) [REF-102]
  - **SELM-SLAM3** — ORB-SLAM3 + deep features (SuperPoint+LightGlue), 87.84% melhor [REF-103]
  - **PhoneGuide-SLAM** — SLAM de smartphone para navegacao indoor de cegos [REF-104]
  - No Jetson: ORB-SLAM3 com camera + IMU do dispositivo
- **Referencia:** [REF-102] ORB-SLAM3; [REF-103] SELM-SLAM3; [REF-104] PhoneGuide-SLAM
- **Quem faz:** dotLumen (SLAM proprio), Glidance (mapeamento proprio)
- **Classificacao:** **ESSENCIAL** (para versao Jetson/glasses) — transforma ClaraVis de "detector" em "navegador"

### 9.2 SLAM semantico (mapa com significado)
- **O que e:** SLAM que rotula o mapa: "porta", "escada", "elevador", nao apenas "obstaculo em X,Y"
- **Como fazer:**
  - **Kimera** (MIT-SPARK, metrico-semantico em tempo real) [REF-105]
  - **Hydra** (grafos de cena 3D a partir de sensores) [REF-106]
  - Combinacao: YOLO identifica objetos → SLAM posiciona no mapa → resultado: mapa semantico
- **Referencia:** [REF-105] Kimera; [REF-106] Hydra
- **Classificacao:** **INTERESSANTE** — poderoso mas computacionalmente pesado. Fase 3-4 do roadmap.

### 9.3 Navegacao indoor UWB (ultra-wideband)
- **O que e:** Posicionamento indoor com precisao centimetrica usando radio UWB
- **Como fazer:**
  - **Qorvo DW3000** (~$15/modulo, 10cm precisao) ou **ESP32-UWB** (~$15 DIY) [REF-107]
  - Ja comprovado: NTT Data no estadio de 20K lugares em Londres (usuario cego encontrou assento) [REF-107]
  - Depende de infraestrutura (anchors UWB no local)
  - Mercado UWB: $2.0B (2025) → $3.3B (2028), 18.9% CAGR
- **Referencia:** [REF-107] UWB indoor positioning
- **Classificacao:** **INTERESSANTE** — excelente quando infraestrutura existir. ClaraVis pode ser pioneiro em BR.

### 9.4 Navegacao indoor BLE beacons
- **O que e:** Posicionamento indoor via beacons Bluetooth (1-3m precisao)
- **Como fazer:**
  - iBeacon/Eddystone — beacons $5-15 cada [REF-108]
  - Bluetooth 5.1 AoA melhora para ~0.5m [REF-108]
  - Muitos predios ja tem BLE para marketing — ClaraVis pode pegar "de graca"
- **Referencia:** [REF-108] BLE beacons
- **Quem faz:** Lazarillo (GPS outdoor), WeWalk (GPS + BLE)
- **Classificacao:** **INTERESSANTE** — mais facil que UWB, menos preciso, infraestrutura existente

### 9.5 GPS acessivel com pontos de interesse
- **O que e:** Navegacao GPS com anuncio de POIs (lojas, semaforos, paradas de onibus)
- **Como fazer:**
  - Integrar Google Maps API ou OpenStreetMap com overlay sonoro
  - Lazarillo faz isso bem — 250K+ usuarios em 29 paises, gratis [REF-109]
- **Referencia:** [REF-109] Lazarillo
- **Classificacao:** **ESSENCIAL** — feature basica, facil de implementar via API

---

## CATEGORIA 10: VISAO TERMICA E NOTURNA

### 10.1 Camera termal (FLIR Lepton)
- **O que e:** Detectar calor — pessoas no escuro, superficies quentes, animais, pocas geladas
- **Como fazer:**
  - **FLIR Lepton XDS** (lancado fev. 2026): 160x120 termal + 5MP visivel, fusao MSX, $109-239 [REF-110]
  - Tamanho de moeda, 150-200mW — projetado para wearables
  - Integrar como segundo input no pipeline de fusao sensorial
- **Referencia:** [REF-110] FLIR Lepton XDS
- **Quem faz:** NINGUEM no mercado assistivo usa termal
- **Classificacao:** **INTERESSANTE** — diferencial unico, preco acessivel, preenche lacuna noturna

---

## CATEGORIA 11: HARDWARE / PLATAFORMA

### 11.1 Jetson Orin NX 16GB (belt-pack compute)
- **O que e:** Modulo GPU para IA na borda — roda YOLO + Depth + SLAM + VLM simultaneamente
- **Specs:** 100-157 TOPS, 10-25W, 70x45mm, $599 [REF-111]
- **JetPack 6.2 Super Mode** dobra performance de inferencia [REF-111]
- **Como usar:** Belt-pack conectado ao display AR via cabo, camera no frame dos oculos
- **Classificacao:** **ESSENCIAL** — ja no roadmap ClaraVis (reComputer Super J4012, $950)

### 11.2 Hailo-8 AI accelerator (on-glasses)
- **O que e:** Acelerador de IA dedicado, tamanho de moeda, para rodar YOLO direto nos oculos
- **Specs:** 26 TOPS, 2.5W, menor que moeda, M.2 interface [REF-112]
- **Como usar:** Integrar no frame dos oculos para YOLO on-glasses sem Jetson
- **Referencia:** [REF-112] Hailo-8
- **Classificacao:** **FUTURO** — fantastico mas integracao em glasses custom e complexa. Fase 3+.

### 11.3 Snapdragon AR1+ Gen 1 (all-in-one glasses chip)
- **O que e:** Chip projetado para smart glasses com IA — roda Llama 3.2 1B on-device
- **Specs:** Display binocular 1280x1280, camera 12MP, 14-bit ISP, 4nm [REF-94]
- **Como usar:** Plataforma all-in-one para ClaraVis glasses standalone
- **Referencia:** [REF-94] Snapdragon AR1+
- **Quem faz:** Ray-Ban Meta (AR1 Gen 1), RayNeo X3 Pro (upcoming)
- **Classificacao:** **ESSENCIAL** (longo prazo) — o chip que torna ClaraVis glasses standalone viavel

### 11.4 Oculos AR open-source (referencia de hardware)
- **O que e:** Designs open-source de smart glasses para referencia
- **Como fazer:**
  - **OpenGlass** ($20, ESP32S3, basico mas funcional) [REF-113]
  - **Open Source Smart Glasses** (TeamOpenSmartGlasses, PCB + frame 3D printado) [REF-114]
  - **Brilliant Labs Frame** (AR glasses open-source com display) [REF-115]
  - **Focus AI Glasses** (capstone project, similar goals) [REF-116]
- **Referencia:** [REF-113] OpenGlass; [REF-114] Open Source Smart Glasses; [REF-115] Brilliant Labs Frame; [REF-116] Focus AI Glasses
- **Classificacao:** **INTERESSANTE** — referencia de design, nao para produzir identico

---

## CATEGORIA 12: INTEGRACAO SMART HOME / IoT

### 12.1 Integracao Matter protocol
- **O que e:** Conectar ClaraVis com dispositivos smart home (luzes, portas, eletrodomesticos)
- **Como fazer:** Matter SDK, 300+ dispositivos certificados (2025) [REF-117]
- **Classificacao:** **BULLSHIT** (por enquanto) — nice-to-have mas nao resolve problema core de mobilidade

### 12.2 Rastreamento de objetos pessoais (UWB tags)
- **O que e:** Tags UWB em objetos (chaves, carteira, mochila) para localizar pelo audio 3D
- **Como fazer:** Tags BLE/UWB ($15-30 cada) + audio espacial no ClaraVis [REF-107]
- **Classificacao:** **BULLSHIT** — AirTag/SmartTag ja fazem isso. ClaraVis nao precisa reinventar.

---

## CATEGORIA 13: SENSORY SUBSTITUTION (SUBSTITUICAO SENSORIAL)

### 13.1 Visao-para-audio (The vOICe style)
- **O que e:** Converter imagem inteira em paisagem sonora (eixo X → tempo, Y → frequencia, brilho → volume)
- **Como fazer:** Algoritmo bem documentado, roda em qualquer device [REF-82]
- **Classificacao:** **BULLSHIT para ClaraVis** — requer semanas/meses de treinamento. Approach de nicho acadêmico, nao escalavel. ClaraVis tem DISPLAY VISUAL — nao precisa substituir visao por som.

### 13.2 Luva haptica para profundidade
- **O que e:** Ver analise em 4.3 (Unfolding Space Glove)
- **Classificacao:** **BULLSHIT para ClaraVis** — ja classificado acima

---

## CATEGORIA 14: TRACKING E INTERACAO

### 14.1 Eye tracking (rastreamento do olhar)
- **O que e:** Saber para onde o usuario esta olhando e fornecer info sobre aquela regiao
- **Como fazer:**
  - **Pupil Labs** (open-source, LGPL) [REF-118]
  - Cameras NIR + corneal reflection — ja integrado em VR headsets
  - Util para: zoom automatico na regiao do olhar, descricao do que o usuario tenta ver
- **Referencia:** [REF-118] Pupil Labs
- **Quem faz:** Apple Vision Pro (core), Meta Quest
- **Classificacao:** **FUTURO** — relevante para baixa visao (nao cegos totais). Fase 3-4.

### 14.2 Gestos por micro-camera (Ultraleap Helios)
- **O que e:** Camera de eventos 3x4mm, 20mW, para gestos sem mao (micro-gestos no frame dos oculos)
- **Como fazer:** Ultraleap Helios SDK (alpha) [REF-119]
- **Referencia:** [REF-119] Ultraleap Helios
- **Classificacao:** **FUTURO** — elegante mas nao prioritario. Voz e mais acessivel para DV.

### 14.3 Person tracking / Re-ID
- **O que e:** Seguir pessoa especifica em multidao (acompanhante)
- **Como fazer:**
  - **ByteTrack** (open-source, leve) [REF-120]
  - **DeepSORT** (Re-ID + tracking) [REF-120]
- **Referencia:** [REF-120] ByteTrack/DeepSORT
- **Classificacao:** **INTERESSANTE** — util para nao perder acompanhante em multidao

---

## CATEGORIA 15: PESQUISA BRASILEIRA RELEVANTE

### 15.1 NavWear (UNESP + UFES)
- **O que e:** Mochila com Jetson Nano + camera RGB-D Intel RealSense + motores hapticos nas alças. Testado com cegos — menos colisoes, mais segurança percebida vs. bengala sozinha.
- **Financiamento:** FAPESP Grant 2019/14438-4 [REF-121]
- **Classificacao:** **REFERENCIA** — projeto brasileiro mais proximo do ClaraVis. Potencial parceiro de pesquisa/colaboracao.

### 15.2 UFRB RFID em piso tatil
- **O que e:** Tags RFID em piso tatil + leitor no sapato — fornece info sobre localizacao
- **Referencia:** [REF-122] Prof. Joao Neto, UFRB
- **Classificacao:** **BULLSHIT para ClaraVis** — depende de infraestrutura fisica instalada em cada local

### 15.3 UFBA — Analise de patentes assistivas brasileiras
- **O que e:** Panorama de propriedade intelectual em tech assistiva no Brasil
- **Referencia:** [REF-123] UFBA Cadernos de Prospeccao
- **Classificacao:** **REFERENCIA** — util para entender landscape de PI no Brasil

---

## RESUMO: MATRIZ DE PRE-SELECAO

### ESSENCIAIS (implementar obrigatoriamente)

| # | Feature | Fase | Custo Est. | Complexidade |
|---|---------|------|-----------|-------------|
| 1 | Deteccao YOLO on-device (upgrade YOLO11/v10) | 1 | $0 | Baixa |
| 2 | Profundidade monocular (Depth Anything V2) | 1 | $0 | Media |
| 3 | Audio espacial 3D (3DTI/Steam Audio) | 1 | $0 | Media |
| 4 | OCR on-device (ML Kit ou PaddleOCR) | 1 | $0 | Baixa |
| 5 | Deteccao de semaforos (LYTNet) | 1 | $0 | Media |
| 6 | Reconhecimento facial (InsightFace) | 1 | $0 | Media |
| 7 | GPS acessivel com POIs | 1 | $0 | Baixa |
| 8 | Overlays visuais AR (contornos, labels, alertas) | 2 | $700-3.000 | Alta |
| 9 | Magnificacao/zoom em tempo real | 2 | $0 (software) | Baixa |
| 10 | Enhancement de contraste e bordas | 2 | $0 (software) | Baixa |
| 11 | VLM on-device (SmolVLM2-500M) | 2 | $0 | Alta |
| 12 | SLAM visual (ORB-SLAM3) | 2 | $0 | Alta |
| 13 | Jetson Orin NX belt-pack | 2 | $950 | Media |
| 14 | Snapdragon AR1+ glasses (longo prazo) | 4 | TBD | Muito Alta |

### INTERESSANTES (avaliar caso a caso)

| # | Feature | Fase | Custo Est. | Justificativa |
|---|---------|------|-----------|---------------|
| 1 | Segmentacao de calcada (QPULM) | 2-3 | $0 | Melhor que bboxes para navegacao |
| 2 | Camera stereo (OAK-D Lite) | 2 | $149 | Profundidade mais precisa |
| 3 | Sonificacao de profundidade | 2 | $0 | Modo avancado para power users |
| 4 | Pulseira/cinto haptico | 2 | $50 DIY | Complemento ao audio |
| 5 | WOAD (obstacle avoidance) | 2 | $0 (open-source) | Referencia de ponta |
| 6 | Night vision digital | 3 | $50-200 | Caminhada noturna segura |
| 7 | FLIR Lepton XDS termal | 3 | $109-239 | Unico no mercado |
| 8 | SLAM semantico (Kimera) | 3 | $0 | Mapa com significado |
| 9 | UWB indoor nav | 3 | $15-50 | Depende de infraestrutura |
| 10 | Person tracking (ByteTrack) | 2 | $0 | Nao perder acompanhante |
| 11 | WalkVLM dataset para fine-tune | 2 | $0 | Melhora VLM para caminhada |
| 12 | Deteccao de pedestres (LP-YOLO) | 1 | $0 | Upgrade especifico |

### BULLSHIT (descartados)

| # | Feature | Motivo |
|---|---------|--------|
| 1 | Unfolding Space Glove | Conflita com AR overlay; luva desconfortavel |
| 2 | BrainPort eletrotactil | Nicho extremo, $8K, 10h treinamento presencial |
| 3 | vOICe sensory substitution | Semanas de treinamento; ClaraVis TEM display visual |
| 4 | Matter smart home | Nice-to-have; nao resolve mobilidade |
| 5 | UWB object tracking | AirTag ja faz; nao e core |
| 6 | RFID em piso tatil | Depende de infraestrutura fisica em cada local |
| 7 | Intel Movidius | Descontinuado, superado |
| 8 | Amlogic A311D | Datado (2019), alternativas melhores |
| 9 | Full LiDAR automotivo | Pesado, caro, overkill para wearable |

### FUTURO (monitorar para fase 3-4)

| # | Feature | Quando | Trigger |
|---|---------|--------|---------|
| 1 | Hailo-8 on-glasses | Quando design de glasses custom comecar | Fase 3 |
| 2 | Eye tracking | Quando glasses tiver camera NIR | Fase 3-4 |
| 3 | Micro-gestos (Helios) | Quando SDK amadurecer | Fase 4 |
| 4 | LiDAR chip-scale | Quando custo cair < $30 | Fase 3 |
| 5 | mmWave radar | Quando integrar em glasses frame | Fase 3-4 |
| 6 | Sensor fusion completo | Quando tiver multiplos sensores | Fase 3-4 |

---

## ROADMAP DE IMPLEMENTACAO

### Fase 1: Software-Only (0-3 meses, $0)
> Hardware existente: Xiaomi Redmi Note 9 (Helio G85) + futuro Jetson Orin NX

1. Upgrade modelo YOLO11n ou YOLOv10n (TFLite)
2. Integrar Google ML Kit OCR v2 (Android)
3. Integrar LYTNet para semaforos (modelo separado)
4. Adicionar InsightFace para reconhecimento facial
5. GPS acessivel com OpenStreetMap POIs
6. Preparar Depth Anything V2 Small para Jetson (ONNX/TensorRT)
7. Preparar 3DTI Toolkit ou Steam Audio para audio espacial
8. Preparar ORB-SLAM3 para Jetson

### Fase 2: Jetson + Display (3-12 meses, ~$1.650)
> Hardware: reComputer Super J4012 ($950) + display AR ($700)

9. Deploy YOLO + Depth Anything V2 + SLAM no Jetson (pipeline unificado)
10. Audio espacial 3D: obstaculos emitem som da direcao real
11. SmolVLM2-500M quantizado no Jetson para descricao de cena
12. Overlays visuais AR (contornos coloridos por tipo, labels, distancia)
13. Magnificacao + enhancement de contraste no display
14. Prototipo haptico (pulseira Arduino + 4 motors, $50)

### Fase 3: Sensores Adicionais (12-24 meses, ~$500-1.000)
> Hardware: FLIR Lepton XDS ($109-239) + OAK-D Lite ($149) + UWB ($30)

15. Fusao camera + termal + stereo depth
16. Night vision com NIR + STARVIS
17. Indoor nav UWB (parceria com locais-piloto)
18. SLAM semantico (Kimera no Jetson)
19. Person tracking (ByteTrack)

### Fase 4: Glasses Standalone (24-36 meses, TBD)
> Hardware: Custom glasses com Snapdragon AR1+ ou Hailo-8

20. Port completo para glasses standalone
21. LLM on-glasses (Llama 3.2 1B no AR1+)
22. Full sensor fusion (camera + depth + thermal + radar + UWB)
23. Costmap navigation adaptado de Nav2
24. Eye tracking + micro-gestos

---

## OPORTUNIDADES DE COLABORACAO ACADEMICA (BRASIL)

| Instituicao | Projeto | Sinergia com ClaraVis | Acao |
|-------------|---------|----------------------|------|
| UNESP + UFES | NavWear (Jetson + haptico) | Mesma plataforma, feedback haptico | Contatar via FAPESP Grant |
| UFRB | Piso tatil RFID | Infraestrutura complementar | Monitorar |
| UFBA | Patentes assistivas | Landscape de PI | Consultar publicacao |
| USP/Unicamp | Pesquisa em CV/IA | Potencial co-orientacao | Buscar grupos de acessibilidade |

---

*Documento compilado em 2026-03-11. Baseado em pesquisa de 20 produtos concorrentes, 50+ papers academicos e 8 industrias. Todas as fontes em REFERENCIAS.md.*
