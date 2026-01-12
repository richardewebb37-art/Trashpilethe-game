#pragma once

#include <vector>
#include <map>
#include <memory>

namespace trashapp {
namespace audio {

struct SoundInstance {
    int soundId;
    float volume;
    float pan;
    bool active;
    int currentPosition;
};

class AudioMixer {
public:
    AudioMixer();
    ~AudioMixer();
    
    void mix(float* output, int32_t numFrames);
    void playSound(int soundId, float volume = 1.0f, float pan = 0.0f);
    void stopSound(int soundId);
    void stopAll();
    
private:
    std::vector<SoundInstance> mActiveSounds;
    std::map<int, std::vector<float>> mLoadedSounds;
    
    void mixSound(SoundInstance&amp; sound, float* output, int32_t numFrames);
    void applyPan(float* samples, int32_t numFrames, float pan);
};

} // namespace audio
} // namespace trashapp