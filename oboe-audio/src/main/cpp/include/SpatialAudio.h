#pragma once

#include <vector>
#include <map>
#include <cmath>

namespace trashapp {
namespace audio {

struct Vector3 {
    float x, y, z;
    
    Vector3(float x = 0, float y = 0, float z = 0) : x(x), y(y), z(z) {}
    
    float distanceTo(const Vector3&amp; other) const {
        float dx = x - other.x;
        float dy = y - other.y;
        float dz = z - other.z;
        return std::sqrt(dx*dx + dy*dy + dz*dz);
    }
};

struct Sound3D {
    int soundId;
    Vector3 position;
    float volume;
    float maxDistance;
    bool active;
};

class SpatialAudio {
public:
    SpatialAudio();
    ~SpatialAudio();
    
    void setListenerPosition(float x, float y, float z);
    void playSound3D(int soundId, float x, float y, float z, float volume, float maxDistance = 100.0f);
    void process(float* audioData, int32_t numFrames);
    void stopSound3D(int soundId);
    
private:
    Vector3 mListenerPosition;
    std::vector<Sound3D> mActiveSounds;
    
    float calculateAttenuation(const Vector3&amp; soundPos, float volume, float maxDistance);
    void applyHRTF(float* audioData, int32_t numFrames, float azimuth);
};

} // namespace audio
} // namespace trashapp