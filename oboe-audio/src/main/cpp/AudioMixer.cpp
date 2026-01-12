#include &quot;AudioMixer.h&quot;
#include <cstring>
#include <cmath>
#include <algorithm>

namespace trashapp {
namespace audio {

AudioMixer::AudioMixer() {
}

AudioMixer::~AudioMixer() {
    stopAll();
}

void AudioMixer::mix(float* output, int32_t numFrames) {
    // Clear output buffer
    memset(output, 0, sizeof(float) * numFrames * 2); // Stereo
    
    // Mix all active sounds
    for (auto&amp; sound : mActiveSounds) {
        if (sound.active) {
            mixSound(sound, output, numFrames);
        }
    }
}

void AudioMixer::playSound(int soundId, float volume, float pan) {
    SoundInstance sound;
    sound.soundId = soundId;
    sound.volume = volume;
    sound.pan = pan;
    sound.active = true;
    sound.currentPosition = 0;
    
    mActiveSounds.push_back(sound);
}

void AudioMixer::stopSound(int soundId) {
    mActiveSounds.erase(
        std::remove_if(mActiveSounds.begin(), mActiveSounds.end(),
            [soundId](const SoundInstance&amp; sound) {
                return sound.soundId == soundId;
            }),
        mActiveSounds.end()
    );
}

void AudioMixer::stopAll() {
    mActiveSounds.clear();
}

void AudioMixer::mixSound(SoundInstance&amp; sound, float* output, int32_t numFrames) {
    auto it = mLoadedSounds.find(sound.soundId);
    if (it == mLoadedSounds.end()) {
        sound.active = false;
        return;
    }
    
    const auto&amp; soundData = it->second;
    int samplesToMix = std::min(
        static_cast<int>(soundData.size()) - sound.currentPosition,
        numFrames
    );
    
    if (samplesToMix <= 0) {
        sound.active = false;
        return;
    }
    
    // Mix stereo samples
    for (int i = 0; i < samplesToMix; i++) {
        int sampleIndex = sound.currentPosition + i;
        float left = soundData[sampleIndex * 2];
        float right = soundData[sampleIndex * 2 + 1];
        
        // Apply volume and pan
        float volume = sound.volume;
        float pan = sound.pan;
        
        // Stereo panning: -1 (left) to 1 (right)
        float leftGain = volume * (pan < 0 ? 1.0f : 1.0f - pan);
        float rightGain = volume * (pan > 0 ? 1.0f : 1.0f + pan);
        
        // Mix into output (simple additive mixing with clipping protection)
        int outIndex = i * 2;
        output[outIndex] += left * leftGain;
        output[outIndex + 1] += right * rightGain;
        
        // Soft clipping
        output[outIndex] = std::clamp(output[outIndex], -1.0f, 1.0f);
        output[outIndex + 1] = std::clamp(output[outIndex + 1], -1.0f, 1.0f);
    }
    
    sound.currentPosition += samplesToMix;
}

void AudioMixer::applyPan(float* samples, int32_t numFrames, float pan) {
    float leftGain = pan < 0 ? 1.0f : 1.0f - pan;
    float rightGain = pan > 0 ? 1.0f : 1.0f + pan;
    
    for (int i = 0; i < numFrames; i++) {
        int index = i * 2;
        samples[index] *= leftGain;
        samples[index + 1] *= rightGain;
    }
}

} // namespace audio
} // namespace trashapp