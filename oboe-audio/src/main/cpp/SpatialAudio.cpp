#include &quot;SpatialAudio.h&quot;
#include <cmath>
#include <algorithm>

namespace trashapp {
namespace audio {

SpatialAudio::SpatialAudio() : mListenerPosition(0, 0, 0) {
}

SpatialAudio::~SpatialAudio() {
}

void SpatialAudio::setListenerPosition(float x, float y, float z) {
    mListenerPosition = Vector3(x, y, z);
}

void SpatialAudio::playSound3D(int soundId, float x, float y, float z, float volume, float maxDistance) {
    Sound3D sound;
    sound.soundId = soundId;
    sound.position = Vector3(x, y, z);
    sound.volume = volume;
    sound.maxDistance = maxDistance;
    sound.active = true;
    
    mActiveSounds.push_back(sound);
}

void SpatialAudio::process(float* audioData, int32_t numFrames) {
    // Process each 3D sound and apply spatial effects
    for (auto&amp; sound : mActiveSounds) {
        if (!sound.active) continue;
        
        float distance = mListenerPosition.distanceTo(sound.position);
        float attenuation = calculateAttenuation(sound.position, sound.volume, sound.maxDistance);
        
        if (attenuation > 0.01f) {
            // Calculate azimuth for HRTF
            float dx = sound.position.x - mListenerPosition.x;
            float dz = sound.position.z - mListenerPosition.z;
            float azimuth = std::atan2(dx, dz) * 180.0f / M_PI;
            
            // Apply spatial effects
            for (int i = 0; i < numFrames * 2; i++) {
                audioData[i] *= attenuation;
            }
            
            applyHRTF(audioData, numFrames, azimuth);
        } else {
            sound.active = false;
        }
    }
    
    // Remove inactive sounds
    mActiveSounds.erase(
        std::remove_if(mActiveSounds.begin(), mActiveSounds.end(),
            [](const Sound3D&amp; sound) { return !sound.active; }),
        mActiveSounds.end()
    );
}

void SpatialAudio::stopSound3D(int soundId) {
    mActiveSounds.erase(
        std::remove_if(mActiveSounds.begin(), mActiveSounds.end(),
            [soundId](const Sound3D&amp; sound) { return sound.soundId == soundId; }),
        mActiveSounds.end()
    );
}

float SpatialAudio::calculateAttenuation(const Vector3&amp; soundPos, float volume, float maxDistance) {
    float distance = mListenerPosition.distanceTo(soundPos);
    
    if (distance >= maxDistance) {
        return 0.0f;
    }
    
    // Inverse distance attenuation model
    float attenuation = volume / (1.0f + distance * 0.1f);
    
    return std::clamp(attenuation, 0.0f, 1.0f);
}

void SpatialAudio::applyHRTF(float* audioData, int32_t numFrames, float azimuth) {
    // Simple HRTF approximation using interaural time difference (ITD)
    // and interaural level difference (ILD)
    
    float itd = (azimuth / 180.0f) * 0.001f; // Max 1ms delay
    int delaySamples = static_cast<int>(itd * 48000.0f); // Assuming 48kHz
    
    // Apply level difference based on azimuth
    float levelDiff = (azimuth / 90.0f) * 0.5f; // Max 6dB difference
    levelDiff = std::clamp(levelDiff, -0.5f, 0.5f);
    
    float leftGain = 1.0f - (levelDiff > 0 ? levelDiff : 0);
    float rightGain = 1.0f + (levelDiff < 0 ? levelDiff : 0);
    
    for (int i = 0; i < numFrames; i++) {
        int index = i * 2;
        audioData[index] *= leftGain;
        audioData[index + 1] *= rightGain;
    }
}

} // namespace audio
} // namespace trashapp