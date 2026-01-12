#pragma once

#include <string>
#include <unordered_map>
#include <vector>
#include <memory>
#include <mutex>
#include <android/log.h>

namespace trashapp {
namespace audio {

// Sound data structure
struct SoundData {
    std::vector<float> samples;
    int sampleRate;
    int channels;
    int currentFrame;
    bool loop;
    float volume;
    float pan;
    float position[3]; // 3D position
};

class SoundManager {
public:
    SoundManager();
    ~SoundManager();
    
    // Load/unload sounds
    int loadSound(const std::string& filename, int soundId);
    void unloadSound(int soundId);
    void unloadAllSounds();
    
    // Playback control
    void playSound(int soundId, float volume = 1.0f, float pan = 0.0f);
    void playSound3D(int soundId, float x, float y, float z, float volume = 1.0f);
    void stopSound(int soundId);
    void stopAllSounds();
    
    // Audio processing
    void mixAudio(float* output, int numFrames);
    
    // Sound state
    bool isPlaying(int soundId) const;
    int getActiveSoundCount() const;
    
private:
    std::mutex mMutex;
    std::unordered_map<int, SoundData> mLoadedSounds;
    std::unordered_map<int, SoundData> mActiveSounds;
    
    void generateTone(SoundData& sound, float frequency, float duration);
    void generateNoise(SoundData& sound, float duration);
    void generateClick(SoundData& sound);
    void generateWhoosh(SoundData& sound);
};

} // namespace audio
} // namespace trashapp