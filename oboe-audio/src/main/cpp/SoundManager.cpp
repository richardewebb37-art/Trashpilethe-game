#include "SoundManager.h"
#include <cmath>
#include <random>
#include <algorithm>

#define LOG_TAG "SoundManager"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

namespace trashapp {
namespace audio {

const int SAMPLE_RATE = 48000;
const int CHANNELS = 2;

SoundManager::SoundManager() {
    LOGI("SoundManager created");
}

SoundManager::~SoundManager() {
    unloadAllSounds();
}

int SoundManager::loadSound(const std::string& filename, int soundId) {
    std::lock_guard<std::mutex> lock(mMutex);
    
    // Check if sound is already loaded
    if (mLoadedSounds.find(soundId) != mLoadedSounds.end()) {
        LOGI("Sound %d already loaded", soundId);
        return soundId;
    }
    
    SoundData sound;
    sound.sampleRate = SAMPLE_RATE;
    sound.channels = CHANNELS;
    sound.currentFrame = 0;
    sound.loop = false;
    sound.volume = 1.0f;
    sound.pan = 0.0f;
    sound.position[0] = 0.0f;
    sound.position[1] = 0.0f;
    sound.position[2] = 0.0f;
    
    // Generate procedural sounds based on soundId
    // These will be replaced with actual audio files later
    switch(soundId) {
        case 1: // Card deal
            generateClick(sound);
            break;
        case 2: // Card flip
            generateWhoosh(sound);
            break;
        case 3: // Card place
            generateClick(sound);
            break;
        case 4: // Button click
            generateClick(sound);
            break;
        case 5: // Shuffle
            generateNoise(sound, 0.3f);
            break;
        case 6: // Coin
            generateTone(sound, 1200.0f, 0.1f);
            break;
        case 7: // Win
            generateTone(sound, 880.0f, 0.5f);
            break;
        case 8: // Lose
            generateTone(sound, 440.0f, 0.5f);
            break;
        default:
            generateClick(sound);
            break;
    }
    
    mLoadedSounds[soundId] = sound;
    LOGI("Loaded sound: %s (ID: %d, frames: %zu)", filename.c_str(), soundId, sound.samples.size());
    
    return soundId;
}

void SoundManager::unloadSound(int soundId) {
    std::lock_guard<std::mutex> lock(mMutex);
    
    mLoadedSounds.erase(soundId);
    mActiveSounds.erase(soundId);
    LOGI("Unloaded sound ID: %d", soundId);
}

void SoundManager::unloadAllSounds() {
    std::lock_guard<std::mutex> lock(mMutex);
    
    mLoadedSounds.clear();
    mActiveSounds.clear();
    LOGI("Unloaded all sounds");
}

void SoundManager::playSound(int soundId, float volume, float pan) {
    std::lock_guard<std::mutex> lock(mMutex);
    
    auto it = mLoadedSounds.find(soundId);
    if (it == mLoadedSounds.end()) {
        LOGE("Sound %d not loaded", soundId);
        return;
    }
    
    // Create a copy of the sound for playback
    SoundData playbackSound = it->second;
    playbackSound.currentFrame = 0;
    playbackSound.volume = volume;
    playbackSound.pan = pan;
    
    mActiveSounds[soundId] = playbackSound;
    LOGI("Playing sound ID: %d, volume: %f, pan: %f", soundId, volume, pan);
}

void SoundManager::playSound3D(int soundId, float x, float y, float z, float volume) {
    std::lock_guard<std::mutex> lock(mMutex);
    
    auto it = mLoadedSounds.find(soundId);
    if (it == mLoadedSounds.end()) {
        LOGE("Sound %d not loaded", soundId);
        return;
    }
    
    // Create a copy of the sound for 3D playback
    SoundData playbackSound = it->second;
    playbackSound.currentFrame = 0;
    playbackSound.volume = volume;
    playbackSound.position[0] = x;
    playbackSound.position[1] = y;
    playbackSound.position[2] = z;
    
    // Calculate pan based on 3D position
    float distance = sqrt(x*x + y*y + z*z);
    float pan = std::max(-1.0f, std::min(1.0f, x / (distance + 0.1f)));
    playbackSound.pan = pan;
    
    mActiveSounds[soundId] = playbackSound;
    LOGI("Playing 3D sound ID: %d at (%.2f, %.2f, %.2f), volume: %f", soundId, x, y, z, volume);
}

void SoundManager::stopSound(int soundId) {
    std::lock_guard<std::mutex> lock(mMutex);
    mActiveSounds.erase(soundId);
    LOGI("Stopped sound ID: %d", soundId);
}

void SoundManager::stopAllSounds() {
    std::lock_guard<std::mutex> lock(mMutex);
    mActiveSounds.clear();
    LOGI("Stopped all sounds");
}

void SoundManager::mixAudio(float* output, int numFrames) {
    std::lock_guard<std::mutex> lock(mMutex);
    
    // Clear output buffer
    std::fill(output, output + numFrames * CHANNELS, 0.0f);
    
    // Mix all active sounds
    for (auto& pair : mActiveSounds) {
        SoundData& sound = pair.second;
        
        if (sound.currentFrame >= (int)sound.samples.size()) {
            continue; // Sound has finished
        }
        
        int framesToMix = std::min(numFrames, (int)sound.samples.size() - sound.currentFrame);
        
        for (int i = 0; i < framesToMix; i++) {
            int sampleIndex = sound.currentFrame + i;
            float sample = sound.samples[sampleIndex] * sound.volume;
            
            // Apply stereo panning
            float leftPan = 1.0f;
            float rightPan = 1.0f;
            
            if (sound.pan < 0.0f) {
                leftPan = 1.0f + sound.pan;
                rightPan = 1.0f;
            } else if (sound.pan > 0.0f) {
                leftPan = 1.0f;
                rightPan = 1.0f - sound.pan;
            }
            
            // Apply 3D distance attenuation
            float distance = sqrt(sound.position[0]*sound.position[0] + 
                                  sound.position[1]*sound.position[1] + 
                                  sound.position[2]*sound.position[2]);
            float attenuation = 1.0f / (1.0f + distance * 2.0f);
            sample *= attenuation;
            
            // Mix to stereo output
            int outputIndex = i * CHANNELS;
            output[outputIndex] += sample * leftPan;
            output[outputIndex + 1] += sample * rightPan;
        }
        
        sound.currentFrame += framesToMix;
        
        // Remove finished sounds
        if (sound.currentFrame >= (int)sound.samples.size()) {
            if (!sound.loop) {
                mActiveSounds.erase(pair.first);
            } else {
                sound.currentFrame = 0;
            }
        }
    }
}

bool SoundManager::isPlaying(int soundId) const {
    return mActiveSounds.find(soundId) != mActiveSounds.end();
}

int SoundManager::getActiveSoundCount() const {
    return mActiveSounds.size();
}

// Sound generation functions
void SoundManager::generateTone(SoundData& sound, float frequency, float duration) {
    int numFrames = (int)(SAMPLE_RATE * duration);
    sound.samples.resize(numFrames * CHANNELS);
    
    for (int i = 0; i < numFrames; i++) {
        float t = (float)i / SAMPLE_RATE;
        float sample = sin(2.0f * M_PI * frequency * t);
        
        // Apply envelope
        float envelope = 1.0f;
        if (i < numFrames * 0.1f) {
            envelope = (float)i / (numFrames * 0.1f); // Attack
        } else if (i > numFrames * 0.7f) {
            envelope = 1.0f - ((float)(i - numFrames * 0.7f) / (numFrames * 0.3f)); // Decay
        }
        sample *= envelope;
        
        sound.samples[i * CHANNELS] = sample;
        sound.samples[i * CHANNELS + 1] = sample;
    }
}

void SoundManager::generateNoise(SoundData& sound, float duration) {
    int numFrames = (int)(SAMPLE_RATE * duration);
    sound.samples.resize(numFrames * CHANNELS);
    
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_real_distribution<float> dist(-1.0f, 1.0f);
    
    for (int i = 0; i < numFrames; i++) {
        float t = (float)i / SAMPLE_RATE;
        float envelope = 1.0f;
        
        if (i < numFrames * 0.1f) {
            envelope = (float)i / (numFrames * 0.1f);
        } else if (i > numFrames * 0.8f) {
            envelope = 1.0f - ((float)(i - numFrames * 0.8f) / (numFrames * 0.2f));
        }
        
        float sample = dist(gen) * envelope * 0.5f;
        sound.samples[i * CHANNELS] = sample;
        sound.samples[i * CHANNELS + 1] = sample;
    }
}

void SoundManager::generateClick(SoundData& sound) {
    int numFrames = (int)(SAMPLE_RATE * 0.05f); // 50ms
    sound.samples.resize(numFrames * CHANNELS);
    
    for (int i = 0; i < numFrames; i++) {
        float t = (float)i / SAMPLE_RATE;
        float envelope = exp(-t * 50.0f); // Fast decay
        float sample = envelope * 0.7f;
        sound.samples[i * CHANNELS] = sample;
        sound.samples[i * CHANNELS + 1] = sample;
    }
}

void SoundManager::generateWhoosh(SoundData& sound) {
    int numFrames = (int)(SAMPLE_RATE * 0.15f); // 150ms
    sound.samples.resize(numFrames * CHANNELS);
    
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_real_distribution<float> dist(-0.5f, 0.5f);
    
    for (int i = 0; i < numFrames; i++) {
        float t = (float)i / SAMPLE_RATE;
        float envelope = sin(M_PI * t / 0.15f) * 0.5f;
        float sample = dist(gen) * envelope;
        sound.samples[i * CHANNELS] = sample;
        sound.samples[i * CHANNELS + 1] = sample;
    }
}

} // namespace audio
} // namespace trashapp