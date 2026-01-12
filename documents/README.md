# TRASH - Premium Custom Engine Wild West Card Game 🎴🤠

A fully native Android card game built with **custom premium game engines** using LibGDX, Oboe, and Skia.

## 🚀 Premium Tech Stack

### **Game Framework: LibGDX**
- Cross-platform game framework
- OpenGL ES rendering
- Asset management
- Input handling
- Game loop management

### **Audio Engine: Oboe (Google's Official)**
- Low-latency audio (2-5ms)
- Native C++ audio processing
- Spatial audio (3D sound)
- Professional audio mixing
- Real-time audio effects

### **Graphics Engine: Skia (Chrome's Graphics Engine)**
- Hardware-accelerated 2D graphics
- Custom GLSL shaders
- Vector graphics and paths
- Advanced text rendering
- Image effects and filters

### **Game Architecture: GCMS**
- Game Command Management System
- Command pattern for user actions
- Event-driven state updates
- Reactive state management
- Clean architecture

## 🏗️ Architecture Overview

```
TRASH Game App (Native Android)
│
├── Java/Kotlin Layer
│   ├── MainActivity (Android entry point)
│   ├── LibGDX AndroidLauncher
│   ├── JNI Bridge (Java ↔ C++)
│   ├── GCMS Controller
│   └── Firebase Integration
│
├── LibGDX Game Layer
│   ├── TrashGame (ApplicationAdapter)
│   ├── Game Loop
│   ├── Input Handling
│   └── Asset Management
│
├── Oboe Audio Engine (C++)
│   ├── AudioEngine (Core)
│   ├── AudioMixer
│   ├── SpatialAudio (3D sound)
│   └── JNI Bridge
│
├── Skia Graphics Engine (C++)
│   ├── GraphicsEngine (Core)
│   ├── Renderer (OpenGL ES)
│   ├── ShaderManager (Custom shaders)
│   ├── CardRenderer (Card visuals)
│   ├── ParticleEffect (Wild West effects)
│   └── JNI Bridge
│
└── GCMS Game Logic
    ├── Commands (Draw, Place, Flip, etc.)
    ├── Events (CardDrawn, RoundEnded, etc.)
    ├── Handlers (Command processors)
    ├── Models (Card, Deck, State)
    └── Controller (Central orchestrator)
```

## 🎮 Features

### **Premium Graphics**
- ✅ Custom OpenGL ES 3.0 rendering
- ✅ Wild West themed shaders (wood grain, vintage effects)
- ✅ Smooth card animations
- ✅ Particle effects (gold coins, dust, fire sparks)
- ✅ 4x MSAA anti-aliasing
- ✅ Hardware acceleration

### **Professional Audio**
- ✅ Low-latency audio engine (Oboe)
- ✅ Spatial 3D audio positioning
- ✅ Real-time audio effects
- ✅ Dynamic music system
- ✅ High-fidelity sound mixing
- ✅ Reverb and spatial audio

### **Game Architecture**
- ✅ Command pattern for all actions
- ✅ Event-driven state updates
- ✅ Reactive game state management
- ✅ Clean separation of concerns
- ✅ Scalable architecture

### **Android Integration**
- ✅ Native Android performance
- ✅ Hilt dependency injection
- ✅ Firebase integration (Firestore, Auth, Storage)
- ✅ Room database for local data
- ✅ Play Store ready

## 📱 Project Structure

```
TrashApp/
├── app/                          # Main Android application
│   ├── src/main/
│   │   ├── java/com/trashapp/
│   │   │   ├── MainActivity.kt
│   │   │   └── TrashApplication.kt
│   │   ├── AndroidManifest.xml
│   │   └── res/
│   └── build.gradle.kts
│
├── libgdx-core/                  # LibGDX game framework
│   ├── src/main/java/com/trashapp/libgdx/
│   │   ├── TrashGame.kt          # Main game class
│   │   └── AndroidLauncher.kt    # Android integration
│   └── build.gradle.kts
│
├── oboe-audio/                   # Oboe audio engine (C++)
│   ├── src/main/
│   │   ├── cpp/
│   │   │   ├── include/
│   │   │   │   ├── AudioEngine.h
│   │   │   │   ├── AudioMixer.h
│   │   │   │   └── SpatialAudio.h
│   │   │   ├── AudioEngine.cpp
│   │   │   ├── AudioMixer.cpp
│   │   │   ├── SpatialAudio.cpp
│   │   │   └── jni_bridge.cpp
│   │   └── java/com/trashapp/oboe/
│   │       └── AudioEngine.java
│   └── CMakeLists.txt
│
├── skia-graphics/                # Skia graphics engine (C++)
│   ├── src/main/
│   │   ├── cpp/
│   │   │   ├── include/
│   │   │   │   ├── GraphicsEngine.h
│   │   │   │   ├── Renderer.h
│   │   │   │   ├── ShaderManager.h
│   │   │   │   ├── CardRenderer.h
│   │   │   │   └── ParticleEffect.h
│   │   │   ├── GraphicsEngine.cpp
│   │   │   ├── Renderer.cpp
│   │   │   ├── ShaderManager.cpp
│   │   │   ├── CardRenderer.cpp
│   │   │   ├── ParticleEffect.cpp
│   │   │   └── jni_bridge.cpp
│   │   └── java/com/trashapp/skia/
│   │       └── GraphicsEngine.java
│   └── CMakeLists.txt
│
└── gcms-core/                    # GCMS game logic
    ├── models/                   # Game models
    │   ├── Card.kt
    │   ├── Deck.kt
    │   └── GameState.kt
    ├── commands/                 # Command classes
    │   └── GCMSCommand.kt
    ├── events/                   # Event classes
    │   └── GCMSEvent.kt
    ├── handlers/                 # Command handlers
    │   ├── CardCommandHandler.kt
    │   └── MatchCommandHandler.kt
    └── GCMSController.kt
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34
- NDK r25c or later
- CMake 3.22.1 or later
- Python 3 (for Skia build)

### Setup
1. Clone the repository
2. Clone submodules (Oboe and Skia):
   ```bash
   git submodule update --init --recursive
   ```
3. Open in Android Studio
4. Sync Gradle
5. Build and run

### Build Commands
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install to device
./gradlew installDebug
```

## 🔧 Configuration

### Native Dependencies
The project automatically downloads and builds:
- **Oboe**: `https://github.com/google/oboe.git`
- **Skia**: `https://skia.googlesource.com/skia.git`

### Firebase Setup
1. Create a Firebase project at console.firebase.google.com
2. Add an Android app with package name `com.trashapp`
3. Download `google-services.json`
4. Place it in the project root

### GCMS Integration
The GCMS is fully connected to:
- **LibGDX**: Game loop and input processing
- **Oboe**: Audio feedback for game events
- **Skia**: Visual rendering of game state

## 🎨 Wild West Theme

### Custom Shaders
- **Wood Grain Effect**: Authentic wood texture for card backs
- **Vintage Effect**: Sepia tone with vignette for card faces
- **Particle Effects**: Gold coins, dust, fire sparks

### Card Design
- Custom Wild West suits: Sheriff Stars (♠), Horseshoes (♥), Cactus (♣), Gold Nuggets (♦)
- Vintage 1800s playing card style
- Weathered paper texture
- Western-themed colors

### Audio Design
- Low-latency sound effects
- Spatial audio positioning
- Western-themed music
- Dynamic sound mixing

## 🧪 Testing

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# Performance profiling
./gradlew assembleDebug
```

## 📊 Performance

### Targets
- **Frame Rate**: 60 FPS sustained
- **Audio Latency**: