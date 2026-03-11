# Comprehensive Technical Feature Analysis: Assistive Technology for the Visually Impaired

**Date:** 2026-03-11
**Research compiled for:** ClaraVis Project competitive intelligence

---

## Table of Contents

1. [OrCam MyEye 3 Pro (& Read 3)](#1-orcam-myeye-3-pro--read-3)
2. [Envision Glasses](#2-envision-glasses)
3. [eSight Go (& eSight 4)](#3-esight-go--esight-4)
4. [Be My Eyes (+ Be My AI)](#4-be-my-eyes--be-my-ai)
5. [Seeing AI (Microsoft)](#5-seeing-ai-microsoft)
6. [Aira](#6-aira)
7. [dotLumen Glasses](#7-dotlumen-glasses)
8. [WeWalk Smart Cane 2](#8-wewalk-smart-cane-2)
9. [Glidance Glide](#9-glidance-glide)
10. [Google Lookout](#10-google-lookout)
11. [Lazarillo GPS](#11-lazarillo-gps)
12. [IrisVision](#12-irisvision)
13. [NuEyes Pro 3e (& e3+, Pro 4 LV)](#13-nueyes-pro-3e--e3-pro-4-lv)
14. [Sullivan+](#14-sullivan-korea)
15. [RealSAM Pocket](#15-realsam-pocket)
16. [Ray-Ban Meta Smart Glasses](#16-ray-ban-meta-smart-glasses)
17. [Apple Vision Pro](#17-apple-vision-pro)
18. [Hable One](#18-hable-one)
19. [BrainPort Vision Pro](#19-brainport-vision-pro)
20. [SoundSight](#20-soundsight)
21. [Summary Comparison Matrix](#summary-comparison-matrix)

---

## 1. OrCam MyEye 3 Pro (& Read 3)

**Manufacturer:** OrCam Technologies (Israel)
**Form Factor:** Clip-on module for any eyeglass frame

### Hardware Specs
| Spec | Detail |
|------|--------|
| Camera | 13 MP |
| Processor | Proprietary on-device AI chip (undisclosed) |
| Weight | 22.5 g (0.79 oz) |
| Dimensions | 76 x 21 x 14.9 mm |
| Battery | 3.7 VDC Li-ion; 60-90 min active use |
| Charging | ~3 hours wall charger; optional MyCharger provides 4 full charges (up to 10 hrs) |
| Display | None (audio-only output) |
| Sensors | Camera only (no depth/LiDAR) |

### AI/ML Stack
- **On-device:** All core features run offline -- OCR, face recognition, product/barcode ID, color detection, currency recognition
- **Cloud (WiFi required):** Smart Magnifier (streams to phone/tablet), "Just Ask" AI assistant
- **Tasks:** Real-time text reading, face recognition (stores database on device), product ID via barcode, color identification, currency identification, handwriting recognition
- **Languages:** 20+ for TTS; 140+ for Smart Magnifier translation

### Audio Output
- Built-in miniature speaker resting above the ear
- Bluetooth audio output to hearing aids (Starkey Livio Edge AI, Phonak Audeo Marvel) and standard BT earbuds/speakers

### Visual Output
- Smart Magnifier feature streams magnified view to connected phone/tablet screen with zoom, contrast, and font adjustment
- No AR overlay on glasses

### Haptic Feedback
- None reported

### Navigation
- None (not a navigation device)

### Connectivity
- Bluetooth (for audio devices and hearing aids)
- WiFi (for software updates and cloud AI features only)

### Open APIs/SDKs
- No public API or SDK

### Notable Patents/Proprietary Tech
- Gesture and touch-bar control interface on the device body
- On-device AI processing without cloud dependency for core features
- Magnetic attachment system for any glasses frame

### Pricing
| Model | Price |
|-------|-------|
| MyEye 3 Pro | ~$4,250 |
| Read 3 | ~$1,890 (UK: GBP 1,890 excl. VAT) |

**OrCam Read 3** is a handheld variant (same dimensions/weight) with 4.5-5 hr battery, includes a stationary reader stand, focuses on text reading + Smart Magnifier. No face recognition or wearable mode.

**Sources:**
- [OrCam MyEye 3 Pro Official](https://www.orcam.com/en-us/orcam-myeye-3-pro)
- [OrCam Read 3 Official](https://www.orcam.com/en-us/orcam-read-3)
- [Amazon - OrCam MyEye 3 Pro](https://www.amazon.com/OrCam-Advanced-Vision-Impairment-Wearable/dp/B0CVHHPFCT)
- [New England Low Vision](https://nelowvision.com/product/orcam-myeye-3-pro-wearable-ai-reading-aid/)

---

## 2. Envision Glasses

**Manufacturer:** Envision (Netherlands)
**Form Factor:** Smart glasses (Google Glass Enterprise Edition 2 hardware)

### Hardware Specs
| Spec | Detail |
|------|--------|
| Processor | Qualcomm Quad Core, 1.7 GHz, 10 nm |
| RAM / Storage | 3 GB LPDDR4 / 32 GB eMMC Flash |
| Camera | 8 MP, 80-degree diagonal FOV |
| Display | 640 x 360 optical display module (prism, right eye) |
| Battery | 820 mAh with fast charge; 5-6 hrs regular use |
| Weight | ~46 g (device body) |
| Connectivity | 802.11ac dual-band WiFi, Bluetooth 5.x AoA, USB-C (USB 2.0) |
| Audio | Mono speaker, 3 beam-forming microphones, USB & BT audio |
| OS | Android Oreo |
| Durability | Water and dust resistant |

### AI/ML Stack
- **On-device + Cloud hybrid:** Uses GPT-5 (OpenAI) for "Ask Envision" document Q&A feature
- **On-device tasks:** Instant Text (real-time OCR), light detection, color detection, cash recognition
- **Cloud tasks:** Describe Scene (AI scene description), Find People/Objects, Batch Scan, Ask Envision
- **Face Recognition:** "Teach a Face" stores faces locally for on-device recognition

### Audio Output
- Mono speaker on device frame
- Bluetooth audio support
- USB-C audio output

### Visual Output
- Small prism display in right eye showing text/UI
- No AR overlay or magnification on the display

### Haptic Feedback
- None reported

### Navigation
- Hands-free navigation feature (announced)
- No built-in GPS or depth sensors; relies on phone GPS

### Connectivity
- WiFi (802.11ac), Bluetooth 5.x, USB-C
- Companion app on iOS/Android

### Open APIs/SDKs
- No public SDK reported
- Integration with Aira service (Call Aira feature)
- Integration with Be My Eyes companion calling

### Notable Patents/Proprietary Tech
- Envision's proprietary AI software layer on Google Glass EE2 hardware
- "Ask Envision" GPT-5 document Q&A pipeline

### Pricing
| Edition | Price | Features |
|---------|-------|----------|
| Home | ~$1,899 | Instant Text, Scan Text, Call a Companion, Describe Scene, Detect Light/Colors |
| Read | ~$2,499 | + Batch Scan, Recognise Cash, Find People/Objects |
| Professional | ~$3,499 | + Teach a Face, Explore, Ask Envision, lifetime updates |

**Note on Monocle platform:** Google's upcoming 2026 AI glasses include a "Monocular XR Glasses" variant with in-lens display for AR overlays (navigation, translation). Separate from Envision; Envision may transition to new hardware as Google Glass EE2 is discontinued.

**Sources:**
- [Envision Glasses Professional - Store](https://shop.letsenvision.com/products/glasses-professional)
- [Envision Glasses Home](https://www.letsenvision.com/glasses/home)
- [9to5Google - Envision on Glass](https://9to5google.com/2020/03/09/envision-google-glass/)
- [MacRumors - Google AI Glasses 2026](https://www.macrumors.com/2025/12/08/google-ai-smart-glasses-2026/)

---

## 3. eSight Go (& eSight 4)

**Manufacturer:** eSight Corp (Canada)
**Form Factor:** Head-mounted electronic glasses (low-vision enhancement)

### eSight Go Hardware Specs
| Spec | Detail |
|------|--------|
| Display | Dual full HD OLED near-to-eye screens (68.75% clarity improvement over eSight 4) |
| Camera | High-resolution with image stabilization and smart autofocus |
| FOV | 45 degrees (wider than eSight 4) |
| Magnification | Up to 24x zoom |
| Weight | ~170 g |
| Battery | Up to 3 hrs continuous use; ergonomic neck battery pack |
| Controls | Tactile control buttons on frame |
| Connectivity | Wireless; OTA software updates; USB-C charging |

### eSight 4 Hardware Specs
| Spec | Detail |
|------|--------|
| Display | Dual OLED screens |
| Camera | HD autofocus camera |
| Weight | 1.1 lbs (~500 g) |
| Battery | Up to 3 hrs continuous use |
| Price | $6,950 (or $249/month rental) |

### AI/ML Stack
- **On-device:** Real-time video processing, autofocus algorithms, image stabilization
- **No cloud AI features** reported (not scene description, not OCR-based)
- Primary function is video magnification and enhancement, not AI recognition

### Audio Output
- Integrated speakers on eSight Go

### Visual Output
- **Primary function:** Real-time magnified video feed on OLED displays
- Up to 24x digital magnification
- Color and contrast filters
- Image stabilization for reading and distance viewing
- Designed for low-vision users (acuity as low as 20/400)

### Haptic Feedback
- None reported

### Navigation
- None (not a navigation device; mobility-focused FOV design)

### Connectivity
- Wireless (specifics undisclosed)
- USB-C for charging
- OTA firmware updates

### Open APIs/SDKs
- No public API or SDK

### Notable Patents/Proprietary Tech
- Proprietary "Bioptic Tilt" technology for switching between enhanced and natural vision
- Dual-screen binocular OLED display system
- Neck-mounted battery design for weight reduction

### Pricing
| Model | Price |
|-------|-------|
| eSight Go | $4,950 USD / $6,600 CAD |
| eSight 4 | $6,950 USD (or $249/mo rental) |

**Sources:**
- [eSight Go Official](https://www.esighteyewear.com/esight-go/)
- [eSight Go Pricing](https://www.esighteyewear.com/esight-go-pricing/)
- [eSight 4 Tech Specs PDF](https://esighteyewear.com/wp-content/uploads/2021/08/eSight-4-Technical-Specifications.pdf)
- [Digital Trends - eSight Go CES 2023](https://www.digitaltrends.com/phones/esight-go-smart-glasses-gives-people-their-vision-back-ces-2023/)

---

## 4. Be My Eyes (+ Be My AI)

**Manufacturer:** Be My Eyes (Denmark)
**Form Factor:** Mobile app (iOS, Android, Windows)

### Hardware Specs
- **No proprietary hardware** -- runs on user's existing smartphone
- Also available on **Ray-Ban Meta Smart Glasses** (voice-activated "Call a Volunteer")
- Requires device camera and internet connection

### AI/ML Stack
- **Be My AI:** Powered by OpenAI GPT-4o (multimodal vision model)
- **Architecture:** User captures image via phone camera -> image sent to OpenAI API -> AI generates detailed visual description -> returned as text/audio
- **Tasks:** Scene description, text reading, object identification, product recognition, document analysis, image Q&A
- **Languages:** AI descriptions in 36 languages; volunteer calls in 185+ languages
- **Cloud-only:** Requires internet connection for both AI and volunteer features

### Audio Output
- Uses device's built-in speaker/earphones
- TTS via device accessibility features (VoiceOver/TalkBack)

### Visual Output
- None (audio-first design)
- Text descriptions displayed on screen (for low-vision users)

### Haptic Feedback
- Standard phone vibration only

### Navigation
- Not a navigation tool (scene description can help orientation)

### Connectivity
- Requires internet (WiFi or cellular)
- Available on iOS, Android, Windows
- Ray-Ban Meta integration via voice command

### Open APIs/SDKs
- **Be My Eyes Partner API** for businesses to provide accessible customer support
- "Specialized Help" directory for company integrations (Microsoft, Google, Procter & Gamble, etc.)

### Notable Patents/Proprietary Tech
- First accessibility app to integrate GPT-4V for visual assistance
- Crowdsourced volunteer network (7M+ sighted volunteers globally)
- Partnership with Meta for smart glasses integration

### Pricing
- **Completely free** for all users (blind/low-vision and volunteers)
- Funded by partnerships with companies in the Specialized Help directory

**Sources:**
- [Be My Eyes Official](https://www.bemyeyes.com/bme-app/)
- [Be My Eyes - Introducing Be My AI](https://www.bemyeyes.com/blog/introducing-be-my-ai/)
- [Be My Eyes on Ray-Ban Meta](https://www.bemyeyes.com/be-my-eyes-smartglasses/)
- [Be My Eyes Help Center - Pricing](https://support.bemyeyes.com/hc/en-us/articles/360006070777)

---

## 5. Seeing AI (Microsoft)

**Manufacturer:** Microsoft (USA)
**Form Factor:** Mobile app (iOS, Android)

### Hardware Specs
- **No proprietary hardware** -- runs on user's smartphone
- Uses device camera, speaker, and processor

### AI/ML Stack
- **Cloud + on-device hybrid:** Leverages Azure AI Vision, Azure Cognitive Services
- **Models:** Computer vision (object detection, scene recognition), OCR, facial recognition, barcode scanning, generative AI for rich descriptions
- **Channels/Modes:**
  1. **Short Text:** Real-time OCR, speaks text as it appears in camera view
  2. **Documents:** Guided page capture with formatting-aware OCR
  3. **Products:** Barcode scanning with audio guidance beeps
  4. **Scenes:** AI-generated scene descriptions; tap "more info" for rich detail; finger-explore for spatial object locations
  5. **People:** Face detection, age/gender estimation, proximity announcements
  6. **Currency:** Banknote recognition
  7. **Colors:** Color identification
  8. **Handwriting:** Handwritten text recognition (subset of languages)
  9. **Light:** Audible tone for ambient brightness
- **Recent addition:** Chat with Seeing AI to ask questions about scanned documents

### Audio Output
- Device speaker or connected audio devices
- TTS via platform accessibility (VoiceOver/TalkBack)
- Spatial audio cues (light channel tones, barcode proximity beeps)

### Visual Output
- None (audio-first design for blind users)

### Haptic Feedback
- Standard phone vibration for notifications

### Navigation
- Not a navigation tool

### Connectivity
- Some features work offline (Short Text, Currency, Light, Colors)
- Scene descriptions and rich features require internet
- iOS and Android

### Open APIs/SDKs
- No public SDK for Seeing AI itself
- Built on Azure AI Vision which has public APIs

### Notable Patents/Proprietary Tech
- Microsoft Research project; leverages proprietary Azure AI models
- Spatial touch-explore feature for scene images (finger on screen reveals object locations)
- Audio framing guidance for camera alignment

### Pricing
- **Completely free**

**Sources:**
- [Seeing AI - Google Play](https://play.google.com/store/apps/details?id=com.microsoft.seeingai)
- [Microsoft Garage - Seeing AI](https://www.microsoft.com/en-us/garage/wall-of-fame/seeing-ai/)
- [Seeing AI Android Launch - Microsoft Blog](https://blogs.microsoft.com/accessibility/seeing-ai-app-launches-on-android-including-new-and-updated-features-and-new-languages/)
- [AFB - Seeing AI Review](https://afb.org/aw/18/8/15185)

---

## 6. Aira

**Manufacturer:** Aira Tech Corp (USA)
**Form Factor:** Service platform (app + optional smart glasses)

### Hardware Specs
- **No proprietary hardware** -- uses smartphone camera or compatible smart glasses
- **Compatible glasses:**
  - Envision Glasses (Google Glass EE2)
  - Ray-Ban Meta Smart Glasses (pilot program, 2025-2026)
  - ARx Glasses (pilot)
  - Agiga Echo Vision Glasses (in development)
- **Smartphone:** iOS and Android app

### AI/ML Stack
- **Human-in-the-loop primary model:** Trained human agents ("Explorers" connect to "Agents") view live video feed and provide real-time guidance
- **AI augmentation:** OCR, object recognition assist agents on their dashboard
- **Agent dashboard integrates:** Live video, GPS, Google Maps, transit data, Uber/Lyft services
- **Not primarily AI-driven** -- human visual interpretation is the core service

### Audio Output
- Phone speaker/earphones or smart glasses built-in speakers
- Real-time voice conversation with human agent

### Visual Output
- None for the user (agents see the video feed on their dashboard)

### Haptic Feedback
- None reported

### Navigation
- **Human-guided navigation:** Agents provide verbal turn-by-turn directions using live video + GPS + Maps
- Indoor and outdoor navigation
- Transit assistance, airport navigation, shopping guidance

### Connectivity
- Requires internet (WiFi or cellular) for video streaming to agents
- Bluetooth for smart glasses connection
- GPS for location context

### Open APIs/SDKs
- Integration API with Envision Glasses
- Meta Wearables Device Access Toolkit integration
- Aira Access program for businesses/venues (free access at partner locations)

### Notable Patents/Proprietary Tech
- Proprietary agent dashboard merging video + geospatial data
- "Aira Access" partner network (airports, banks, grocery stores, museums)
- Trained professional visual interpreters (not casual volunteers)

### Pricing
- **Subscription tiers:** Silver, Gold, Platinum (each with 1-star, 2-star, 3-star levels)
- Pricing not publicly listed; subsidized for accessibility
- **Aira Access:** Free at 500+ partner locations (airports, universities, retailers)
- No price increases in 2025; 10%+ minute increases for all Metal Plan subscribers

**Sources:**
- [Aira Pricing](https://aira.io/pricing/)
- [Aira Smart Glasses](https://aira.io/using-smart-glasses-with-aira/)
- [Aira New Wearables Pilot](https://aira.io/new-wearables-pilot/)
- [Aira + Meta AI Glasses](https://aira.io/aira-ai-glasses-meta/)

---

## 7. dotLumen Glasses

**Manufacturer:** dotLumen (Romania)
**Form Factor:** Non-visual smart glasses (haptic navigation headset)

### Hardware Specs
| Spec | Detail |
|------|--------|
| Cameras | 6 total: 3 near-field (feet to few meters), 3 far-field (up to 10 m) |
| Processing | 90 FPS 3D environment processing; NVIDIA Inception member |
| Processor | Undisclosed (NVIDIA partnership suggests Jetson-class) |
| Haptic System | Patented forehead-contact actuators; pulls head direction up to 100x/sec |
| Connectivity | Fully offline operation; no internet required |
| Battery | Undisclosed |
| Weight | Undisclosed (described as "wearable headset form factor") |

### AI/ML Stack
- **Pedestrian Autonomous Driving AI (PAD AI):** Proprietary technology
- **On-device only:** 100% offline, no cloud dependency
- **Tasks:**
  - Real-time 3D spatial mapping (no pre-mapped environments needed)
  - Obstacle detection and avoidance
  - Safe path planning and computation
  - Sidewalk detection and following
  - Dynamic re-routing around obstacles
- **Processing:** 90 environment scans per second from 6 cameras
- **No OCR, no scene description, no face recognition** -- pure navigation

### Audio Output
- Audio cues for alerts (details sparse)
- Primary output is haptic, not audio

### Visual Output
- **None** -- non-visual goggles by design (opaque housing)

### Haptic Feedback
- **Primary interface:** Patented haptic system on forehead
- Gently "pulls" user's head in correct direction
- Up to 100 haptic updates per second
- Communicates: direction, obstacles, path corrections
- Described as similar to a guide dog's gentle pulling

### Navigation
- **Core function:** Autonomous pedestrian navigation
- No GPS dependency (pure vision-based navigation)
- Detects: obstacles, curbs, stairs, doors, other pedestrians, vehicles
- Real-time path planning in unknown environments
- Does not require pre-mapping

### Connectivity
- Fully offline
- Connectivity details for updates not disclosed

### Open APIs/SDKs
- No public API/SDK reported
- Uses Dassault Systemes 3DEXPERIENCE platform for development

### Notable Patents/Proprietary Tech
- **Patented haptic interface** for directional guidance via forehead contact
- **PAD AI (Pedestrian Autonomous Driving)** -- proprietary
- CES 2026 Accessibility Award winner
- CTA Foundation Pitch Competition winner
- Tested by 300+ visually impaired individuals across 30 countries

### Pricing
- **Estimated:** EUR 5,000-10,000
- Currently shipping in Romania only; international orders handled individually
- US market entry targeted Q4 2025
- Goal: 10,000 units by end of 2026

**Sources:**
- [dotLumen Glasses](https://www.dotlumen.com/glasses)
- [dotLumen CES 2026 Award](https://www.dotlumen.com/post/lumen-wins-the-ces-2026-accessibility-award-on-the-global-technology-stage)
- [Interview with dotLumen](https://en.ain.ua/2025/02/20/interview-with-lumen/)
- [NVIDIA Blog - dotLumen](https://blogs.nvidia.com/blog/dotlumen-ai-podcast/)
- [Envision Blog - CES 2024 dotLumen](https://www.letsenvision.com/blog/ces-2024-dotlumens-haptic-navigation-smart-glasses)

---

## 8. WeWalk Smart Cane 2

**Manufacturer:** WeWalk (Turkey/UK)
**Form Factor:** Smart cane handle (attaches to standard white cane)

### Hardware Specs
| Spec | Detail |
|------|--------|
| Sensors | TDK 6-axis IMU, MEMS microphone, ultrasonic ToF sensor, gyroscope, magnetometer, compass, pressure sensor |
| Obstacle Detection | Ultrasonic head-level sensor + Pathfinder tip (ground to head); 30/120-degree detection angle |
| Speaker | Harman Kardon 2W speaker |
| Battery | ~20 hrs standby; 6-8 hrs active use; ~2 hrs to full charge |
| Weight | 152 g (handle only); 250 g (handle + graphite cane) |
| Connectivity | Bluetooth 4.2 |
| Controls | Push buttons (improved from touchpad) |
| Durability | Rainwater and weather resistant |
| Compatibility | Android 4.4+, iOS 10+ (VoiceOver/TalkBack compatible) |
| LED | Built-in flashlight |
| Vibration | Vibration motor for haptic alerts |

### AI/ML Stack
- **Cloud AI:** Built-in ChatGPT assistant for general queries, menu reading, landmark info
- **On-device:** Ultrasonic obstacle detection (no AI required), sensor fusion for orientation
- **No computer vision** (no camera)

### Audio Output
- Harman Kardon 2W speaker (3x more powerful than v1)
- Customizable audio alerts
- Voice navigation guidance
- ChatGPT voice responses

### Visual Output
- None (cane device)
- LED flashlight for visibility to others

### Haptic Feedback
- Vibration motor for obstacle proximity alerts
- Customizable vibration patterns

### Navigation
- **Turn-by-turn voice navigation** (via companion app + phone GPS)
- Real-time public transit tracking
- Step-by-step walking directions
- Ultrasonic obstacle detection (head-to-ground coverage)

### Connectivity
- Bluetooth 4.2 to smartphone
- Requires companion app (WeWalk app, iOS/Android)
- Internet via phone for AI and navigation features

### Open APIs/SDKs
- No public API/SDK reported

### Notable Patents/Proprietary Tech
- Pathfinder tip technology for ground-level detection
- TDK sensor integration with multi-sensor fusion
- Harman Kardon audio partnership

### Pricing
| Option | Price |
|--------|-------|
| Smart Cane (no voice assistant) | $850 |
| Smart Cane + Voice Assistant (lifetime) | $1,150 |
| Voice Assistant subscription | $4.99/month |

**Sources:**
- [WeWalk Product](https://wewalk.io/en/product/)
- [WeWalk Product Details](https://wewalk.io/en/product-details/)
- [TDK - WeWalk Partnership](https://www.tdk.com/en/featured_stories/entry_084-WeWALK-Smart-Cane-2.html)
- [Engadget - WeWalk CES 2025](https://www.engadget.com/home/the-wewalk-smart-cane-2-could-be-one-of-ais-few-good-use-cases-at-ces-2025-182020074.html)

---

## 9. Glidance Glide

**Manufacturer:** Glidance (USA)
**Form Factor:** Autonomous wheeled robot guide (handheld via telescoping handle)

### Hardware Specs
| Spec | Detail |
|------|--------|
| Primary Camera | Stereo depth camera, 50 ft (15 m) range |
| Secondary Cameras | Two stereo-depth cameras in handle area |
| Near-range Sensors | Obstacle and cliff/drop-off detection |
| Wheels | Two 7.5-inch replaceable terrain wheels with intelligent steering, power assist, dynamic brakes |
| Weight | <8 lbs (~3.6 kg) |
| Compact Height | 25 inches when fully collapsed |
| Battery | 6+ hrs active use; full-day with smart standby; USB-C charging |
| Audio | Built-in speaker and microphones |
| Controls | Ergonomic handle with selection buttons and programmable quick-access button |
| Durability | Water resistant; bump and impact resistant |
| Body | 8-inch tall rounded body with front bumper |

### AI/ML Stack
- **Proprietary:** "Sensible Wayfinding Service" -- computer vision + sense-making AI
- **On-device:** Stereo depth perception, obstacle detection, path planning
- **Cloud (subscription):** Map data, routing, OTA updates
- **Tasks:**
  - Automatic steering and braking
  - Path detection and following
  - Obstacle avoidance (stationary, moving, overhead)
  - Target identification (doors, crosswalks, stairs, elevators)
  - Waypoint detection and guidance

### Audio Output
- Built-in speaker for verbal directions and alerts
- Bluetooth audio support
- Microphone for voice input

### Visual Output
- None

### Haptic Feedback
- **Directional haptic feedback in the handle** -- guides user's walking direction
- Physical steering resistance and dynamic braking

### Navigation
- **Core function:** Autonomous pedestrian navigation
- **Directed mode:** Turn-by-turn via Google Maps or pre-programmed routes
- **Freestyle mode:** User-guided with AI obstacle avoidance
- Line-of-sight target guidance with auto-arrival detection
- Companion app for setup, route planning, settings

### Connectivity
- Bluetooth for audio
- WiFi/cellular for maps and cloud services (via companion app on phone)
- USB-C charging
- OTA firmware updates

### Open APIs/SDKs
- No public API/SDK reported

### Notable Patents/Proprietary Tech
- First autonomous robotic mobility aid for blind users
- "Sensible Wayfinding Service" proprietary AI
- Collaborative navigation (partial autonomy with user override)
- Founded by Amos Miller (blind CEO)

### Pricing
- **Device:** $1,499 USD
- **Subscription:** $30/month (required for full functionality, connectivity, updates)
- Currently in beta testing; Pioneer preorder rollout Spring 2026

**Sources:**
- [Glidance Product](https://glidance.io/product/)
- [Glidance FAQ](https://glidance.io/frequently-asked-questions/)
- [Glidance Roadmap](https://glidance.io/product-roadmap/)
- [New Atlas - Glide](https://newatlas.com/good-thinking/glide-white-cane-blind-assistive-device)
- [The Robot Report - Glidance](https://www.therobotreport.com/glide-works-with-people-with-blindness-navigate-world-says-glidance-ceo/)

---

## 10. Google Lookout

**Manufacturer:** Google (USA)
**Form Factor:** Mobile app (Android only)

### Hardware Specs
- **No proprietary hardware** -- runs on Android smartphones
- Uses device camera and sensors

### AI/ML Stack
- **Hybrid on-device + cloud:** Uses Google's computer vision and generative AI models
- **On-device:** OCR, barcode scanning, currency recognition, food label ID
- **Cloud:** AI-generated image captions, scene descriptions, generative descriptions
- **Modes:**
  1. **Text:** Real-time OCR in 30+ languages
  2. **Documents:** Full page capture and reading
  3. **Explore:** Identifies objects, people, text in real time
  4. **Currency:** Banknote identification
  5. **Food Labels:** Packaged food ID in 20+ countries
  6. **Find (beta):** Directed search for 7 categories (seating, bathrooms, etc.) with direction/distance
  7. **Images:** Capture and ask questions about images (generative AI)

### Audio Output
- Device speaker or connected audio
- TTS via Android TalkBack integration

### Visual Output
- None (audio-first for blind users)
- High-contrast UI for low vision

### Haptic Feedback
- Standard phone vibration

### Navigation
- **Find mode:** Direction and distance to specific object categories
- Not turn-by-turn navigation

### Connectivity
- Some features offline (text, currency)
- Image descriptions require internet
- Android only

### Open APIs/SDKs
- No public API for Lookout
- Built on Google Cloud Vision AI (which has public APIs)

### Notable Patents/Proprietary Tech
- Google's proprietary ML models for accessibility
- Multi-sensory design approach
- Audio framing guidance for camera alignment

### Pricing
- **Completely free**

**Sources:**
- [Google Lookout - Play Store](https://play.google.com/store/apps/details?id=com.google.android.apps.accessibility.reveal)
- [Google Blog - Lookout AI Updates](https://blog.google/company-news/outreach-and-initiatives/accessibility/ai-accessibility-update-gaad-2024/)
- [Google Blog - Lookout Overview](https://blog.google/outreach-initiatives/accessibility/lookout-discover-your-surroundings-help-ai/)

---

## 11. Lazarillo GPS

**Manufacturer:** Lazarillo Tec SpA (Chile)
**Form Factor:** Mobile app (iOS, Android)

### Hardware Specs
- **No proprietary hardware** -- runs on user's smartphone
- Leverages phone GPS, Bluetooth, and speakers

### AI/ML Stack
- **Minimal AI:** Primarily a geospatial/mapping application, not a vision AI tool
- **Core tech:** GPS positioning, Bluetooth beacon triangulation, GIS mapping
- **No computer vision, no OCR, no object detection**

### Audio Output
- Continuous audio announcements of surroundings
- Customizable voice engine, language, measurement units
- TTS for place names, directions, distances

### Visual Output
- Accessible map interface (for low-vision users)
- Customizable exploration screen layout

### Haptic Feedback
- Standard phone vibration for alerts

### Navigation
- **Core function:** Accessible GPS navigation
- **Outdoor:** GPS-based with 1-50 m accuracy
- **Indoor:** Bluetooth Low Energy beacons for precise positioning
- Turn-by-turn audio walking directions
- Continuous announcements: street names, intersections, nearby businesses, transit stops
- Category search (banks, restaurants, health, shopping, etc.)
- **B2B indoor mapping:** 3D LiDAR scanning, CAD/PDF floor plans, beacon deployment, 5G integration

### Connectivity
- Requires mobile data or WiFi
- Bluetooth for indoor beacon scanning
- GPS for outdoor positioning

### Open APIs/SDKs
- **Business API** for integration into webpages, mobile apps, kiosks
- Indoor mapping platform for venues (airports, shopping centers, hospitals, smart cities)
- Custom digital mapping packages for businesses

### Notable Patents/Proprietary Tech
- Flexible indoor positioning using beacons, 5G, visual positioning, WiFi
- Hybrid indoor/outdoor navigation seamless handoff
- Customizable accessible digital map platform

### Pricing
- **App:** Completely free for end users
- **Business (B2B):** Custom pricing based on area size and integrations

**Sources:**
- [Lazarillo Official](https://lazarillo.app/)
- [Lazarillo App Page](https://lazarillo.app/theapp/)
- [Lazarillo Business](https://lazarillo.app/business/)
- [Lazarillo - Google Play](https://play.google.com/store/apps/details?id=com.lazarillo)
- [Perkins School - Lazarillo Review](https://www.perkins.org/resource/lazarillo-free-accessible-gps-app-blind-and-visually-impaired/)

---

## 12. IrisVision

**Manufacturer:** IrisVision Global (USA)
**Form Factor:** VR headset (smartphone-based)

### Hardware Specs (IrisVision Live 2.0)
| Spec | Detail |
|------|--------|
| Platform | Google Pixel 7 Pro smartphone in custom headset |
| Camera | 50 MP (Pixel 7 Pro main camera) |
| Processor | Google Tensor G2 Octa-core |
| Display | 1440 x 3120 resolution (Pixel 7 Pro AMOLED) |
| FOV | 77 degrees (industry-leading for low-vision aids) |
| Magnification | Up to 14x in Scene Mode |
| Controls | Independent focus dials per eye, IPD dial, wireless joystick or voice control |
| Connectivity | Bluetooth remote, WiFi, cellular (via phone) |

### AI/ML Stack
- **On-device:** OCR for text-to-speech (works offline)
- **Tasks:** Real-time magnification, OCR, contrast enhancement
- **Not an AI recognition device** -- primarily video magnification

### Audio Output
- Device speaker
- Bluetooth audio
- Voice control support

### Visual Output
- **Primary function:** Real-time magnified video feed
- Up to 14x magnification
- Adjustable contrast and color modes
- YouTube Mode with voice-controlled video playback
- Scene mode, reading mode, and custom display settings

### Haptic Feedback
- None reported

### Navigation
- None (not a navigation device)

### Connectivity
- WiFi, Bluetooth, cellular (via embedded smartphone)

### Open APIs/SDKs
- No public API/SDK

### Notable Patents/Proprietary Tech
- Smartphone-in-headset architecture for cost reduction
- Proprietary low-vision optimization software overlay
- 77-degree FOV for low-vision (wider than competitors)

### Pricing
- **Discontinued** (IrisVision Live 2.0 no longer sold)
- Previous pricing: ~$2,500 (with Samsung/Pixel phone included)

**Sources:**
- [IrisVision Live 2.0](https://irisvision.com/live-2-0/)
- [IrisVision Official](https://irisvision.com/)
- [VisionAid - IrisVision Live 2.0](https://www.visionaid.co.uk/irisvision-live-20)

---

## 13. NuEyes Pro 3e (& e3+, Pro 4 LV)

**Manufacturer:** NuEyes (USA)
**Form Factor:** Smart glasses (AR/VR-style wearable)

### NuEyes e3+ Specifications (Primary Low-Vision Model)
| Spec | Detail |
|------|--------|
| Camera | 16 MP |
| Display | Ultra-high-definition dual displays |
| FOV | 110 degrees |
| Magnification | Up to 18x |
| Battery | 4+ hours |
| Weight | Lightweight ergonomic (exact weight undisclosed) |
| Features | OCR text-to-speech, voice commands, adjustable contrast, modular design |

### NuEyes Pro 3e Specifications (Entertainment/Enterprise)
| Spec | Detail |
|------|--------|
| Display | 1080p HD; 148-inch virtual screen |
| Weight | 68 g |
| Connectivity | USB-C to smartphones/PCs; 5G ready |
| Audio | Stereo speakers |
| Controls | Volume/brightness on frames |

### NuEyes Pro 4 LV (Low Vision)
| Spec | Detail |
|------|--------|
| Magnification | Up to 12x variable |
| Camera | Built-in HD camera with Samsung smartphone integration |
| Features | Contrast/color enhancement, OCR text-to-speech, voice control, hands-free |

### AI/ML Stack
- **On-device:** OCR for text-to-speech
- **Minimal AI:** Focus is on video magnification and display enhancement
- Voice commands for hands-free operation

### Audio Output
- Stereo speakers (Pro 3e)
- Speaker output on all models
- Voice control support

### Visual Output
- Real-time magnified video
- Contrast and color enhancement modes
- Large virtual display (148" equivalent on Pro 3e)

### Haptic Feedback
- None reported

### Navigation
- None

### Connectivity
- USB-C (plug and play to phones, PCs, Macs)
- 5G ready (Pro 3e)
- Bluetooth (select models)

### Open APIs/SDKs
- No public API/SDK

### Pricing
- **NuEyes Pro (older):** ~$5,995
- **Current models:** Contact NuEyes for pricing (not publicly listed for e3+ or Pro 4 LV)
- **Pro 3e:** ~$399 (entertainment/enterprise model)

**Sources:**
- [NuEyes Pro 3e](https://www.nueyes.com/pro3e)
- [NuEyes e3+](https://www.nueyes.com/e3)
- [NuEyes Pro 4 LV - NE Low Vision](https://nelowvision.com/product/nueyes-pro4-lv-low-vision-glasses/)
- [NuEyes e3+ - NE Low Vision](https://nelowvision.com/product/nueyes-e3-low-vision-glasses/)

---

## 14. Sullivan+ (Korea)

**Manufacturer:** TUAT Inc. / SK Telecom (South Korea)
**Form Factor:** Mobile app (iOS, Android)

### Hardware Specs
- **No proprietary hardware** -- runs on user's smartphone

### AI/ML Stack
- **Cloud AI:** SKT A.X Multimodal AI (trained on 1 billion+ images)
- **Tasks:**
  - **Image Recognition:** Describes scenes, objects, landscapes in detail
  - **AI Facescan:** Recognizes age, gender, facial expressions (e.g., "a 33-year-old man is smiling")
  - **Text Recognition (OCR)**
  - **Object Finder:** Locates specific objects
  - **Currency Recognition**
  - **Color Recognition**
  - **Face Registration:** Learn and recognize specific people
- **Sullivan Finder (newer app):** Shopping assistance, restaurant info, walking/safety guidance

### Audio Output
- Device TTS
- Detailed verbal descriptions of scenes

### Visual Output
- None (audio-first for blind users)

### Haptic Feedback
- Standard phone haptics

### Navigation
- Sullivan Finder includes walking/safety features
- Not a full navigation solution

### Connectivity
- Requires internet for AI features
- iOS and Android

### Open APIs/SDKs
- No public API/SDK reported

### Notable Patents/Proprietary Tech
- SKT's A.X Multimodal AI trained on 1 billion+ images
- Seoul Design Award winner
- Backed by SK Telecom (major Korean telecom)

### Pricing
- **Free**

**Sources:**
- [Sullivan+ - Google Play](https://play.google.com/store/apps/details?id=tuat.kr.sullivan)
- [Sullivan+ - PR Newswire](https://www.prnewswire.com/news-releases/ai-based-app-gives-voice-guidance-for-visually-impaired-300960716.html)
- [Sullivan+ - Seoul Design Award](http://www.seouldesignaward.or.kr/en/winners/2023/sullivan-plus)
- [SK Telecom - Sullivan](https://www.sktelecom.com/en/feature/feature.do)

---

## 15. RealSAM Pocket

**Manufacturer:** RealThing AI (UK)
**Form Factor:** Modified Samsung smartphone with custom software

### Hardware Specs
| Spec | Detail |
|------|--------|
| Base Device | Samsung Galaxy handset (waterproof model) |
| Connectivity | 5G unlocked (all major carriers), WiFi, Bluetooth |
| Audio | Large built-in speaker for quality sound |
| Controls | One large physical button + full voice control |
| Hearing Aid Compat. | Direct: Phonak hearing aids; others via Bluetooth streaming device |

### AI/ML Stack
- **Cloud:** Voice-controlled AI assistant for natural language commands
- **Integration:** Be My Eyes (video volunteer calls), built-in magnifier
- **Tasks:** Voice calls, messaging, book/media playback, navigation assistance, emergency support
- **Not primarily a vision AI device** -- accessible smartphone platform

### Audio Output
- High-quality built-in speaker
- Bluetooth audio to hearing aids and speakers
- Voice-first interface (tap screen and talk)

### Visual Output
- Magnifier app for low-vision users
- Otherwise audio-first design

### Haptic Feedback
- Standard phone haptics

### Navigation
- Navigation assistance via voice commands
- Relies on phone GPS + third-party navigation

### Connectivity
- 5G cellular, WiFi, Bluetooth
- Compatible with all major carriers (unlocked)

### Open APIs/SDKs
- No public API (proprietary RealSAM software layer)
- Cross-platform versions (iOS, Android, Web App) arriving late 2025

### Notable Patents/Proprietary Tech
- Voice-first smartphone UI requiring no visual interaction
- Natural language command processing
- RNIB Library integration (UK), Talking Book service, BBC podcasts

### Pricing
- ~$349-449 (varies by market; includes phone + RealSAM software)
- Ongoing service for content access (library, news)

**Sources:**
- [RealSAM Pocket - Official US](https://realsam.us/pocket/)
- [RealSAM - RealThing AI](https://realthing.ai/products/realsam-assistive-technology/)
- [RNIB - RealSAM Pocket](https://www.rnib.org.uk/living-with-sight-loss/assistive-aids-and-technology/reading-and-writing/what-is-realsam-pocket/)
- [Amazon - RealSAM Pocket](https://www.amazon.com/RealSAM-Voice-Operated-Smartphone-Visually-Impaired/dp/B0BGCWV67M)

---

## 16. Ray-Ban Meta Smart Glasses

**Manufacturer:** Meta / EssilorLuxottica (USA/Italy)
**Form Factor:** Fashion smart glasses (Ray-Ban Wayfarer style)

### Hardware Specs
| Spec | Detail |
|------|--------|
| Processor | Qualcomm Snapdragon AR1 Gen1 |
| Camera | 12 MP ultrawide |
| Video | 3K video recording |
| Audio | 5-microphone array, open-ear speakers |
| Battery | ~4 hrs active use (glasses); 154 mAh cell |
| Charging Case | 8 additional charges; 36 hrs total; 133 g case |
| Weight | ~50.8 g (glasses) |
| Durability | IPX4 water resistant |
| Connectivity | Bluetooth, WiFi |

### AI/ML Stack
- **Meta AI:** Cloud-based multimodal AI assistant
- **Tasks:**
  - "Hey Meta, look at this" -- AI describes what camera sees
  - Object recognition, environmental description
  - OCR (reading text)
  - Translation (real-time)
  - General AI Q&A (Llama-based models)
- **Cloud-only:** All AI features require internet via connected phone
- **Be My Eyes integration:** Voice-activated volunteer calls

### Audio Output
- Open-ear speakers (5 speakers)
- 5-microphone system for calls and voice commands
- No bone conduction

### Visual Output
- **Gen 2 (2024):** No display
- **Ray-Ban Meta Display (2025):** Small in-lens monocular display for notifications, navigation, translation captions
- No AR overlay or magnification for low vision

### Haptic Feedback
- None reported

### Navigation
- Meta AI can describe surroundings
- Not a turn-by-turn navigation device
- Relies on phone for maps/directions

### Connectivity
- Bluetooth 5.x to smartphone
- WiFi for AI features
- Meta View companion app (iOS/Android)
- Requires Meta account

### Open APIs/SDKs
- **Meta Wearables Device Access Toolkit** (for developer integrations like Aira)
- Limited third-party app ecosystem

### Notable Patents/Proprietary Tech
- Consumer smart glasses with mainstream fashion design (Ray-Ban collab)
- Snapdragon AR1 purpose-built AR processor
- First mainstream smart glasses with Be My Eyes accessibility

### Pricing
- **Starting at $299** (no display model)
- **Ray-Ban Meta Display:** ~$399+ (with monocular display)
- No subscription required for Meta AI

**Sources:**
- [Ray-Ban Meta Official](https://www.ray-ban.com/usa/ray-ban-meta-ai-glasses)
- [Ray-Ban Meta Specs](https://www.ray-ban.com/usa/discover-ray-ban-meta-ai-glasses/clp)
- [Be My Eyes on Meta Glasses](https://www.bemyeyes.com/be-my-eyes-smartglasses/)
- [AFB - Meta Glasses Review](https://afb.org/aw/fall2025/meta-glasses-review)
- [Consumer Reports - Meta Glasses for Blind](https://www.consumerreports.org/electronics/emerging-technology/can-ray-ban-meta-ai-glasses-guide-the-blind-a6400488928/)

---

## 17. Apple Vision Pro

**Manufacturer:** Apple (USA)
**Form Factor:** Mixed-reality headset

### Hardware Specs
| Spec | Detail |
|------|--------|
| Processor | Apple M5 chip + R1 chip (sensor processing) |
| Cameras | 12 total: 2 high-res main (18mm f/2.0, 6.5 stereo MP), 6 world-tracking, 4 eye-tracking |
| Sensors | TrueDepth camera, LiDAR Scanner |
| Display | Dual micro-OLED; 23 million pixels total; 3660x3200 per eye |
| Refresh Rate | 90 FPS (auto-adjusts to 96/100 FPS) |
| Weight | 600-650 g (headset); 353 g (external battery) |
| Battery | 2 hours (external battery pack); USB-C power pass-through |
| Audio | Spatial Audio with personalized head tracking; dual-driver pods near ears |
| OS | visionOS 3 (2025) |

### AI/ML Stack (Accessibility Features)
- **On-device ML:**
  - **Live Recognition:** Describes surroundings, finds objects, reads documents using on-device ML
  - VoiceOver with spatial audio for 3D UI navigation
  - Zoom magnification of entire view including real-world passthrough
  - Pointer control via eye/hand/head tracking
- **Cloud:** Siri with Apple Intelligence
- **New in visionOS 3 (2025):**
  - Magnifier using main camera system for real-world magnification
  - Live Recognition for object/text/scene description
  - Camera API for third-party accessibility apps (Be My Eyes)

### Audio Output
- Spatial Audio with personalized HRTF
- Built-in speakers near ears (not bone conduction)
- Bluetooth audio output
- VoiceOver with 3D spatial cues

### Visual Output
- **Full AR/VR capability:** Real-time passthrough with digital overlays
- **Magnification:** Zoom magnifies real-world view through cameras
- Contrast enhancement options
- Reduce motion, reduce transparency accessibility options
- Color filters for color blindness

### Haptic Feedback
- None on headset (hand tracking is gesture-based, no controllers)

### Navigation
- Not a navigation device
- LiDAR provides depth sensing for spatial mapping
- Indoor spatial awareness

### Connectivity
- WiFi 6E, Bluetooth 5.3, USB-C

### Open APIs/SDKs
- **visionOS SDK:** Full developer platform (Swift, SwiftUI, RealityKit)
- **New Accessibility Camera API (2025):** Third-party apps can access main camera for visual assistance
- ARKit, RealityKit frameworks

### Notable Patents/Proprietary Tech
- R1 chip: 12ms sensor-to-display latency
- Eye tracking for UI control and foveated rendering
- Custom micro-OLED displays (highest resolution in any headset)
- LiDAR + photogrammetry spatial mapping
- Optic ID (iris authentication)

### Pricing
- **$3,499** (M5 model, launched late 2025)
- No subscription

**Sources:**
- [Apple Vision Pro Specs](https://www.apple.com/apple-vision-pro/specs/)
- [Apple Vision Pro Tech Specs - Support](https://support.apple.com/en-us/117810)
- [Apple Newsroom - 2025 Accessibility](https://www.apple.com/newsroom/2025/05/apple-unveils-powerful-accessibility-features-coming-later-this-year/)
- [AppleVis - Vision Pro Accessibility](https://www.applevis.com/blog/apple-documents-apple-vision-pro-accessibility-features-including-voiceover-zoom-guides)

---

## 18. Hable One

**Manufacturer:** Hable (Netherlands)
**Form Factor:** Portable Braille keyboard/controller

### Hardware Specs
| Spec | Detail |
|------|--------|
| Dimensions | 10 cm x 5 cm (4" x 2") |
| Buttons | 6 tactile Braille input keys + 2 function keys |
| Connectivity | Bluetooth Low Energy 5.1 (up to 5 m range) |
| Battery | Month-long battery life |
| Material | Recycled ABS plastic |
| Compatibility | iOS 9+, Android 9+ (phones and tablets) |
| Haptic | Vibration for battery level indication (1/2/3 vibrations) |

### AI/ML Stack
- **None** -- Hable One is an input device, not an AI device
- Works as a Braille keyboard and screen reader remote control

### Audio Output
- None on device (uses phone's audio/screen reader output)

### Visual Output
- None (tactile-only device)

### Haptic Feedback
- Vibration motor for battery level feedback
- Tactile key differentiation by touch

### Navigation
- Navigates phone UI via screen reader (VoiceOver/TalkBack)
- Not a physical navigation device

### Connectivity
- Bluetooth LE 5.1
- Pairs with one device at a time

### Open APIs/SDKs
- No public API (works via standard Bluetooth HID profile with screen readers)

### Notable Patents/Proprietary Tech
- World's smallest Braille keyboard
- Works as both input keyboard AND full remote control for phone
- Supports Braille Grade 1 and Grade 2
- Dictation function integration

### Pricing
- **~$250-275** (varies by region; ~GBP 174)
- 1-year warranty

**Sources:**
- [Hable One Official](https://www.iamhable.com/en-am/products/hable-one-keyboard)
- [BoundlessAT - Hable One](https://www.boundlessat.com/Blindness/Braille-Displays/Hable-One-Braille-Keyboard)
- [Perkins School - Hable One](https://www.perkins.org/resource/hable-one-controller-for-smartphones-and-tablets/)
- [CNIB - Hable One](https://cnibsmartlife.ca/products/hable-one-braille-keyboard)

---

## 19. BrainPort Vision Pro

**Manufacturer:** Wicab Inc. (USA)
**Form Factor:** Headband with camera + tongue electrode array

### Hardware Specs
| Spec | Detail |
|------|--------|
| Camera | Small video camera on headband; adjustable FOV; works in varied lighting |
| Electrode Array | 394-400 electrodes on tongue pad (postage-stamp sized) |
| Battery | 2 rechargeable lithium batteries (included) |
| Sizes | 3 headset sizes available |
| Package Contents | Headset with camera, padded case, 2 batteries, charger, manual |
| Connection | Flexible cable connects tongue array to headset (anti-drop) |

### AI/ML Stack
- **None** -- not an AI device
- Direct camera-to-tongue electrotactile signal conversion
- White pixels = strong stimulation; black = none; gray = medium
- User's brain learns to interpret patterns as visual information

### Audio Output
- None (purely tactile output device)

### Visual Output
- None (designed for profoundly blind users; output is via tongue)

### Haptic Feedback
- **Core function:** Electrotactile stimulation on tongue surface
- 394-400 electrode array generates "bubble-like" patterns
- Users learn to interpret patterns as shape, size, location, and motion of objects
- Training required: 10 hours over 3 days with certified instructor

### Navigation
- Orientation and mobility aid (obstacle detection, spatial awareness)
- Used as adjunct to white cane or guide dog
- Not GPS or map-based navigation

### Connectivity
- Self-contained device; no WiFi, Bluetooth, or phone required
- Fully offline

### Open APIs/SDKs
- None (FDA-regulated medical device)

### Notable Patents/Proprietary Tech
- **FDA-approved (2015)** -- Class II medical device (De Novo classification)
- **CE marked** for EU market
- Electrotactile sensory substitution (tongue replaces eyes)
- Pioneered by neuroscientist Paul Bach-y-Rita
- Available in: USA, EU, Chile, Peru, Hong Kong

### Pricing
- **~$7,995** (previously $10,000; price reduced)
- Training course additional cost (10 hours with certified instructor)

**Sources:**
- [BrainPort Vision Pro Official](https://www.wicab.com/brainport-vision-pro)
- [BrainPort - AskJAN](https://askjan.org/products/BrainPort-Vision-Pro.cfm)
- [Wikipedia - Brainport](https://en.wikipedia.org/wiki/Brainport)
- [Retinal Physician - BrainPort](https://www.retinalphysician.com/issues/2012/janfeb/visual-perception-for-the-blind-the-brainport-vision-device/)
- [FDA - BrainPort Review](https://www.accessdata.fda.gov/cdrh_docs/reviews/DEN130039.pdf)

---

## 20. SoundSight

**Manufacturer:** SoundSight / Academic research project (UK)
**Form Factor:** Smartphone app (research/prototype)

### Hardware Specs
- **No proprietary hardware** -- runs on existing smartphones
- Uses phone camera, depth sensor (if available), temperature sensor, speaker

### AI/ML Stack
- **Sensory substitution (sonification):** Converts visual, distance, and thermal data into real-time soundscapes
- **Not traditional AI:** Algorithmic mapping of sensor data to sound parameters
- **Three modes:**
  - **3D/Distance mode:** Spatial audio representing depth and position
  - **Color mode:** Sonifies colors into distinct sounds
  - **Thermal mode:** Temperature information as sound
- Users can customize: tones, rainfall sounds, speech, instruments, musical tracks as output

### Audio Output
- **Core output:** Real-time generated soundscapes
- Headphones recommended (spatial audio)
- Users can select sound style (tones, instruments, speech, music)
- Thousands of high-quality sounds controlled in real-time

### Visual Output
- None (designed as sensory substitution for vision)

### Haptic Feedback
- Phone vibration planned for future versions (central pixel depth as vibration intensity)
- Not yet implemented

### Navigation
- Indoor navigation via 3D mode
- Following others, mapping new routes
- Checking seat availability
- Not GPS-based turn-by-turn

### Connectivity
- Standard smartphone connectivity

### Open APIs/SDKs
- Academic/research project; not commercially distributed
- Published research papers available

### Notable Patents/Proprietary Tech
- Novel sonification approach allowing user-customizable sound mappings
- Multi-modal sensory substitution (color + distance + temperature)
- Smartphone-based (no special hardware needed)

### Pricing
- **Research project** -- not commercially available as of 2026
- Website: www.SoundSight.co.uk

**Sources:**
- [SoundSight - Springer Nature](https://link.springer.com/article/10.1007/s12193-021-00376-w)
- [SoundSight - ResearchGate](https://www.researchgate.net/publication/352907777)

---

## Summary Comparison Matrix

### Hardware Comparison

| Product | Form Factor | Weight | Camera | Display | Battery | Price |
|---------|-------------|--------|--------|---------|---------|-------|
| OrCam MyEye 3 Pro | Clip-on module | 22.5 g | 13 MP | None (audio) | 60-90 min | $4,250 |
| OrCam Read 3 | Handheld | 22.5 g | 13 MP | Phone screen | 4.5-5 hrs | ~$1,890 |
| Envision Glasses | Smart glasses | 46 g | 8 MP, 80-deg FOV | 640x360 prism | 5-6 hrs | $1,899-3,499 |
| eSight Go | Head-mounted | 170 g | HD autofocus | Dual FHD OLED | 3 hrs | $4,950 |
| eSight 4 | Head-mounted | 500 g | HD autofocus | Dual OLED | 3 hrs | $6,950 |
| Be My Eyes | App | N/A | Phone cam | Phone screen | N/A | Free |
| Seeing AI | App | N/A | Phone cam | Phone screen | N/A | Free |
| Aira | App + glasses | N/A | Phone/glasses cam | None | N/A | Subscription |
| dotLumen | Haptic headset | Undisclosed | 6 cameras | None (opaque) | Undisclosed | EUR 5K-10K |
| WeWalk Smart Cane 2 | Cane handle | 152 g | None | None | 20 hrs | $850-1,150 |
| Glidance Glide | Robot guide | <3.6 kg | Stereo depth + 2 | None | 6+ hrs | $1,499 + $30/mo |
| Google Lookout | App | N/A | Phone cam | Phone screen | N/A | Free |
| Lazarillo GPS | App | N/A | None | Phone screen | N/A | Free |
| IrisVision Live 2.0 | VR headset | ~300 g | 50 MP | 1440x3120 | 2-3 hrs | ~$2,500 (discontinued) |
| NuEyes e3+ | Smart glasses | Undisclosed | 16 MP | UHD dual, 110-deg | 4+ hrs | ~$5,995 |
| Sullivan+ | App | N/A | Phone cam | Phone screen | N/A | Free |
| RealSAM Pocket | Smartphone | ~180 g | Phone cam | Phone screen | All-day | ~$349-449 |
| Ray-Ban Meta | Smart glasses | 50.8 g | 12 MP ultrawide | None/Monocle | 4 hrs | From $299 |
| Apple Vision Pro | MR headset | 600-650 g | 12 cameras + LiDAR | Dual micro-OLED 23M px | 2 hrs | $3,499 |
| Hable One | Braille keyboard | ~50 g | None | None | 1 month | ~$250 |
| BrainPort Vision Pro | Headband + tongue | Undisclosed | Video camera | None (tongue) | Recharge Li-ion | ~$7,995 |
| SoundSight | App | N/A | Phone cam | None | N/A | Research only |

### AI/ML Capability Comparison

| Product | OCR | Scene Desc. | Object Det. | Face Recog. | Navigation | On-Device AI | Cloud AI |
|---------|-----|-------------|-------------|-------------|------------|-------------|----------|
| OrCam MyEye 3 Pro | Yes | No | Barcode/products | Yes | No | Yes | Partial |
| Envision Glasses | Yes | Yes (GPT-5) | Yes | Yes | Planned | Partial | Yes |
| eSight Go/4 | No | No | No | No | No | Minimal | No |
| Be My Eyes | Yes | Yes (GPT-4o) | Yes | No | No | No | Yes |
| Seeing AI | Yes | Yes | Yes | Yes | No | Partial | Yes |
| Aira | Yes (agent) | Yes (human) | Yes (human) | Yes (human) | Yes (human) | No | Human+AI |
| dotLumen | No | No | Obstacles | No | Yes (core) | Yes | No |
| WeWalk Cane 2 | No | No | Ultrasonic | No | Yes | Sensors | ChatGPT |
| Glidance Glide | No | No | Yes (depth) | No | Yes (core) | Yes | Partial |
| Google Lookout | Yes | Yes | Yes | Yes | Find mode | Partial | Yes |
| Lazarillo GPS | No | No | No | No | Yes (core) | No | GPS only |
| IrisVision | Yes (OCR) | No | No | No | No | Partial | No |
| NuEyes e3+ | Yes (OCR) | No | No | No | No | Partial | No |
| Sullivan+ | Yes | Yes | Yes | Yes | Partial | No | Yes |
| RealSAM Pocket | No | No | No | No | Basic | No | Voice AI |
| Ray-Ban Meta | Yes | Yes | Yes | No | No | No | Yes |
| Apple Vision Pro | Yes | Yes | Yes | No | Spatial | Yes | Yes |
| Hable One | N/A | N/A | N/A | N/A | N/A | N/A | N/A |
| BrainPort Vision | No | No | Basic shapes | No | Orientation | No | No |
| SoundSight | No | Sonification | Distance | No | Indoor | Local proc. | No |

### Output Modality Comparison

| Product | Audio/TTS | Spatial Audio | AR Visual Overlay | Magnification | Haptic | Electrotactile | Braille |
|---------|-----------|---------------|-------------------|---------------|--------|-----------------|---------|
| OrCam MyEye 3 Pro | Yes | No | No | Yes (phone) | No | No | No |
| Envision Glasses | Yes | No | Minimal (prism) | No | No | No | No |
| eSight Go/4 | Yes | No | No | Yes (24x) | No | No | No |
| Be My Eyes | Yes | No | No | No | No | No | No |
| Seeing AI | Yes | Partial | No | No | No | No | No |
| Aira | Yes | No | No | No | No | No | No |
| dotLumen | Minimal | No | No | No | Yes (core) | No | No |
| WeWalk Cane 2 | Yes | No | No | No | Yes | No | No |
| Glidance Glide | Yes | No | No | No | Yes (handle) | No | No |
| Google Lookout | Yes | No | No | No | No | No | No |
| Lazarillo GPS | Yes | No | No | No | No | No | No |
| IrisVision | Yes | No | No | Yes (14x) | No | No | No |
| NuEyes e3+ | Yes | No | No | Yes (18x) | No | No | No |
| Sullivan+ | Yes | No | No | No | No | No | No |
| RealSAM Pocket | Yes | No | No | Yes (basic) | No | No | No |
| Ray-Ban Meta | Yes | No | Partial (Display) | No | No | No | No |
| Apple Vision Pro | Yes | Yes (3D) | Yes (full) | Yes (Zoom) | No | No | No |
| Hable One | No | No | No | No | Yes (basic) | No | Yes (input) |
| BrainPort Vision | No | No | No | No | No | Yes (core) | No |
| SoundSight | Yes (core) | Yes (3D) | No | No | Planned | No | No |

### Pricing Tier Summary

| Tier | Products |
|------|----------|
| **Free** | Be My Eyes, Seeing AI, Google Lookout, Lazarillo, Sullivan+ |
| **$250-500** | Hable One (~$250), RealSAM Pocket (~$349-449), Ray-Ban Meta (from $299) |
| **$850-1,500** | WeWalk Cane 2 ($850-1,150), Glidance Glide ($1,499 + $30/mo) |
| **$1,500-3,500** | Envision Glasses ($1,899-3,499), IrisVision (~$2,500 disc.), Apple Vision Pro ($3,499) |
| **$4,000-7,000** | OrCam MyEye 3 Pro ($4,250), eSight Go ($4,950), NuEyes (~$5,995), eSight 4 ($6,950) |
| **$5,000-10,000** | dotLumen (EUR 5K-10K est.), BrainPort Vision Pro (~$7,995) |
| **Subscription** | Aira (tiered), Glidance ($30/mo), WeWalk voice assistant ($4.99/mo) |

### Key Competitive Insights for ClaraVis

1. **AR Visual Overlays Gap:** Only Apple Vision Pro offers true real-time AR overlays for vision assistance. Ray-Ban Meta Display has minimal monocular notification display. **No competitor in the assistive tech space provides real-time visual AR overlays (contours, labels, alerts) optimized for visually impaired users on affordable hardware.** This is ClaraVis's primary differentiator.

2. **On-Device vs Cloud:** Most AI-powered products (Envision, Be My Eyes, Seeing AI, Sullivan+) depend on cloud processing. OrCam and dotLumen are notable exceptions with full offline operation. ClaraVis's offline YOLO approach aligns with OrCam/dotLumen philosophy but adds visual overlays that neither offers.

3. **Navigation Void in Glasses:** Navigation-focused products are canes (WeWalk), robots (Glidance), apps (Lazarillo), or non-visual headsets (dotLumen). No AR glasses product combines object detection with visual navigation overlays for low-vision users.

4. **Price Positioning:** The affordable end ($299-500) is occupied by consumer tech (Ray-Ban Meta, Hable, RealSAM). Dedicated assistive devices cluster at $4,000-7,000. ClaraVis on Jetson + affordable display could target the $1,000-2,000 gap.

5. **Haptic Innovation:** dotLumen (forehead pulling) and Glidance (handle steering) demonstrate strong interest in non-audio guidance. BrainPort's tongue electrotactile is the most radical approach. ClaraVis could consider haptic supplements to visual overlays.

6. **Model Trend:** The industry is rapidly shifting toward multimodal AI (GPT-4V/5, Gemini). Products without generative AI capabilities (eSight, NuEyes, BrainPort) focus on specific functions. ClaraVis's fine-tuned YOLO model is efficient for real-time on-device detection but lacks scene description -- VLM addition on Jetson could close this gap.

---

*Research compiled 2026-03-11. All specifications verified via manufacturer websites, retailer listings, and published reviews. Specifications may have been updated since research date.*
