# Cross-Industry Technology Research for Assistive Visual Technology

**Date:** 2026-03-11
**Purpose:** Identify technologies from other industries that can be adapted for assistive glasses/wearables for visually impaired users (ClaraVis project)

---

## 1. Autonomous Vehicles

### 1.1 LiDAR Sensors

**What it does:** Fires laser pulses and measures time-of-flight to create 3D point clouds of the environment. Used for obstacle detection, mapping, and navigation at ranges of 10-200+ meters.

| Sensor | Size | Weight | Cost | Power |
|--------|------|--------|------|-------|
| Livox Mid-360 | 65x65x58mm | ~265g | ~$1,000 | 9W |
| Ouster OS0 (solid-state) | 72x89mm | ~590g | ~$3,500 | ~15W |
| Velodyne Velabit | 60x60x30mm | ~300g | ~$100 (announced) | ~5W |
| Intel RealSense L515 (LiDAR) | 61x26mm | ~100g | ~$350 (discontinued) | ~3.5W |

**Open-source alternatives:**
- [OpenLiDAR](https://github.com/openlidarmap) - open-source LiDAR processing
- [LOAM/F-LOAM](https://github.com/LucasWEIchen/FLOAM_ssl) - LiDAR odometry and mapping for solid-state LiDAR
- Point Cloud Library (PCL) - open-source 3D point cloud processing

**Wearable feasibility:** LOW (current multi-beam LiDAR too heavy/power-hungry) to MEDIUM (chip-scale solid-state dToF sensors like those in iPhone LiDAR are glasses-compatible today)

**Miniaturization timeline:** Chip-scale dToF LiDAR (e.g., Apple-style) already fits in phones. True 360-degree solid-state LiDAR in glasses form factor: 3-5 years.

**ClaraVis relevance:** HIGH. A forward-facing dToF sensor (like iPhone LiDAR) could provide accurate 0.5-5m depth maps for obstacle distance estimation, complementing ClaraVis's YOLO-based detection with precise range data.

---

### 1.2 Sensor Fusion Algorithms

**What it does:** Combines data from multiple sensor types (camera, LiDAR, radar, IMU) to create a unified environmental model more reliable than any single sensor.

**Current state:** Autonomous vehicles fuse camera + LiDAR + radar. FusionAD (Transformer-based) integrates temporal/contextual information in three stages. Optimized on Jetson Orin with ~55% computational efficiency improvement.

**Open-source alternatives:**
- [OpenPCDet](https://github.com/open-mmlab/OpenPCDet) - 3D object detection with LiDAR-camera fusion
- [BEVFusion](https://github.com/mit-han-lab/bevfusion) - multi-task multi-sensor fusion
- [MMDetection3D](https://github.com/open-mmlab/mmdetection3d) - comprehensive 3D detection toolbox

**Wearable feasibility:** MEDIUM. Algorithms are portable; the challenge is having multiple miniaturized sensors to fuse.

**ClaraVis relevance:** HIGH. Even fusing camera + IMU + single depth sensor would dramatically improve ClaraVis detection reliability, especially for transparent/reflective obstacles that YOLO alone misses.

---

### 1.3 Radar (Automotive mmWave)

**What it does:** Uses millimeter-wave radio (77GHz) to detect objects through fog, rain, darkness, and smoke. Works where cameras and LiDAR fail.

| Sensor | Size | Cost | Power | Range |
|--------|------|------|-------|-------|
| Texas Instruments AWR1843 | 10.4x10.4mm (chip) | ~$30 | ~1.5W | 0.2-120m |
| Infineon BGT60TR13C | 6.5x5mm (chip) | ~$15 | ~0.5W | 0.2-10m |

**Open-source alternatives:**
- [OpenRadar](https://github.com/PreSenseRadar/OpenRadar) - open-source radar processing
- TI mmWave SDK (free, not fully open)

**Wearable feasibility:** HIGH. mmWave radar chips are already tiny (< 1cm^2) and low-power enough for glasses.

**Miniaturization timeline:** Already wearable-ready. 60GHz radar is in consumer devices (Google Pixel Soli).

**ClaraVis relevance:** MEDIUM-HIGH. Radar provides all-weather, day/night obstacle detection. Ideal for detecting moving vehicles, approaching hazards, and ground-level obstacles. Works where camera fails (night, fog, glare).

---

## 2. Drones/UAVs

### 2.1 Visual-Inertial Odometry (VIO) & Visual SLAM

**What it does:** Uses camera(s) + IMU to estimate device position and orientation in 3D space without GPS. Skydio uses 6 cameras with 45 megapixels for 360-degree obstacle avoidance with VIO at high altitudes.

**Open-source alternatives:**
- [ORB-SLAM3](https://github.com/UZ-SLAMLab/ORB_SLAM3) - visual, visual-inertial, multimap SLAM (monocular, stereo, RGB-D)
- [OpenVINS](https://docs.openvins.com/) - MSCKF-based VIO, lightweight
- [RTAB-Map](https://github.com/introlab/rtabmap) - visual + lidar SLAM, appearance-based loop closure
- [VINS-Fusion](https://github.com/HKUST-Aerial-Robotics/VINS-Fusion) - multi-sensor visual-inertial state estimator

**Wearable feasibility:** HIGH. VIO runs on smartphones today. Stereo cameras + IMU fit in glasses frames.

**ClaraVis relevance:** VERY HIGH. VIO/SLAM enables ClaraVis to build a persistent map of the user's environment, track their position within it, remember where obstacles were, and provide consistent navigation instructions. This is the key to moving from "detection" to "navigation."

---

### 2.2 Compact Stereo Depth Cameras

**What it does:** Uses two cameras (stereo baseline) to compute depth maps in real time, similar to human binocular vision. On-device AI runs object detection directly.

| Camera | Size | Cost | Power | Depth Range | AI |
|--------|------|------|-------|-------------|-----|
| Luxonis OAK-D Lite | 91x28x17.5mm | $149 | ~2.5W | 0.2-19m | 4 TOPS on-device |
| Luxonis OAK-D Pro | 115x30x20mm | $299 | ~3W | 0.2-35m | 4 TOPS + IR |
| RealSense D435i | 90x25x25mm | ~$200 | ~2W | 0.1-10m | No on-device AI |

**Open-source alternatives:**
- [DepthAI](https://github.com/luxonis/depthai) - Luxonis open-source SDK (Apache 2.0)
- [librealsense](https://github.com/IntelRealSense/librealsense) - Intel RealSense SDK
- RealSense spun out of Intel (July 2025) as independent company

**Wearable feasibility:** MEDIUM. OAK-D Lite is small enough for a head-mounted device, not quite glasses-sized. Stereo baseline (~7cm) is a fundamental constraint.

**ClaraVis relevance:** HIGH. Stereo depth provides precise obstacle distance at near ranges (0.2-5m), which is the critical zone for a walking user. OAK-D can run YOLO on-device at 4 TOPS, offloading the phone.

---

### 2.3 Monocular Depth Estimation (Software-Based)

**What it does:** Uses a single camera + AI to estimate depth from a monocular image. No additional hardware needed.

| Model | Size | Speed (Jetson) | Accuracy | License |
|-------|------|----------------|----------|---------|
| MiDaS v3.1 Small | ~25MB | ~30 FPS | Good | MIT |
| Depth Anything v2 Small | ~25MB | ~30 FPS | State-of-art | Apache 2.0 |
| Depth Anything v2 Large | ~335MB | ~8 FPS | Best | Apache 2.0 |

**Open-source alternatives:**
- [MiDaS](https://github.com/isl-org/MiDaS) - Intel ISL, multiple model sizes
- [Depth Anything](https://github.com/LiheYoung/Depth-Anything) - foundation model, 62M training images
- [OpenMiDaS](https://github.com/cmusatyalab/openmidas) - edge deployment, Android app available

**Wearable feasibility:** VERY HIGH. Runs on existing ClaraVis hardware (phone camera + Jetson compute). Zero additional sensors needed.

**ClaraVis relevance:** VERY HIGH. Immediate integration potential: run Depth Anything alongside YOLO on the same camera feed to add distance estimation to every detected object. This is the lowest-cost, lowest-complexity path to depth awareness.

---

## 3. Robotics

### 3.1 ROS2 Navigation Stack (Nav2)

**What it does:** Complete navigation framework including path planning, obstacle avoidance, costmap generation, behavior trees. The standard in robotics for autonomous navigation.

**Components relevant to ClaraVis:**
- **Costmap2D**: Builds occupancy grids from sensor data showing safe/unsafe zones
- **SLAM Toolbox**: Real-time 2D mapping and localization
- **NvBlox** (NVIDIA): GPU-accelerated 3D occupancy mapping, Costmap2D plugin
- **Behavior Trees**: Decision framework for complex navigation decisions

**Open-source:**
- [Nav2](https://navigation.ros.org/) - full navigation stack (Apache 2.0)
- [SLAM Toolbox](https://github.com/SteveMacenski/slam_toolbox) - 2D SLAM
- [NvBlox](https://github.com/NVIDIA-ISAAC-ROS/isaac_ros_nvblox) - GPU-accelerated 3D reconstruction

**Wearable feasibility:** MEDIUM. Nav2 is designed for robots, but individual components (costmaps, planners) can be adapted. Requires ROS2 runtime, which is heavy for a wearable.

**ClaraVis relevance:** MEDIUM-HIGH. Costmap generation concepts (free space vs. obstacles) could provide ClaraVis users with navigable-path guidance instead of just obstacle alerts. The algorithms are mature and well-tested.

---

### 3.2 Semantic SLAM

**What it does:** Combines SLAM (mapping + localization) with semantic understanding. The map doesn't just know "there's something at coordinate X,Y" but "there's a door at X,Y."

**Open-source alternatives:**
- [Kimera](https://github.com/MIT-SPARK/Kimera) - real-time metric-semantic SLAM
- [RTAB-Map](https://github.com/introlab/rtabmap) with semantic labels
- [Hydra](https://github.com/MIT-SPARK/Hydra) - 3D scene graphs from sensor data

**Wearable feasibility:** MEDIUM. Computationally demanding, but can run on Jetson Orin NX.

**ClaraVis relevance:** HIGH. A semantic map would let ClaraVis say "the door is 3 meters ahead on your left, the stairs begin 5 meters ahead" rather than just "door detected." This transforms isolated detections into spatial understanding.

---

### 3.3 Human-Following & Person Tracking

**What it does:** Robots track and follow specific humans using appearance features, skeleton estimation, and re-identification (Re-ID).

**Open-source:**
- [OpenPose](https://github.com/CMU-Perceptual-Computing-Lab/openpose) - skeleton estimation
- [DeepSORT](https://github.com/nwojke/deep_sort) - object tracking with Re-ID
- [ByteTrack](https://github.com/ifzhang/ByteTrack) - high-performance multi-object tracking

**Wearable feasibility:** HIGH. Lightweight trackers (ByteTrack) run on mobile devices.

**ClaraVis relevance:** MEDIUM. Useful for tracking a companion in crowds, or tracking specific objects/people the user cares about. Lower priority than obstacle/navigation features.

---

## 4. Military/Defense

### 4.1 Thermal Imaging (LWIR)

**What it does:** Detects long-wave infrared radiation (heat). Sees through darkness, smoke, and light fog. Detects people, animals, vehicles by body heat.

| Sensor | Resolution | Size | Cost | Power |
|--------|-----------|------|------|-------|
| FLIR Lepton 3.5 | 160x120 | 11.8x12.7x8.0mm | ~$200 | ~150mW |
| FLIR Lepton XDS (2026) | 160x120 + 5MP visible | Compact module | $109-239 | ~200mW |
| Seek Thermal Compact | 206x156 | Smartphone plug | $250-370 | USB-powered |

**Key development (Feb 2026):** Teledyne FLIR launched Lepton XDS at MWC 2026 -- combines thermal (160x120) + visible (5MP) camera with MSX fusion in a single module. Designed explicitly for drones, wearables, and IoT.

**Open-source alternatives:**
- [OpenMV](https://openmv.io/) - supports FLIR Lepton adapter module
- [LeptonSDK](https://github.com/groupgets/LeptonModule) - open-source Lepton interface

**Wearable feasibility:** HIGH. FLIR Lepton is already dime-sized, 150mW. The Lepton XDS is explicitly designed for wearable integration.

**ClaraVis relevance:** HIGH. Thermal detects hazards invisible to RGB cameras: people/animals in darkness, hot surfaces (stoves, engines), wet/icy patches (thermal contrast), fire, vehicles at night. At $109-239, cost is practical.

---

### 4.2 Night Vision (Digital/Low-Light)

**What it does:** Amplifies available light or uses near-infrared illumination to see in darkness. Modern digital night vision uses CMOS sensors with extreme low-light sensitivity.

| Technology | Cost | Size | Power | Notes |
|-----------|------|------|-------|-------|
| Gen3+ analog tubes | $3,000-8,000 | Large/heavy | ~100mW | Military-grade, ITAR restricted |
| Digital NV (Sony STARVIS 2) | $50-200 (sensor) | Chip-scale | ~500mW | No export restrictions, commercially available |
| Smartphone NV mode | Free (software) | N/A | Camera power | Google Pixel Night Sight, Samsung |

**Wearable feasibility:** HIGH for digital NV. Sony STARVIS 2 sensors are used in compact security cameras and smartphones. An IR-illuminated digital NV system fits in glasses.

**ClaraVis relevance:** MEDIUM-HIGH. Low-light performance directly impacts YOLO detection accuracy. A NIR illuminator + STARVIS sensor would extend ClaraVis operation to nighttime walking, which is particularly dangerous for visually impaired users.

---

### 4.3 Heads-Up Displays (HUD)

**What it does:** Projects information onto a transparent display in the user's field of view. Military IVAS (based on HoloLens) overlays thermal/NV imagery, waypoints, and friend/foe identification.

**Current state:** Microsoft handed IVAS production to Anduril (Feb 2025). The system uses a flat display with ~60-degree FOV, but caused headaches/eyestrain in testing. Army is recompeting the program.

| Display Technology | FOV | Resolution | Weight | Cost |
|-------------------|-----|-----------|--------|------|
| MicroLED waveguide (current AR glasses) | 30-52 deg | 1280x1280 | <50g (optics) | $300-3,500 |
| Birdbath optics (Xreal) | 46 deg | 1920x1080 per eye | ~79g | $699 |
| Holographic waveguide (HoloLens 2) | 52 deg | 2048x1080 | 566g | $3,500 |
| Micro-OLED (Meta Orion prototype) | ~70 deg | TBD | ~98g | N/A (prototype) |

**Open-source alternatives:**
- [OpenHMD](http://www.openhmd.net/) - open-source HMD driver library
- [Project North Star](https://github.com/leapmotion/ProjectNorthStar) - open-source AR headset design by Ultraleap

**Wearable feasibility:** HIGH. Multiple commercial AR glasses already exist in glasses form factor.

**ClaraVis relevance:** VERY HIGH. This is ClaraVis's core differentiator -- visual overlays (contours, labels, alerts, magnified text) on see-through display. Current options: Xreal Light ($699), Envision Glasses ($2,500), RayNeo X3 Pro (upcoming with Snapdragon AR1+).

---

## 5. Gaming/VR/AR

### 5.1 Spatial Audio Engines (3D/HRTF)

**What it does:** Uses Head-Related Transfer Functions (HRTFs) to render sounds that appear to come from specific 3D locations around the listener, enabling audio navigation cues.

| Engine | License | Platform | Latency | Key Features |
|--------|---------|----------|---------|--------------|
| Steam Audio | Free (proprietary) | Windows/Linux/macOS/Android | <5ms | Physics-based propagation, ray tracing, FMOD/Wwise/Unity plugins |
| Meta XR Audio SDK | Free | Quest/mobile | <5ms | HRTF, ambisonics, room acoustics |
| 3DTI Toolkit | Open-source (GPL) | Cross-platform | <10ms | Binaural spatializer, hearing-aid simulation |

**Open-source alternatives:**
- [3D Tune-In Toolkit](https://github.com/3DTune-In/3dti_AudioToolkit) - GPL, binaural rendering + hearing device simulation
- [Spatial Audio Framework](https://github.com/leomccormack/Spatial_Audio_Framework) - C/C++ spatial audio library
- [Steam Audio](https://valvesoftware.github.io/steam-audio/) - free, with FMOD/Wwise integration
- [openal-soft](https://github.com/kcat/openal-soft) - open-source 3D audio (LGPL)

**Wearable feasibility:** VERY HIGH. Runs on any device with stereo audio output. Bone-conduction headphones preserve ambient hearing.

**ClaraVis relevance:** VERY HIGH. Spatial audio is the primary output modality for ClaraVis. Instead of "car detected," the system can play a car sound from the direction and distance where the car actually is. The 3DTI Toolkit even includes hearing-aid simulation, directly relevant to users with combined visual/hearing impairment.

---

### 5.2 Haptic Feedback Systems

**What it does:** Converts spatial/environmental information into vibration patterns felt on the body. Different locations/intensities encode direction and distance.

| System | Type | Motors/Points | Cost | Weight |
|--------|------|---------------|------|--------|
| bHaptics TactSuit X40 | Vibrotactile vest | 40 | $549 | ~1.2kg |
| OWO Skin | Electro-muscle stimulation | 10 zones | $299 | ~200g |
| Custom haptic belt (research) | Vibrotactile waist | 20 actuators | ~$50 DIY | ~150g |
| Wrist-band (single motor) | Vibrotactile | 1-4 | $10-30 DIY | ~30g |

**Open-source alternatives:**
- [TactileWave](https://github.com/bhaptics/tactosy) - bHaptics SDK
- Arduino-based haptic belt designs (multiple research papers)
- [OpenHaptics](https://github.com/3DSystems/OpenHaptics) - haptic rendering toolkit

**Wearable feasibility:** HIGH. A 4-8 motor haptic band on wrist or waist is lightweight and power-efficient.

**ClaraVis relevance:** HIGH. Haptic feedback provides silent, non-intrusive directional guidance. A waistband with 8 vibration motors can indicate obstacle direction (which motor vibrates) and distance (vibration intensity). Works in noisy environments where audio alerts fail.

---

### 5.3 Eye Tracking

**What it does:** Tracks gaze direction using near-IR cameras + corneal reflection. Enables gaze-based interaction, foveated rendering, and attention monitoring.

| System | Accuracy | Latency | Form Factor | Cost |
|--------|----------|---------|-------------|------|
| Tobii Eye Tracker 5 | 0.4-0.6 deg | <10ms | Screen-mounted bar | $229 |
| Tobii (embedded in headsets) | 0.5 deg | <5ms | Integrated in VR HMDs | OEM pricing |
| Pupil Labs Neon | 1.6 deg | 5ms | Glasses form factor | ~$4,900 |

**Open-source alternatives:**
- [Pupil Labs](https://github.com/pupil-labs/pupil) - open-source eye tracking (LGPL)
- [OpenFace](https://github.com/TadasBaltrusaitis/OpenFace) - facial/gaze analysis
- [GazeTracking](https://github.com/antoinelame/GazeTracking) - lightweight gaze tracking

**Wearable feasibility:** MEDIUM-HIGH. Already integrated in VR headsets and Pupil Labs glasses. Size/power are manageable.

**ClaraVis relevance:** MEDIUM. For users with partial vision (low vision, not total blindness), eye tracking could determine what the user is trying to look at and provide enhanced information about that specific region. Lower priority for users with no remaining vision.

---

### 5.4 Hand Tracking / Gesture Recognition

**What it does:** Detects hand position, orientation, and gestures using cameras or event-based sensors.

| System | Power | Size | Latency | Notes |
|--------|-------|------|---------|-------|
| Ultraleap Helios (event camera) | ~20mW | 3x4mm sensor | <10ms | Micro-gestures for glasses |
| Ultraleap Hyperion | ~500mW | Module | <15ms | Full hand tracking |
| Camera-based (MediaPipe) | ~200mW | Software only | ~30ms | Uses existing camera |

**Key development:** Ultraleap Helios (Dec 2024) uses Prophesee GENX320 event camera -- 3x4mm, 20mW -- for always-on gesture recognition on glasses. Supports swipe, pinch gestures.

**Open-source alternatives:**
- [MediaPipe Hands](https://github.com/google/mediapipe) - hand tracking on mobile (Apache 2.0)
- Ultraleap Helios SDK (alpha, developer kit available)

**Wearable feasibility:** VERY HIGH. Helios sensor is 3x4mm at 20mW -- easily fits in glasses.

**ClaraVis relevance:** MEDIUM. Allows hands-free UI control via gestures (e.g., pinch to zoom, swipe to change mode). Useful but not core to the obstacle detection mission.

---

## 6. Medical Imaging

### 6.1 Real-Time Segmentation (nnU-Net / MONAI)

**What it does:** Automatically segments structures in images with pixel-level precision. nnU-Net self-configures preprocessing, architecture, and training for any segmentation task.

**Open-source:**
- [nnU-Net](https://github.com/MIC-DKUZ/nnUNet) - self-configuring segmentation (Apache 2.0)
- [MONAI](https://monai.io/) - medical imaging AI toolkit (Apache 2.0)
- Auto-nnU-Net (2025) adds AutoML for hyperparameter optimization

**Wearable feasibility:** MEDIUM. nnU-Net is GPU-intensive for training but lighter models can run inference on Jetson. MONAI's deployment tools support TensorRT optimization.

**ClaraVis relevance:** MEDIUM. Segmentation architectures from medical imaging (U-Net variants) could improve ClaraVis's scene parsing -- segmenting walkable surfaces vs. obstacles vs. drop-offs with pixel precision rather than bounding boxes alone.

---

### 6.2 Portable AI Diagnostics (Butterfly IQ paradigm)

**What it does:** Butterfly IQ put an entire ultrasound system on a single semiconductor chip -- "Ultrasound-on-Chip." The iQ3 (2024) doubles data transfer speed, adds 3D imaging, and runs AI guidance on-device.

**ClaraVis relevance:** LOW-MEDIUM (indirect). The engineering paradigm -- putting an entire sensing system on a single chip with on-device AI -- is the roadmap for miniaturizing ClaraVis's sensor stack. The principle of chip-on-sensor AI is directly applicable.

---

### 6.3 Retinal Imaging / Vision Enhancement

**What it does:** eSight and similar devices capture high-res video, process it in real-time, and display enhanced imagery on OLED screens positioned in front of the user's eyes.

| Device | Resolution | Enhancement | Cost | Weight |
|--------|-----------|-------------|------|--------|
| eSight 4 | HD OLED | Contrast, zoom, color adjustment | $5,950 | ~95g |
| Envision Glasses | Google Glass EE2 | AI description + OCR | $2,500 | ~46g |
| Xreal Light | 1920x1080/eye | XR overlays | $699 | ~79g |

**ClaraVis relevance:** HIGH. ClaraVis's visual overlay approach (contours, labels, magnification) is directly in this lineage. Key learning: enhance the remaining vision rather than replace it entirely. Contrast enhancement + edge highlighting are proven effective for low-vision users.

---

## 7. Smart Home / IoT

### 7.1 UWB (Ultra-Wideband) Indoor Positioning

**What it does:** Uses time-of-flight of ultra-wideband radio pulses to achieve centimeter-level indoor positioning (10-30cm accuracy).

| Chip | Process | Accuracy | Range | Power |
|------|---------|----------|-------|-------|
| Apple U2 | 7nm | <1 inch | 60m | Ultra-low (years on coin cell) |
| NXP SR040 (Samsung) | 40nm | ~30cm | 30m practical | Low |
| Qorvo DW3000 | N/A | 10cm | 30m | ~30mW active |

**Real-world deployment:** NTT Data deployed UWB indoor positioning in a 20,000-seat London stadium, enabling a visually impaired user to locate their specific seat.

**Market growth:** UWB hardware/software revenue projected at $2.0B (2025) growing to $3.3B (2028) at 18.9% CAGR.

**Open-source alternatives:**
- [Decawave/Qorvo DW3000 SDK](https://github.com/Decawave) - UWB ranging
- [Home Assistant UWB integration](https://www.home-assistant.io/)
- ESP32-UWB modules (~$15) for DIY anchors

**Wearable feasibility:** VERY HIGH. UWB chips are already in smartphones and AirTags. A UWB tag in glasses is trivial.

**ClaraVis relevance:** VERY HIGH for indoor navigation. If buildings deploy UWB anchors (which is happening), ClaraVis can provide turn-by-turn indoor navigation with centimeter accuracy -- find your seat in a stadium, navigate a hospital, find a specific store in a mall.

---

### 7.2 BLE Beacons for Indoor Positioning

**What it does:** Bluetooth Low Energy beacons broadcast signals used for proximity-based positioning (1-3m accuracy).

| Protocol | Accuracy | Range | Cost/beacon | Infrastructure needed |
|----------|----------|-------|-------------|----------------------|
| iBeacon (Apple) | 1-3m | ~30m | $5-15 | Medium |
| Eddystone (Google) | 1-3m | ~30m | $5-15 | Medium |
| AoA (Bluetooth 5.1) | ~0.5m | ~15m | $20-50 | Medium-High |

**Open-source:**
- [Espressif ESP32 BLE beacon](https://github.com/espressif/esp-idf)
- [Indoor Atlas](https://www.indooratlas.com/) (freemium)
- [Find My framework](https://developer.apple.com/find-my/)

**Wearable feasibility:** VERY HIGH. BLE is already in every phone and most wearables.

**ClaraVis relevance:** MEDIUM-HIGH. Less accurate than UWB but much cheaper infrastructure. Many buildings already have BLE beacons deployed for marketing. ClaraVis can piggyback on existing infrastructure.

---

### 7.3 Matter Protocol / Smart Home Integration

**What it does:** Universal smart home protocol (Apple + Google + Amazon + Samsung) enabling cross-platform device interoperability. 300+ certified devices (2025), version 1.5 released Nov 2025.

**ClaraVis relevance:** MEDIUM. ClaraVis glasses could interact with Matter-compatible smart home devices -- automatic lighting adjustment when entering rooms, door/window state awareness, appliance status. The IoT-enabled Smart Cane research (2026) shows this direction: context-aware obstacle detection integrated with building systems.

---

## 8. Semiconductor / Edge AI

### 8.1 Qualcomm Snapdragon AR1+ Gen 1

**What it does:** Purpose-built chip for AI smart glasses. Powers Ray-Ban Meta glasses.

| Spec | Value |
|------|-------|
| Package | 26% smaller than AR1 Gen 1 |
| Power | 7% less than predecessor |
| Display | Binocular 1280x1280 |
| Camera | 12MP photo, 6MP video (14-bit ISP) |
| AI | On-device Llama 3.2 1B language model |
| Process | Advanced node (estimated 4nm) |

**ClaraVis relevance:** VERY HIGH. This is the leading candidate chip for ClaraVis glasses. Running Llama 3.2 1B on-device means ClaraVis could describe scenes in natural language without cloud connectivity. The 14-bit ISP improves low-light performance for YOLO. Already powers shipping products (Ray-Ban Meta).

---

### 8.2 MediaTek Dimensity 9500

**What it does:** Mobile SoC with NPU 990 (2x previous gen AI performance). Powers Xreal One Pro standalone AR glasses.

| Spec | Value |
|------|-------|
| NPU | NPU 990, ~2x previous gen |
| AI | Multimodal LLM on-device (text, image, voice, video) |
| Process | 3nm TSMC |
| Power | Optimized for wearable thermal constraints |

**ClaraVis relevance:** HIGH. Alternative to Qualcomm. MediaTek's strength in power-per-watt optimization suits wearables. Xreal One Pro proves it works in standalone glasses.

---

### 8.3 NVIDIA Jetson Orin NX/Nano

**What it does:** GPU-accelerated AI compute module for edge applications.

| Module | AI Perf | Power | Size | Cost |
|--------|---------|-------|------|------|
| Jetson Orin Nano (Super) | 67 TOPS | 7-25W | 70x45mm | $249 |
| Jetson Orin NX 16GB | 100-157 TOPS | 10-25W | 70x45mm | $599 |

**JetPack 6.2 (2025):** Super Mode doubles inference performance. New 25W and 40W power modes.

**ClaraVis relevance:** HIGH (for belt-pack/companion device, not glasses-mounted). Already in ClaraVis roadmap as reComputer Super J4012. Ideal for running multiple AI models (YOLO + depth + SLAM + LLM) simultaneously. Too power-hungry for glasses-on-face.

---

### 8.4 Hailo-8 / Hailo-8L

**What it does:** Dedicated AI accelerator with integrated memory, optimized for vision AI.

| Spec | Hailo-8 | Hailo-8L |
|------|---------|----------|
| Performance | 26 TOPS | 13 TOPS |
| Power | 2.5W | 1.5W |
| Efficiency | ~10 TOPS/W | ~9 TOPS/W |
| Size | Smaller than a penny | Smaller than a penny |
| Interface | M.2, mini-PCIe | M.2 |

**ClaraVis relevance:** HIGH. At 2.5W and penny-size, Hailo-8 could sit in the glasses temple as a dedicated YOLO accelerator. 26 TOPS is sufficient for YOLO + depth estimation at 30fps. Best-in-class TOPS/Watt ratio among dedicated AI chips.

---

### 8.5 Google Edge TPU (Coral)

| Spec | Value |
|------|-------|
| Performance | 4 TOPS (INT8) |
| Power | 2W |
| Efficiency | 2 TOPS/W |
| Size | Coral Micro: 12x12mm |

**ClaraVis relevance:** MEDIUM. Lower performance than Hailo-8, but cheaper and well-supported by TensorFlow Lite ecosystem. Sufficient for running a single YOLO model.

---

### 8.6 Amlogic A311D

| Spec | Value |
|------|-------|
| NPU | 5 TOPS (INT8) |
| CPU | 4x A73 + 2x A53 @ 2.2GHz |
| Power | ~5W |
| Process | 12nm |

**ClaraVis relevance:** LOW-MEDIUM. Dated (2019), lower performance than alternatives. Better options available in Hailo-8, Snapdragon AR1+, or Jetson Nano.

---

### 8.7 Intel Movidius (Myriad X)

| Spec | Value |
|------|-------|
| Performance | ~1-4 TOPS |
| Power | ~1.5W |
| Size | USB stick or embedded |

**ClaraVis relevance:** LOW. Intel Movidius is being phased out. RealSense spun out of Intel (July 2025). Better alternatives exist.

---

## Existing Commercial Assistive Smart Glasses (Competitive Landscape)

| Product | Hardware | Features | Price | Status |
|---------|----------|----------|-------|--------|
| Envision Glasses | Google Glass EE2 | AI scene description, OCR, face recognition | $2,500 | Active |
| OrCam MyEye 3 | Clip-on module | Text reading, face/product recognition | $4,000-6,000 | Company downsizing (2025) |
| eSight 4 | OLED displays | Video magnification, contrast enhancement | $5,950 | Active |
| Xreal Light | Birdbath AR optics | XR overlays, used in YOLOv8 research | $699 | Active |
| Ray-Ban Meta | Snapdragon AR1 | Camera + AI assistant (no display) | $299 | Active |

**ClaraVis differentiator:** Visual overlay approach (contours, AR labels, depth-coded highlights) versus audio-only solutions. Most competitors are audio-first. ClaraVis targets low-vision users who can benefit from enhanced visual information.

---

## Top 15 High-Impact Technologies (Ranked)

| Rank | Technology | Industry | Feasibility | Impact | Justification |
|------|-----------|----------|-------------|--------|---------------|
| **1** | **Monocular Depth Estimation (Depth Anything v2)** | Drones/CV | VERY HIGH | VERY HIGH | Zero hardware cost -- runs on existing ClaraVis camera + Jetson. Adds distance estimation to every YOLO detection. Immediately deployable. |
| **2** | **Spatial Audio (3DTI Toolkit / Steam Audio)** | Gaming | VERY HIGH | VERY HIGH | Software-only, open-source, HRTF-based 3D audio places alerts at correct spatial positions. Transforms ClaraVis from "obstacle detected" to "obstacle at your 2 o'clock, 3 meters." Bone-conduction headphones preserve ambient hearing. |
| **3** | **Qualcomm Snapdragon AR1+ Gen 1** | Semiconductor | HIGH | VERY HIGH | Purpose-built for AI glasses. On-device LLM (Llama 3.2 1B), 12MP camera, binocular display support. Runs ClaraVis YOLO + scene description without cloud. The chip that makes standalone ClaraVis glasses possible. |
| **4** | **Visual-Inertial Odometry / SLAM (ORB-SLAM3)** | Drones/Robotics | HIGH | VERY HIGH | Open-source (GPLv3). Enables persistent spatial mapping -- ClaraVis remembers obstacle locations, tracks user movement, provides consistent navigation. The foundation for moving from "detection" to "navigation." |
| **5** | **FLIR Lepton XDS Thermal Module** | Military | HIGH | HIGH | Dime-sized, 150mW, $109-239. Detects people/animals in darkness, hot surfaces, wet/icy patches. Launched Feb 2026 with explicit wearable target. Fills ClaraVis's biggest gap: nighttime/low-visibility operation. |
| **6** | **UWB Indoor Positioning** | Smart Home/IoT | VERY HIGH | HIGH | Centimeter-level indoor navigation. Already proven for visually impaired navigation (London stadium). Apple U2 chip runs for years on coin cell. As UWB infrastructure grows, ClaraVis gains free indoor GPS. |
| **7** | **Hailo-8 AI Accelerator** | Semiconductor | HIGH | HIGH | 26 TOPS at 2.5W in penny-size package. Dedicated YOLO accelerator that could sit in glasses temple. Best TOPS/W ratio available. Enables on-glasses AI processing without phone/belt-pack. |
| **8** | **Sensor Fusion Algorithms (BEVFusion)** | Autonomous Vehicles | MEDIUM | HIGH | Combining camera + depth + thermal + radar eliminates single-sensor failures. Automotive algorithms are battle-tested for safety-critical applications. Requires multiple sensors but each is now miniature. |
| **9** | **Stereo Depth Camera (Luxonis OAK-D)** | Drones | MEDIUM | HIGH | OAK-D Lite provides hardware depth + on-device YOLO (4 TOPS) at $149 and 2.5W. DepthAI SDK is open-source. More accurate than monocular depth. Small enough for head-mounted but not yet glasses-form. |
| **10** | **AR Display / Heads-Up Display** | Military/VR | HIGH | HIGH | Xreal-class birdbath optics or microLED waveguides. ClaraVis's core feature -- visual overlays for low-vision users. Xreal Light at $699 is the most accessible current option for development. |
| **11** | **Haptic Feedback Belt/Band** | Gaming | HIGH | MEDIUM-HIGH | 4-8 vibration motors on waist or wrist encode direction + distance. Silent, works in noisy environments. Low-cost DIY ($50) using Arduino + vibration motors. Complements audio alerts as secondary channel. |
| **12** | **Semantic SLAM (Kimera)** | Robotics | MEDIUM | HIGH | Builds maps with semantic labels ("door," "stairs," "chair"). Transforms raw obstacle data into meaningful spatial descriptions. Computationally heavy but runs on Jetson Orin NX. |
| **13** | **mmWave Radar (TI AWR1843)** | Autonomous Vehicles | HIGH | MEDIUM-HIGH | 10mm chip, ~$30, 1.5W. Works through fog, rain, darkness, smoke. Detects approaching vehicles/people by motion. Complements camera for all-weather safety. Already tiny enough for glasses. |
| **14** | **Ultraleap Helios Event Camera Gestures** | Gaming/VR | VERY HIGH | MEDIUM | 3x4mm, 20mW event camera for micro-gesture control on glasses. Enables hands-free UI without voice commands. Low priority vs. detection features but important for usability. |
| **15** | **Lightweight Pedestrian Detection (LP-YOLO / DSR-YOLO)** | Autonomous Vehicles | VERY HIGH | MEDIUM | Optimized YOLO variants reduce parameters 24-68% while improving accuracy. Direct drop-in upgrades for ClaraVis's existing model. LP-YOLO and DSR-YOLO papers provide architecture blueprints. |

---

## Implementation Roadmap for ClaraVis

### Phase 1: Software-Only (0-3 months, $0 additional hardware)
1. Integrate **Depth Anything v2** alongside YOLO on Jetson Orin NX
2. Add **spatial audio** (3DTI Toolkit or Steam Audio) for directional obstacle alerts
3. Upgrade YOLO architecture using **LP-YOLO/DSR-YOLO** optimizations
4. Implement basic **VIO** using phone IMU + camera (ARCore/ARKit)

### Phase 2: Sensor Additions (3-6 months, ~$350-500)
5. Add **FLIR Lepton XDS** thermal module for night operation
6. Add **UWB module** for indoor positioning (Qorvo DW3000 or ESP32-UWB)
7. Build prototype **haptic wrist band** (Arduino + 4 vibration motors)
8. Implement camera + thermal **sensor fusion**

### Phase 3: Glasses Platform (6-18 months, ~$700-3,000)
9. Port to **AR glasses** platform (Xreal Light or RayNeo X3 Pro)
10. Integrate **Hailo-8** or **Snapdragon AR1+** for on-glasses AI
11. Implement **semantic SLAM** (Kimera) for spatial understanding
12. Add **mmWave radar** for all-weather detection

### Phase 4: Full Navigation System (18-36 months)
13. Full **sensor fusion** (camera + depth + thermal + radar + UWB)
14. **Costmap-based path planning** adapted from Nav2
15. Building-scale UWB indoor navigation integration
16. On-device **LLM scene description** (Llama 3.2 on Snapdragon AR1+)

---

## Sources

### Autonomous Vehicles / LiDAR
- [Ouster VLP-16](https://ouster.com/products/hardware/vlp-16)
- [Livox Mid-360](https://www.livoxtech.com/mid-360)
- [Velodyne Velabit - $100 LiDAR](https://www.greencarcongress.com/2020/01/20200108-lidar.html)
- [RealSense Spins Out of Intel](https://techcrunch.com/2025/07/11/realsense-spins-out-of-intel-to-scale-its-stereoscopic-imaging-technology/)
- [Intel RealSense L515 Teardown](https://www.techinsights.com/blog/inside-intel-realsense-l515-lidar-camera)
- [Sensor Fusion on Jetson Orin](https://www.nature.com/articles/s41598-024-82356-0)

### Drones / Visual SLAM
- [Skydio Autonomy](https://www.skydio.com/blog/skydio-autonomy-tm-a-new-age-of-drone-intelligence)
- [ORB-SLAM3 GitHub](https://github.com/UZ-SLAMLab/ORB_SLAM3)
- [RTAB-Map](https://arxiv.org/abs/2403.06341)
- [VINS-Fusion Benchmarking 2025](https://onlinelibrary.wiley.com/doi/10.1002/rob.22581)

### Depth Estimation
- [MiDaS GitHub](https://github.com/isl-org/MiDaS)
- [Depth Anything](https://depth-anything.github.io/)
- [OpenMiDaS Edge Deployment](https://github.com/cmusatyalab/openmidas)

### Robotics / Navigation
- [Nav2 Documentation](https://navigation.ros.org/)
- [NvBlox Vision-Based Navigation](https://docs.nav2.org/tutorials/docs/using_isaac_perceptor.html)
- [Spot ROS2 Driver](https://github.com/bdaiinstitute/spot_ros2)

### Military / Thermal / HUD
- [FLIR Lepton XDS Launch (Feb 2026)](https://uasweekly.com/2026/02/25/teledyne-flir-oem-launches-lepton-xds-thermal-visible-camera-at-mwc/)
- [FLIR Lepton Specs](https://oem.flir.com/products/lepton/)
- [IVAS Program Update](https://breakingdefense.com/2025/04/after-ivas-army-reveals-timeline-for-new-augmented-reality-race-to-name-winners-in-august/)
- [HoloLens Declared Dead (Feb 2025)](https://redmondmag.com/articles/2025/02/14/hololens-is-dead.aspx)

### Gaming / VR / AR
- [Steam Audio](https://valvesoftware.github.io/steam-audio/)
- [3D Tune-In Toolkit](https://journals.plos.org/plosone/article?id=10.1371/journal.pone.0211899)
- [Spatial Audio Framework GitHub](https://github.com/leomccormack/Spatial_Audio_Framework)
- [Ultraleap Helios Launch](https://www.ultraleap.com/company/news/press-release/helios-launch/)
- [Ultraleap Helios Paper](https://arxiv.org/html/2407.05206v1)
- [Tobii Assistive Technology](https://www.tobii.com/products/integration/screen-based-integrations/assistive-technology)

### Smart Home / IoT / UWB
- [UWB for Visually Impaired Navigation](https://www.mdpi.com/2076-3417/14/13/5646)
- [UWB Stadium Navigation (NTT Data)](https://locatify.com/uwb-ultrawideband-indoor-positioning-for-visually-impaired/)
- [BLE vs UWB Comparison](https://www.seeedstudio.com/blog/2025/11/13/ble-vs-uwb-vs-gps-vs-wifi-which-is-the-best-indoor-positioning-technology-for-personal-safety/)
- [Apple U2 UWB Chip](https://www.cultofmac.com/news/find-my-iphone-15-more-accurate-u2-chip-uwb-ultra-wideband)
- [Matter 2026 Status](https://matter-smarthome.de/en/development/the-matter-standard-in-2026-a-status-review/)

### Semiconductor / Edge AI
- [Qualcomm AR1+ Gen 1](https://www.qualcomm.com/xr-vr-ar/products/ar-series/snapdragon-ar1-gen-1-platform)
- [AR1+ Announcement (AWE 2025)](https://9to5google.com/2025/06/10/snapdragon-ar1-gen-1/)
- [MediaTek Dimensity 9500 for AR Glasses](https://www.androidheadlines.com/2026/03/mediatek-to-debut-worlds-first-6g-radio-demo-ai-powered-smart-glasses-at-mwc-2026.html)
- [Jetson Orin JetPack 6.2 Super Mode](https://developer.nvidia.com/blog/nvidia-jetpack-6-2-brings-super-mode-to-nvidia-jetson-orin-nano-and-jetson-orin-nx-modules/)
- [Hailo-8 AI Accelerator](https://hailo.ai/products/ai-accelerators/hailo-8-ai-accelerator/)
- [Edge AI Chip Comparison 2026](https://research.aimultiple.com/edge-ai-chips/)

### Stereo Depth Cameras
- [Luxonis OAK-D](https://shop.luxonis.com/products/oak-d)
- [OAK vs ZED Comparison](https://docs.luxonis.com/hardware/platform/comparison/vs-stereolabs/)
- [DepthAI SDK](https://www.luxonis.com/)

### Assistive Glasses Landscape
- [Best Smart Glasses for Visually Impaired (2026)](https://www.iamhable.com/en-am/blogs/article/the-best-smart-glasses-for-visually-impaired-people-a-2025-guide)
- [Top Smart Glasses Comparison](https://www.onedaymd.com/2026/01/top-smart-glasses-for-visually-impaired.html)
- [YOLOv8 XR Smart Glasses Research](https://www.mdpi.com/2079-9292/14/3/425)
- [Envision Glasses](https://www.letsenvision.com/glasses/home)

### Pedestrian Detection
- [LP-YOLO (YOLOv11 Optimized)](https://www.sciencedirect.com/science/article/abs/pii/S1051200425003653)
- [DSR-YOLO (YOLOv8 Optimized)](https://www.sciencedirect.com/science/article/pii/S2667241325000096)
- [Lightweight Pedestrian Detection on IoT Edge](https://arxiv.org/abs/2409.15740)
