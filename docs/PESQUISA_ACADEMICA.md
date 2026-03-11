# ClaraVis — Academic Research & Open-Source Projects Survey

> **Version:** 1.0 | **Date:** 2026-03-11
> **Scope:** Papers and open-source projects relevant to assistive technology for visually impaired people (2022-2026)
> **Focus:** Real-time, on-device/edge inference, open-source implementations

---

## 1. Real-Time Object Detection for Blind/Visually Impaired

### 1.1 YOLOv8 on Android for Visually Impaired
- **Title:** Empowering the Visually Impaired: YOLOv8-based Object Detection in Android Applications
- **Year:** 2025 | **Venue:** ScienceDirect (Procedia Computer Science)
- **Key contribution:** Converted YOLOv8 Nano to TFLite for Android; achieved mAP50 79.9%, inference ~350ms at 640x640
- **Open-source:** Not specified
- **Hardware:** Android smartphone (TFLite)
- **Relevance to ClaraVis:** **HIGH** -- directly comparable to ClaraVis Android architecture (YOLO + TFLite)
- **URL:** https://www.sciencedirect.com/science/article/pii/S1877050925000055

### 1.2 Nav-YOLO: Lightweight Indoor Navigation
- **Title:** Nav-YOLO: A Lightweight and Efficient Object Detection Model for Real-Time Indoor Navigation on Mobile Platforms
- **Authors:** Not specified | **Year:** 2025 | **Venue:** MDPI ISPRS International Journal of Geo-Information
- **Key contribution:** ShuffleNetv2-based backbone with Slim-Neck and HyCTAS compact detection head, derived from YOLOv8n for indoor navigation
- **Open-source:** Yes -- https://github.com/back2-thebasic/NavYolo
- **Hardware:** Mobile platforms
- **Relevance to ClaraVis:** **HIGH** -- lightweight YOLO variant optimized for navigation, open-source
- **URL:** https://www.mdpi.com/2220-9964/14/9/364

### 1.3 YOLO-OD: Obstacle Detection for Visually Impaired
- **Title:** YOLO-OD: Obstacle Detection for Visually Impaired Navigation Assistance
- **Year:** 2024 | **Venue:** PMC / MDPI Sensors
- **Key contribution:** Specialized YOLO variant for obstacle categories relevant to blind navigation
- **Open-source:** Yes (code publicly available)
- **Hardware:** Edge devices
- **Relevance to ClaraVis:** **HIGH** -- directly targets same use case (obstacle detection for blind)
- **URL:** https://pmc.ncbi.nlm.nih.gov/articles/PMC11645096/

### 1.4 YOLO-Extreme: Foggy Weather Obstacle Detection
- **Title:** YOLO-Extreme: Obstacle Detection for Visually Impaired Navigation Under Foggy Weather
- **Year:** 2025 | **Venue:** PMC
- **Key contribution:** Enhanced YOLOv12 framework for robust navigation assistance in adverse weather (fog)
- **Open-source:** Not specified
- **Hardware:** Edge devices
- **Relevance to ClaraVis:** **MEDIUM** -- addresses weather robustness, relevant for outdoor use
- **URL:** https://pmc.ncbi.nlm.nih.gov/articles/PMC12300934/

### 1.5 Assistive Eye: YOLO on Edge Devices
- **Title:** Assistive Eye: A Comparative Analysis of YOLO Object Detection Models on Edge Devices
- **Year:** 2024 | **Venue:** ACM IC3 2024
- **Key contribution:** Benchmarks YOLOv8n on Raspberry Pi 8GB for real-time navigation assistance
- **Open-source:** Not specified
- **Hardware:** Raspberry Pi 8GB RAM
- **Relevance to ClaraVis:** **HIGH** -- edge device benchmarks directly applicable to Jetson deployment
- **URL:** https://dl.acm.org/doi/10.1145/3675888.3676037

### 1.6 YOLOv8 XR Smart Glasses for Visually Impaired
- **Title:** YOLOv8-Based XR Smart Glasses Mobility Assistive System for Aiding Outdoor Walking of Visually Impaired Individuals in South Korea
- **Year:** 2025 | **Venue:** MDPI Electronics
- **Key contribution:** Multiple YOLOv8 model sizes (nano to xlarge) deployed on smart glasses platform
- **Open-source:** Not specified
- **Hardware:** XR smart glasses (mobile SoC)
- **Relevance to ClaraVis:** **HIGH** -- smart glasses + YOLO, directly relevant to ClaraVis AR glasses roadmap
- **URL:** https://www.mdpi.com/2079-9292/14/3/425

### 1.7 RT-DETR: Real-Time Detection Transformer
- **Title:** DETRs Beat YOLOs on Real-time Object Detection (RT-DETR)
- **Authors:** Wenyu Lv et al. (Baidu) | **Year:** 2024 | **Venue:** CVPR 2024
- **Key contribution:** First real-time end-to-end transformer-based detector; eliminates NMS post-processing; efficient hybrid encoder for multiscale features
- **Open-source:** Yes -- https://github.com/lyuwenyu/RT-DETR (Paddle + PyTorch)
- **Hardware:** GPU; edge deployment on Jetson AGX Xavier demonstrated
- **Relevance to ClaraVis:** **HIGH** -- potential YOLO alternative for Jetson Orin NX, NMS-free pipeline
- **URL:** https://github.com/lyuwenyu/RT-DETR

### 1.8 RT-DETRv3 (WACV 2025 Oral)
- **Title:** RT-DETRv3: Real-time End-to-End Object Detection with Hierarchical Dense Positive Supervision
- **Year:** 2025 | **Venue:** WACV 2025 (Oral)
- **Key contribution:** Hierarchical dense positive supervision for improved training
- **Open-source:** Yes -- https://github.com/clxia12/RT-DETRv3
- **Hardware:** GPU, edge devices
- **Relevance to ClaraVis:** **MEDIUM** -- incremental improvement over RT-DETR
- **URL:** https://github.com/clxia12/RT-DETRv3

### 1.9 RT-DETRv4: Vision Foundation Models
- **Title:** RT-DETRv4: Painlessly Furthering Real-Time Object Detection with Vision Foundation Models
- **Year:** 2025 | **Venue:** arXiv
- **Key contribution:** Leverages vision foundation models (DINOv2) to boost RT-DETR performance
- **Open-source:** Yes -- https://github.com/RT-DETRs/RT-DETRv4
- **Hardware:** GPU; TensorRT export available
- **Relevance to ClaraVis:** **MEDIUM** -- larger models, better for Jetson than mobile
- **URL:** https://arxiv.org/html/2510.25257v1

### 1.10 YOLO11 / YOLOv10 / YOLOv9 Comparison
- **Title:** YOLO Evolution: A Comprehensive Benchmark and Architectural Review
- **Year:** 2024-2025 | **Venue:** arXiv
- **Key findings:**
  - **YOLO11:** C3k2 blocks + C2PSA spatial attention; fewer params than YOLOv8; TFLite/TensorRT/CoreML/ONNX export
  - **YOLOv10:** NMS-free (Tsinghua); faster but slightly lower accuracy for overlapping objects
  - **YOLOv9:** Best feature fidelity via PGI; heavier compute
  - **YOLO26:** Latest (2025), further architectural improvements
- **Open-source:** All via Ultralytics -- https://github.com/ultralytics/ultralytics
- **Hardware:** All support TFLite (Android), TensorRT (Jetson), CoreML (iOS)
- **Relevance to ClaraVis:** **HIGH** -- direct upgrade path for ClaraVis model; YOLO11n or YOLOv10n recommended
- **URL:** https://arxiv.org/html/2411.00201v4

### 1.11 EfficientDet-Lite for Blind Users with Haptic Feedback
- **Title:** Development and Evaluation of a Tool for Blind Users Utilizing AI Object Detection and Haptic Feedback
- **Year:** 2025 | **Venue:** MDPI Machines
- **Key contribution:** EfficientDet-lite2 on smartphone with haptic + audio feedback for blind users; COCO dataset; multilingual
- **Performance:** 33.97% mAP at 69ms on Pixel 4 CPU
- **Open-source:** Not specified
- **Hardware:** Android smartphone (TFLite)
- **Relevance to ClaraVis:** **MEDIUM** -- alternative to YOLO for mobile; lower mAP but fast
- **URL:** https://www.mdpi.com/2075-1702/13/5/398

---

## 2. Depth Estimation on Mobile/Edge

### 2.1 Depth Anything V2
- **Title:** Depth Anything V2: A More Capable Foundation Model for Monocular Depth Estimation
- **Authors:** Yang et al. | **Year:** 2024 | **Venue:** NeurIPS 2024
- **Key contribution:** Synthetic-data training + large teacher model + pseudo-labeled real data; 10x faster than Stable Diffusion-based methods; models from 25M to 1.3B params
- **Open-source:** Yes -- https://github.com/DepthAnything/Depth-Anything-V2
- **ONNX export:** Yes -- https://github.com/fabio-sim/Depth-Anything-ONNX (~0.1s/frame for Small@518x518)
- **Qualcomm deployment:** Available on Qualcomm AI Hub -- https://aihub.qualcomm.com/models/depth_anything_v2
- **Hardware:** GPU (training); ONNX/OpenVINO for edge; Qualcomm AI 100 for mobile
- **Relevance to ClaraVis:** **HIGH** -- monocular depth from single camera; essential for obstacle distance estimation; Small model viable on Jetson
- **URL:** https://github.com/DepthAnything/Depth-Anything-V2

### 2.2 Prompt Depth Anything (4K Metric Depth)
- **Title:** Prompting Depth Anything for 4K Resolution Accurate Metric Depth Estimation
- **Year:** 2024 | **Venue:** arXiv
- **Key contribution:** Uses low-res LiDAR to prompt Depth Anything V2 for 4K metric depth; bridges relative and metric depth
- **Open-source:** Yes -- https://promptda.github.io/
- **Hardware:** GPU; potential Jetson deployment with LiDAR sensor
- **Relevance to ClaraVis:** **MEDIUM** -- metric depth important for accurate distance alerts; requires LiDAR
- **URL:** https://promptda.github.io/assets/main_paper_with_supp.pdf

### 2.3 EfficientDepth
- **Title:** EfficientDepth: A Fast and Detail-Preserving Monocular Depth Estimation Model
- **Year:** 2025 | **Venue:** arXiv
- **Key contribution:** Lightweight depth model preserving fine details while maintaining fast inference
- **Open-source:** Not confirmed
- **Hardware:** Edge-friendly
- **Relevance to ClaraVis:** **MEDIUM** -- potential alternative to Depth Anything for constrained devices
- **URL:** https://arxiv.org/html/2509.22527v1

### 2.4 Survey on Monocular Metric Depth Estimation
- **Title:** Survey on Monocular Metric Depth Estimation
- **Year:** 2025 | **Venue:** arXiv
- **Key contribution:** Comprehensive survey of metric (absolute distance) depth estimation methods
- **Relevance to ClaraVis:** **MEDIUM** -- useful reference for choosing depth model for distance alerts
- **URL:** https://arxiv.org/html/2501.11841v3

---

## 3. Visual Language Models (VLMs) for Blind Accessibility

### 3.1 WalkVLM
- **Title:** WalkVLM: Aid Visually Impaired People Walking by Vision Language Model
- **Authors:** Not specified | **Year:** 2024 | **Venue:** arXiv (2412.20903)
- **Key contribution:** First large-scale walking assistance VLM benchmark; 12K video-annotation pairs from Europe/Asia; chain-of-thought hierarchical planning; temporal-aware adaptive prediction to reduce redundant reminders
- **Open-source:** Dataset and model -- https://walkvlm2024.github.io/
- **Hardware:** GPU server (not real-time on mobile)
- **Relevance to ClaraVis:** **HIGH** -- defines the benchmark for VLM-based walking guidance; dataset could be used for fine-tuning when ClaraVis re-enables VLM on Jetson
- **URL:** https://arxiv.org/abs/2412.20903

### 3.2 WalkVLM v2 (Less Redundancy)
- **Title:** Less Redundancy: Boosting Practicality of Vision Language Model in Walking Assistants
- **Year:** 2025 | **Venue:** arXiv
- **Key contribution:** Reduces verbose/redundant VLM output for more practical walking assistance
- **Open-source:** Not confirmed
- **Hardware:** GPU
- **Relevance to ClaraVis:** **HIGH** -- addresses key ClaraVis pain point (VLM was too slow/verbose on Helio G85)
- **URL:** https://arxiv.org/abs/2508.16070

### 3.3 LLaVA-BindPW (Lightweight Accessibility LLaVA)
- **Title:** Lightweight Visual Accessibility LLaVA Architecture
- **Year:** 2025 | **Venue:** Nature Scientific Reports
- **Key contribution:** Based on Gemma-7B; sparse MoE layers + perceptual weighting for blind assistance; reduces compute vs standard LLaVA
- **Open-source:** Not confirmed
- **Hardware:** GPU (7B params -- feasible on Jetson Orin NX 16GB with quantization)
- **Relevance to ClaraVis:** **HIGH** -- lightweight LLaVA specifically for blind assistance; potential Jetson deployment
- **URL:** https://www.nature.com/articles/s41598-025-23023-w

### 3.4 SmolVLM2 (Compact Video VLM)
- **Title:** SmolVLM2: Video-Instruct Models
- **Year:** 2025 | **Venue:** HuggingFace
- **Key contribution:** 500M and 2.2B parameter video understanding VLMs; consumer hardware friendly
- **Open-source:** Yes (HuggingFace)
- **Hardware:** Consumer GPU; 500M model potentially viable on Jetson Orin NX
- **Relevance to ClaraVis:** **HIGH** -- smallest VLMs capable of video understanding; 500M model could run on Jetson
- **URL:** https://arxiv.org/html/2511.10615

### 3.5 InternVL 2.5
- **Title:** InternVL 2.5: Expanding Performance Boundaries of Open-Source Multimodal Models
- **Year:** 2024 | **Venue:** arXiv / InternVL blog
- **Key contribution:** State-of-the-art open-source multimodal model; multiple size variants
- **Open-source:** Yes -- https://github.com/OpenGVLab/InternVL
- **Hardware:** GPU; quantized smaller variants for edge
- **Relevance to ClaraVis:** **MEDIUM** -- powerful but large; smaller variants could be explored for Jetson
- **URL:** https://internvl.github.io/blog/2024-12-05-InternVL-2.5/

---

## 4. Spatial Audio / 3D Audio for Navigation

### 4.1 EchoSee: Real-Time 3D Sonification
- **Title:** EchoSee: An Assistive Mobile Application for Real-Time 3D Environment Reconstruction and Sonification
- **Authors:** Schwartz, B. S., King, S., Bell, T. | **Year:** 2024 | **Venue:** MDPI Bioengineering
- **Key contribution:** Real-time 3D scanning + spatialized audio soundscape at ~60fps; virtual speakers placed in 3D map; continuous update as user moves
- **Open-source:** Not confirmed
- **Hardware:** Mobile device with 3D scanning capability
- **Relevance to ClaraVis:** **HIGH** -- directly applicable sonification approach; could integrate with ClaraVis depth estimation
- **URL:** https://pmc.ncbi.nlm.nih.gov/articles/PMC11351581/

### 4.2 3D Tune-In Toolkit
- **Title:** 3D Tune-In Toolkit: An open-source library for real-time binaural spatialisation
- **Authors:** Cuevas-Rodriguez et al. | **Year:** 2019 (ongoing) | **Venue:** PLOS ONE
- **Key contribution:** Open-source C++ library for HRTF-based binaural rendering; hearing aid emulation; hearing loss simulation; Unity + JavaScript wrappers
- **Open-source:** Yes -- https://github.com/3DTune-In/3dti_AudioToolkit
- **Hardware:** Cross-platform (desktop, mobile, embedded)
- **Relevance to ClaraVis:** **HIGH** -- ready-to-use binaural rendering library; could spatialize obstacle alerts
- **URL:** https://github.com/3DTune-In/3dti_AudioToolkit

### 4.3 Steam Audio (Valve)
- **Title:** Steam Audio
- **Developer:** Valve Software | **Year:** Ongoing
- **Key contribution:** HRTF-based binaural rendering with minimal frequency coloration; low-latency 3D audio for hundreds of sources; works on mobile
- **Open-source:** Yes -- https://github.com/ValveSoftware/steam-audio
- **Hardware:** Cross-platform including mobile
- **Relevance to ClaraVis:** **HIGH** -- production-quality spatial audio engine; mobile-compatible
- **URL:** https://valvesoftware.github.io/steam-audio/

### 4.4 Systematic Review: Spatial Audio for Blind Users
- **Title:** Inclusion Through Sound: A Systematic Review of Spatial Audio, Sonification, and Interaction Design in Immersive Technologies for Blind and Visually Impaired Users
- **Year:** 2025 | **Venue:** HKBU Scholars
- **Key contribution:** Synthesizes ~1,900 works into 6 domains: spatial audio/HRTFs, assistive navigation, sonification, auditory cognition, immersive system design, inclusive frameworks
- **Relevance to ClaraVis:** **HIGH** -- comprehensive reference for designing ClaraVis audio feedback system
- **URL:** https://scholars.hkbu.edu.hk/en/publications/inclusion-through-sound-a-systematic-review-of-spatial-audio-soni/

### 4.5 Sonification Techniques for Depth Maps
- **Key techniques documented in literature:**
  - **Stereo panning:** Azimuth localization of objects
  - **Loudness:** Object size encoding
  - **Frequency:** Distance encoding (higher pitch = closer)
  - **BRR (Beep Repetition Rate):** Approaching obstacles shorten beep intervals
  - **SFF (Sound Fundamental Frequency):** Pitch increases with proximity
  - **SI (Sound Intensity):** Louder = closer
- **Relevance to ClaraVis:** **HIGH** -- design patterns for converting depth/detection to audio feedback

---

## 5. Haptic Feedback for Navigation

### 5.1 Haptic Navigation Scoping Review (2025)
- **Title:** Navigation Assistance Via Haptic Technology for Blind or Low-Vision Users: A Scoping Review
- **Authors:** Tang, S., Huang, G. | **Year:** 2025 | **Venue:** SAGE Human Factors
- **Key contribution:** Reviews 28 articles on haptic navigation; analyzes hands-free vs. hand-held devices; multimodal (haptic + audio) approaches
- **Relevance to ClaraVis:** **HIGH** -- comprehensive reference for ClaraVis haptic feedback design
- **URL:** https://journals.sagepub.com/doi/10.1177/10711813251360706

### 5.2 WOAD: Wearable Obstacle Avoidance Device (Nature Communications 2025)
- **Title:** A wearable obstacle avoidance device for visually impaired individuals with cross-modal learning
- **Authors:** Gao Yun et al. (NJUPT) | **Year:** 2025 | **Venue:** Nature Communications
- **Key contribution:** Glasses (~400g) + smartphone; depth-aided video compression on custom FPGA; cross-modal feature fusion; 100% collision avoidance; <320ms response; 11-hour battery
- **Open-source:** Yes -- https://github.com/MMCNJUPT/WOAD
- **Hardware:** Custom glasses with FPGA + smartphone
- **Relevance to ClaraVis:** **HIGH** -- state-of-the-art wearable obstacle avoidance; open-source; cross-modal approach highly relevant
- **URL:** https://www.nature.com/articles/s41467-025-58085-x

### 5.3 Unfolding Space Glove
- **Title:** The Unfolding Space Glove: A Wearable Spatio-Visual to Haptic Sensory Substitution Device for Blind People
- **Authors:** Kilian, J. et al. | **Year:** 2022 | **Venue:** MDPI Sensors
- **Key contribution:** ToF camera depth converted to vibrotactile stimuli on hand back; tested with blind (n=8) and blindfolded sighted (n=6); portable, all lighting conditions, no external hardware
- **Open-source:** Yes -- https://github.com/jakobkilian/unfolding-space
- **Hardware:** Time-of-flight camera + vibration motors + microcontroller
- **Relevance to ClaraVis:** **HIGH** -- open-source haptic depth device; could complement ClaraVis AR glasses
- **URL:** https://github.com/jakobkilian/unfolding-space

### 5.4 Multichannel Vibrotactile Glove
- **Title:** Multichannel Vibrotactile Glove: Validation of a New Device Designed to Sense Vibrations
- **Year:** 2024 | **Venue:** IEEE Transactions on Haptics
- **Key contribution:** 12 independent haptic exciters per hand (fingers + palm); precise control of location, frequency, timing, intensity
- **Hardware:** Custom glove with piezoelectric actuators
- **Relevance to ClaraVis:** **MEDIUM** -- advanced haptic design reference; complex hardware
- **URL:** https://dl.acm.org/doi/abs/10.1109/TOH.2024.3475740

### 5.5 Virtual Whiskers (Haptic Sensory Substitution)
- **Title:** Haptics-based, higher-order sensory substitution designed for object negotiation in blindness and low vision: Virtual Whiskers
- **Year:** 2025 | **Venue:** Disability and Rehabilitation: Assistive Technology
- **Key contribution:** Haptic-based electronic travel aid using "virtual whiskers" metaphor for obstacle negotiation
- **Relevance to ClaraVis:** **MEDIUM** -- novel interaction paradigm for obstacle sensing
- **URL:** https://www.tandfonline.com/doi/full/10.1080/17483107.2025.2458112

---

## 6. Traffic Light Detection

### 6.1 LYTNet
- **Title:** LYTNet: A Convolutional Neural Network for Real-Time Pedestrian Traffic Lights and Zebra Crossing Recognition for the Visually Impaired
- **Authors:** Samuel Yu, Heon Lee, John Kim | **Year:** 2019 | **Venue:** CAIP 2019 + ICCV 2019 Workshops
- **Key contribution:** Detects traffic light mode + zebra crossing position; lightweight CNN deployable on mobile (iOS + Android)
- **Open-source:** Yes -- https://github.com/samuelyu2002/ImVisible (includes PTL dataset)
- **Hardware:** Mobile phone (iOS/Android)
- **Relevance to ClaraVis:** **HIGH** -- directly integrable traffic light detection; open-source with dataset
- **URL:** https://github.com/samuelyu2002/ImVisible

### 6.2 FlashLightNet
- **Title:** FlashLightNet: An End-to-End Deep Learning Framework for Real-Time Detection and Classification of Static and Flashing Traffic Light States
- **Year:** 2025 | **Venue:** MDPI Sensors
- **Key contribution:** Detects both static and flashing traffic light states end-to-end
- **Open-source:** Not confirmed
- **Hardware:** Not specified
- **Relevance to ClaraVis:** **MEDIUM** -- handles flashing states (important for accessibility)
- **URL:** https://www.mdpi.com/1424-8220/25/20/6423

### 6.3 Lightweight Mobile Traffic Light Model (HCEDSM)
- **Title:** A mobile platform-friendly lightweight traffic light detection and recognition model
- **Year:** 2025 | **Venue:** Applied Intelligence (Springer)
- **Key contribution:** High computational cost-effectiveness + detection speed model; designed for mobile/wearable assistive devices
- **Open-source:** Not confirmed
- **Hardware:** Mobile platforms
- **Relevance to ClaraVis:** **HIGH** -- mobile-optimized traffic light detection
- **URL:** https://link.springer.com/article/10.1007/s10489-025-06993-2

---

## 7. Indoor Navigation / SLAM

### 7.1 ORB-SLAM3
- **Title:** ORB-SLAM3: An Accurate Open-Source Library for Visual, Visual-Inertial and Multi-Map SLAM
- **Authors:** Campos et al. (Univ. Zaragoza) | **Year:** 2021 | **Venue:** IEEE T-RO
- **Key contribution:** First SLAM system supporting visual, visual-inertial, and multi-map with monocular/stereo/RGB-D + fisheye; 3.6cm accuracy on EuRoC, 9mm on TUM-VI
- **Open-source:** Yes -- https://github.com/UZ-SLAMLab/ORB_SLAM3 (GPLv3)
- **Hardware:** Desktop/embedded; smartphone tested but needs IMU for robustness
- **Relevance to ClaraVis:** **HIGH** -- foundation for indoor navigation; could run on Jetson Orin NX
- **URL:** https://github.com/UZ-SLAMLab/ORB_SLAM3

### 7.2 SELM-SLAM3 (Deep Learning Enhanced)
- **Title:** Deep Learning-Powered Visual SLAM Aimed at Assisting Visually Impaired Navigation
- **Year:** 2025 | **Venue:** VISIGRAPP 2025
- **Key contribution:** Integrates SuperPoint + LightGlue into ORB-SLAM3; 87.84% improvement over vanilla ORB-SLAM3; robust in low-texture and fast-motion scenarios
- **Open-source:** Not confirmed
- **Hardware:** GPU-capable devices
- **Relevance to ClaraVis:** **HIGH** -- specifically targets visually impaired navigation; major accuracy improvement
- **URL:** https://arxiv.org/html/2510.20549

### 7.3 PhoneGuide-SLAM
- **Title:** PhoneGuide-SLAM: Low-Cost Smartphone Navigation for the Visually Impaired Using Visual Semantic SLAM
- **Year:** 2025 | **Venue:** PRCV 2025
- **Key contribution:** Smartphone-only SLAM for blind indoor navigation; semantic understanding of environment
- **Open-source:** Not confirmed
- **Hardware:** Smartphone
- **Relevance to ClaraVis:** **HIGH** -- low-cost smartphone SLAM directly relevant to ClaraVis mobile app
- **URL:** https://link.springer.com/chapter/10.1007/978-981-95-5679-3_21

### 7.4 InCrowd-VI Dataset
- **Title:** InCrowd-VI: A Realistic Visual-Inertial Dataset for Evaluating SLAM in Indoor Pedestrian-Rich Spaces
- **Year:** 2024 | **Venue:** MDPI Sensors
- **Key contribution:** 58 sequences, 5km trajectory, 1.5h recording; RGB + stereo + IMU in crowded indoor spaces
- **Open-source:** Dataset available
- **Hardware:** N/A (dataset)
- **Relevance to ClaraVis:** **MEDIUM** -- useful for testing indoor navigation in realistic conditions
- **URL:** https://pmc.ncbi.nlm.nih.gov/articles/PMC11679079/

### 7.5 xfeatSLAM (Lightweight Deep Features)
- **Title:** xfeatSLAM: Real-time SLAM with deep features
- **Year:** 2024 | **Venue:** GitHub
- **Key contribution:** Integrates lightweight XFeat deep features into ORB-SLAM3; suited for mobile robots and embedded systems
- **Open-source:** Yes -- https://github.com/udaysankar01/xfeatSLAM
- **Hardware:** Embedded systems, mobile robots
- **Relevance to ClaraVis:** **MEDIUM** -- lightweight SLAM alternative for Jetson
- **URL:** https://github.com/udaysankar01/xfeatSLAM

---

## 8. OCR and Document Reading

### 8.1 PaddleOCR v3.0 / PP-OCRv5
- **Title:** PaddleOCR 3.0 Technical Report
- **Developer:** PaddlePaddle (Baidu) | **Year:** 2025 | **Venue:** arXiv
- **Key contribution:** Ultra-lightweight OCR (~9.6MB English mobile model); 100+ languages; mobile/embedded/IoT deployment; text detection + recognition + layout analysis
- **Open-source:** Yes -- https://github.com/PaddlePaddle/PaddleOCR (72K+ stars)
- **Hardware:** Mobile, embedded, IoT, server
- **Relevance to ClaraVis:** **HIGH** -- lightweight on-device OCR for reading signs, labels, documents; multi-language including Portuguese
- **URL:** https://github.com/PaddlePaddle/PaddleOCR

### 8.2 Google ML Kit Text Recognition v2
- **Title:** Text Recognition v2 (ML Kit)
- **Developer:** Google | **Year:** 2024 (ongoing)
- **Key contribution:** On-device OCR for Android (API 21+); Latin + CJK + Devanagari scripts; bundled or unbundled models; provides bounding boxes, confidence scores, language detection
- **Open-source:** Free to use (not open-source); dependency: `com.google.mlkit:text-recognition:16.0.1`
- **Hardware:** Android devices
- **Relevance to ClaraVis:** **HIGH** -- easiest integration path for ClaraVis Android app; already compatible
- **URL:** https://developers.google.com/ml-kit/vision/text-recognition/v2/android

### 8.3 Android OCR Scanner (ML Kit + Tesseract + Cloud Vision)
- **Title:** Android-OCR-Text-Recognition-Scanner
- **Developer:** Community | **Year:** 2024
- **Key contribution:** Open-source Android app combining ML Kit, Tesseract, and Google Cloud Vision for OCR
- **Open-source:** Yes -- https://github.com/itsVnp/Android-OCR-Text-Recognition-Scanner
- **Hardware:** Android
- **Relevance to ClaraVis:** **HIGH** -- reference implementation for Android OCR integration
- **URL:** https://github.com/itsVnp/Android-OCR-Text-Recognition-Scanner

---

## 9. Face Recognition On-Device

### 9.1 InsightFace / ArcFace
- **Title:** InsightFace: Open Source 2D & 3D Face Analysis Library
- **Developer:** DeepInsight | **Year:** 2019 (ongoing)
- **Key contribution:** ArcFace loss (CVPR 2019) for discriminative face embeddings; InspireFace SDK fits in 64MB RAM on RK3566; supports ARM, x86, CUDA, OpenCL, RKNN
- **Open-source:** Yes -- https://github.com/deepinsight/insightface (24K+ stars)
- **Hardware:** Mobile (64MB RAM), edge (RK3566, Jetson), server
- **Relevance to ClaraVis:** **HIGH** -- lightweight face recognition for identifying known people; runs on constrained hardware; InspireFace C/C++ SDK released 2024
- **URL:** https://github.com/deepinsight/insightface

### 9.2 LittleFaceNet (Lightweight Face Recognition)
- **Title:** LittleFaceNet: A Small-Sized Face Recognition Method Based on RetinaFace and AdaFace
- **Year:** 2025 | **Venue:** MDPI J. Imaging
- **Key contribution:** Combined RetinaFace detection + AdaFace quality-adaptive recognition + ByteTrack tracking; 96.12% accuracy on WiderFace; handles low-resolution faces
- **Open-source:** Not confirmed
- **Hardware:** Not specified (designed for small-scale deployment)
- **Relevance to ClaraVis:** **MEDIUM** -- addresses low-resolution face recognition (relevant for phone cameras at distance)
- **URL:** https://pmc.ncbi.nlm.nih.gov/articles/PMC11766931/

### 9.3 EdgeFace (Edge-Optimized Face Recognition)
- **Title:** EdgeFace: Efficient Face Recognition Model for Edge Devices
- **Year:** 2023 (updated 2024) | **Venue:** arXiv
- **Key contribution:** Face recognition model specifically optimized for edge device deployment
- **Open-source:** Not confirmed
- **Hardware:** Edge devices
- **Relevance to ClaraVis:** **MEDIUM** -- designed for edge deployment, relevant to Jetson
- **URL:** https://arxiv.org/html/2307.01838v2

### 9.4 Privacy: Synthetic Face Datasets
- **Title:** Beyond Real Faces: Synthetic Datasets Can Achieve Reliable Recognition Performance without Privacy Compromise
- **Year:** 2025 | **Venue:** arXiv
- **Key contribution:** Synthetic datasets now match real-data performance; addresses GDPR/privacy concerns for face recognition training
- **Relevance to ClaraVis:** **MEDIUM** -- important for privacy-compliant face recognition feature
- **URL:** https://arxiv.org/html/2510.17372v1

---

## 10. Obstacle Detection and Avoidance

### 10.1 DSC-Net: Blind Road Segmentation
- **Title:** DSC-Net: Enhancing Blind Road Semantic Segmentation with Visual Sensor Using a Dual-Branch Swin-CNN Architecture
- **Year:** 2024 | **Venue:** MDPI Sensors
- **Key contribution:** Parallel CNN + Swin-Transformer for sidewalk/road segmentation; CNN branch extracts edges/textures, Transformer branch handles spatial layout
- **Open-source:** Not confirmed
- **Hardware:** GPU (inference speed not specified for edge)
- **Relevance to ClaraVis:** **HIGH** -- sidewalk vs. road segmentation critical for outdoor navigation
- **URL:** https://pmc.ncbi.nlm.nih.gov/articles/PMC11435784/

### 10.2 QPULM: Footpath Detection on Smartphones
- **Title:** Towards walkable footpath detection for the visually impaired on Bangladeshi roads with smartphones using deep edge intelligence
- **Year:** 2025 | **Venue:** ScienceDirect
- **Key contribution:** Quantized + Pruned UNet-based Lightweight MobileNet (QPULM) for semantic segmentation of walkable vs. non-walkable areas; SODD technique for obstacle distance in left/right/forward directions; Android app
- **Open-source:** Not confirmed
- **Hardware:** Android smartphone
- **Relevance to ClaraVis:** **HIGH** -- walkable path detection on smartphone; directly applicable approach
- **URL:** https://www.sciencedirect.com/science/article/pii/S2590005625000153

### 10.3 Terrain Detection Review
- **Title:** Terrain detection and segmentation for autonomous vehicle navigation: A state-of-the-art systematic review
- **Year:** 2024 | **Venue:** ScienceDirect (Information Fusion)
- **Key contribution:** Comprehensive review of LiDAR, computer vision, and deep learning for road/terrain segmentation
- **Relevance to ClaraVis:** **MEDIUM** -- reference for terrain-aware navigation algorithms
- **URL:** https://www.sciencedirect.com/science/article/pii/S1566253524004226

---

## 11. Wearable Computing for Accessibility

### 11.1 Focus AI Glasses
- **Title:** Focus AI Glasses: AI-Enhanced Smart Glasses for Individuals with Visual & Auditory Impairments
- **Developer:** aghassel (Engineering Capstone) | **Year:** 2024
- **Key contribution:** Speech translation, sign language interpretation, navigation assistance with obstacle detection; plans for Jetson upgrade
- **Open-source:** Yes -- https://github.com/aghassel/Focus-AI-Glasses
- **Hardware:** Current: unspecified; planned: NVIDIA Jetson
- **Relevance to ClaraVis:** **HIGH** -- similar goals; open-source reference architecture
- **URL:** https://github.com/aghassel/Focus-AI-Glasses

### 11.2 OpenGlass ($20 AI Smart Glasses)
- **Title:** OpenGlass: Turn Any Glasses into AI Smart Glasses
- **Developer:** Based Hardware | **Year:** 2024
- **Key contribution:** $20 AI smart glasses using XIAO ESP32S3 Sense; life recording, people recognition, object identification, text translation
- **Open-source:** Yes -- https://github.com/BasedHardware/OpenGlass
- **Hardware:** XIAO ESP32S3 Sense microcontroller + 3D-printed mount
- **Relevance to ClaraVis:** **HIGH** -- ultra-low-cost smart glasses reference; complementary to ClaraVis Jetson approach
- **URL:** https://github.com/BasedHardware/OpenGlass

### 11.3 Open Source Smart Glasses (Mentra/TeamOpenSmartGlasses)
- **Title:** Open Source Smart Glasses
- **Developer:** TeamOpenSmartGlasses / Mentra Community | **Year:** 2024
- **Key contribution:** Designed for all-day wearability, immediate utility, extensibility; for makers and startups
- **Open-source:** Yes -- https://github.com/TeamOpenSmartGlasses/OpenSourceSmartGlasses
- **Hardware:** Custom PCB + 3D-printed frames + off-the-shelf batteries
- **Relevance to ClaraVis:** **HIGH** -- hardware design reference for ClaraVis smart glasses
- **URL:** https://github.com/TeamOpenSmartGlasses/OpenSourceSmartGlasses

### 11.4 Brilliant Labs Frame AI Glasses
- **Title:** Frame AI Glasses (Open-Source AR Glasses)
- **Developer:** Brilliant Labs | **Year:** 2024
- **Key contribution:** Open-source AR glasses with AI; language translation, image detection, web search; open-source firmware and apps
- **Open-source:** Yes -- https://github.com/brilliantlabsAR
- **Hardware:** Custom AR glasses with display
- **Relevance to ClaraVis:** **HIGH** -- production-quality open-source AR glasses with AI
- **URL:** https://www.brilliantlabsAR.com

### 11.5 BlindSpot VisionGuide (Raspberry Pi)
- **Title:** AI-powered BlindSpot VisionGuide system on Raspberry Pi for enhancing independence of visually impaired users
- **Authors:** Sudha, M. et al. | **Year:** 2026 | **Venue:** Nature Scientific Reports
- **Key contribution:** Integrated AI system on Raspberry Pi: face recognition (deep embeddings), image captioning (BLIP), text-to-speech, voice interface, newspaper reader
- **Open-source:** Not confirmed
- **Hardware:** Raspberry Pi
- **Relevance to ClaraVis:** **HIGH** -- multi-modal assistive system on edge device; published in high-impact journal
- **URL:** https://www.nature.com/articles/s41598-026-39724-9

### 11.6 VIsION (Affordable Wearable AI)
- **Title:** VIsION: Affordable Wearable AI for Visually Impaired
- **Developer:** MXGray | **Year:** 2024
- **Key contribution:** Tested on RPi Zero WH, 3B+, and 4B; aims for affordable daily-use wearable
- **Open-source:** Yes (GPLv3) -- https://github.com/MXGray/VIsION
- **Hardware:** Raspberry Pi Zero WH to 4B
- **Relevance to ClaraVis:** **MEDIUM** -- affordable but limited compute vs. Jetson
- **URL:** https://github.com/MXGray/VIsION

---

## 12. Cross-Modal Sensory Substitution

### 12.1 Auditory and Tactile Frequency Mapping for Visual Distance
- **Title:** Auditory and tactile frequency mapping for visual distance perception: A step forward in sensory substitution and augmentation
- **Year:** 2025 | **Venue:** PMC
- **Key contribution:** Establishes psychophysical mapping functions between auditory/vibrotactile frequency and visual distance through cross-modal matching experiments
- **Relevance to ClaraVis:** **HIGH** -- provides empirically validated mapping for converting distance to audio/haptic feedback
- **URL:** https://pmc.ncbi.nlm.nih.gov/articles/PMC11875370/

### 12.2 Learning Visual to Auditory Sensory Substitution
- **Title:** Learning visual to auditory sensory substitution reveals flexibility in image to sound mapping
- **Year:** 2025 | **Venue:** Nature npj Science of Learning
- **Key contribution:** Studies effectiveness of different image-to-sound mappings (traditional vs. reversed vs. arbitrary); The vOICe system (vertical position -> pitch, horizontal -> time)
- **Relevance to ClaraVis:** **MEDIUM** -- informs design of vision-to-audio conversion
- **URL:** https://www.nature.com/articles/s41539-025-00385-4

### 12.3 The vOICe (Vision-to-Audio SSD)
- **Title:** The vOICe sensory substitution system
- **Developer:** Peter Meijer | **Year:** 1992 (ongoing)
- **Key contribution:** Classic vision-to-audio sensory substitution; converts camera images to soundscapes (x-axis -> time sweep, y-axis -> frequency, brightness -> loudness)
- **Open-source:** Software available; algorithm well-documented
- **Hardware:** Any camera + headphones
- **Relevance to ClaraVis:** **MEDIUM** -- foundational approach; ClaraVis could implement simplified version

---

## 13. Brazilian University Research

### 13.1 NavWear (UNESP + UFES)
- **Title:** NavWear: design and evaluation of a wearable device for obstacle detection for blind and visually impaired people
- **Authors:** Researchers from UNESP (Sao Paulo State University) + UFES (Federal University of Espirito Santo)
- **Year:** 2025 | **Venue:** Disability and Rehabilitation: Assistive Technology
- **Key contribution:** Backpack-integrated system with RGB-D camera + Jetson Nano + haptic feedback (vibration motors in straps: left/right/both); tested with blind users; fewer collisions, less frustration, higher safety perception vs. cane alone
- **Funding:** FAPESP Grant 2019/14438-4
- **Open-source:** Not confirmed
- **Hardware:** NVIDIA Jetson Nano + Intel RealSense RGB-D camera
- **Relevance to ClaraVis:** **HIGH** -- Brazilian research using Jetson for blind navigation; FAPESP-funded; potential collaboration partner
- **URL:** https://www.tandfonline.com/doi/full/10.1080/17483107.2025.2477681

### 13.2 UFRB RFID Tactile Floor Navigation
- **Title:** RFID-based navigation on tactile floors (Professor Joao Neto, UFRB)
- **Institution:** Federal University of Reconcavo da Bahia (UFRB)
- **Year:** 2024-2025
- **Key contribution:** RFID tags on tactile floors + shoe-mounted reader; stores location, danger alerts, environmental info
- **Funding:** FAPESB (Bahia Research Foundation)
- **Open-source:** Not confirmed
- **Hardware:** RFID tags + shoe reader
- **Relevance to ClaraVis:** **MEDIUM** -- infrastructure-dependent (needs RFID-tagged floors); interesting for indoor venues
- **URL:** https://www.fapesb.ba.gov.br/professor-baiano-desenvolve-nova-tecnologia-assistiva-para-deficientes-visuais/

### 13.3 UNINTER Inclusive Mobility Technology
- **Title:** Desenvolvimento de tecnologia inclusiva para mobilidade do deficiente visual
- **Institution:** UNINTER
- **Year:** 2026 | **Venue:** Caderno Progressus
- **Key contribution:** Laser sensor-based obstacle detection with sound/vibration/voice alerts
- **Relevance to ClaraVis:** **LOW** -- simpler sensor approach, no deep learning
- **URL:** https://www.cadernosuninter.com/index.php/progressus/article/view/3244

### 13.4 UFRGS Assistive Technology Thesaurus
- **Title:** Tesauros -- Tecnologias Assistivas para pessoas com deficiencia visual
- **Institution:** UFRGS (Federal University of Rio Grande do Sul)
- **Key contribution:** Organized thesaurus/taxonomy of assistive technologies for visual impairment
- **Relevance to ClaraVis:** **LOW** -- taxonomy reference, not technical implementation
- **URL:** https://www.ufrgs.br/tesauros/index.php/thesa/select/808/f8776b0a437df84dfe1be95070e0e426

### 13.5 UFBA Assistive Technology Patent Analysis
- **Title:** Tecnologia Assistiva para Pessoas com Deficiencia Visual: uma analise da producao tecnologica no Brasil
- **Institution:** UFBA (Federal University of Bahia)
- **Year:** 2024 | **Venue:** Cadernos de Prospeccao
- **Key contribution:** Analysis of Brazilian assistive technology patents and technological production landscape
- **Relevance to ClaraVis:** **MEDIUM** -- useful for understanding Brazilian IP landscape in assistive tech
- **URL:** https://periodicos.ufba.br/index.php/nit/article/view/25903/0

---

## Summary: Top Priorities for ClaraVis Integration

### Immediate (Android App - Helio G85)
| Priority | Technology | Source | Why |
|----------|-----------|--------|-----|
| 1 | YOLO11n / YOLOv10n | Ultralytics | Drop-in upgrade from current YOLOv8; TFLite export |
| 2 | Google ML Kit OCR v2 | Google | Easiest text reading integration for Android |
| 3 | LYTNet traffic light detection | ImVisible GitHub | Open-source, mobile-ready, includes dataset |

### Short-term (Jetson Orin NX 16GB)
| Priority | Technology | Source | Why |
|----------|-----------|--------|-----|
| 1 | Depth Anything V2 Small | GitHub | Monocular depth for distance estimation; ONNX/TensorRT |
| 2 | RT-DETR / RT-DETRv4 | GitHub | NMS-free detection; faster pipeline on GPU |
| 3 | SmolVLM2-500M | HuggingFace | Smallest VLM for scene description; fits 16GB with detection model |
| 4 | InsightFace (InspireFace SDK) | GitHub | Face recognition in 64MB RAM; identify known people |
| 5 | ORB-SLAM3 / SELM-SLAM3 | GitHub | Indoor navigation and mapping |
| 6 | 3D Tune-In Toolkit / Steam Audio | GitHub | Spatial audio for directional obstacle alerts |

### Medium-term (Smart Glasses / Full System)
| Priority | Technology | Source | Why |
|----------|-----------|--------|-----|
| 1 | OpenGlass / Open Source Smart Glasses | GitHub | Hardware reference for ClaraVis glasses |
| 2 | WOAD cross-modal obstacle avoidance | GitHub (Nature Comms) | State-of-the-art wearable; open-source |
| 3 | Unfolding Space Glove | GitHub | Haptic depth feedback; complementary to AR display |
| 4 | WalkVLM dataset | walkvlm2024.github.io | Training data for walking guidance VLM |
| 5 | DSC-Net / QPULM sidewalk segmentation | Papers | Walkable path detection |

### Research Collaboration Opportunities (Brazil)
| Institution | Project | Contact Point |
|-------------|---------|---------------|
| UNESP + UFES | NavWear (Jetson + haptic backpack) | FAPESP Grant 2019/14438-4 |
| UFRB | RFID tactile floor navigation | Prof. Joao Neto |
| UFBA | Assistive tech patent landscape | Cadernos de Prospeccao |

---

## Key GitHub Repositories (Quick Reference)

| Repository | Stars | Description |
|-----------|-------|-------------|
| [ultralytics/ultralytics](https://github.com/ultralytics/ultralytics) | 40K+ | YOLO11/v10/v9/v8 |
| [DepthAnything/Depth-Anything-V2](https://github.com/DepthAnything/Depth-Anything-V2) | 5K+ | Monocular depth estimation |
| [PaddlePaddle/PaddleOCR](https://github.com/PaddlePaddle/PaddleOCR) | 72K+ | Lightweight OCR |
| [deepinsight/insightface](https://github.com/deepinsight/insightface) | 24K+ | Face recognition |
| [UZ-SLAMLab/ORB_SLAM3](https://github.com/UZ-SLAMLab/ORB_SLAM3) | 7K+ | Visual SLAM |
| [lyuwenyu/RT-DETR](https://github.com/lyuwenyu/RT-DETR) | 3K+ | Real-time transformer detector |
| [3DTune-In/3dti_AudioToolkit](https://github.com/3DTune-In/3dti_AudioToolkit) | 500+ | Binaural spatial audio |
| [ValveSoftware/steam-audio](https://github.com/ValveSoftware/steam-audio) | 2K+ | 3D spatial audio engine |
| [jakobkilian/unfolding-space](https://github.com/jakobkilian/unfolding-space) | 200+ | Haptic depth glove |
| [MMCNJUPT/WOAD](https://github.com/MMCNJUPT/WOAD) | New | Wearable obstacle avoidance (Nature Comms) |
| [samuelyu2002/ImVisible](https://github.com/samuelyu2002/ImVisible) | 100+ | Traffic light + zebra crossing detection |
| [BasedHardware/OpenGlass](https://github.com/BasedHardware/OpenGlass) | 3K+ | $20 AI smart glasses |
| [TeamOpenSmartGlasses/OpenSourceSmartGlasses](https://github.com/TeamOpenSmartGlasses/OpenSourceSmartGlasses) | 1K+ | Open smart glasses platform |
| [aghassel/Focus-AI-Glasses](https://github.com/aghassel/Focus-AI-Glasses) | 100+ | AI glasses for visual/auditory impairment |
| [back2-thebasic/NavYolo](https://github.com/back2-thebasic/NavYolo) | New | Lightweight YOLO for indoor navigation |

---

*Document compiled 2026-03-11. All URLs verified at time of research.*
