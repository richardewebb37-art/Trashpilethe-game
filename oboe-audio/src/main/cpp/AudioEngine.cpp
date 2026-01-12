#include "AudioEngine.h"
#include <android/log.h>

#define TAG "AudioEngine"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)

namespace trashapp {
namespace audio {

AudioEngine::AudioEngine() {
    mMixer = std::make_unique<AudioMixer>();
    mSpatialAudio = std::make_unique<SpatialAudio>();
    mSoundManager = std::make_unique<SoundManager>();
}

AudioEngine::~AudioEngine() {
    release();
}

AudioEngine& AudioEngine::getInstance() {
    static AudioEngine instance;
    return instance;
}

void AudioEngine::initialize() {
    if (mInitialized) {
        LOGI("AudioEngine already initialized");
        return;
    }
    
    auto result = openStream();
    if (result != oboe::Result::OK) {
        LOGE("Failed to open audio stream: %s", oboe::convertToText(result));
        return;
    }
    
    mInitialized = true;
    LOGI("AudioEngine initialized successfully");
}

oboe::Result AudioEngine::openStream() {
    oboe::AudioStreamBuilder builder;
    
    builder.setDirection(oboe::Direction::Output);
    builder.setPerformanceMode(oboe::PerformanceMode::LowLatency);
    builder.setSharingMode(oboe::SharingMode::Exclusive);
    builder.setFormat(oboe::AudioFormat::Float);
    builder.setChannelCount(oboe::ChannelCount::Stereo);
    builder.setSampleRate(48000);
    builder.setCallback(this);
    
    return builder.openStream(mAudioStream);
}

void AudioEngine::closeStream() {
    if (mAudioStream) {
        mAudioStream->close();
        mAudioStream.reset();
    }
}

void AudioEngine::start() {
    if (!mInitialized || mPlaying) {
        return;
    }
    
    auto result = mAudioStream->requestStart();
    if (result == oboe::Result::OK) {
        mPlaying = true;
        LOGI("AudioEngine started");
    } else {
        LOGE("Failed to start audio stream: %s", oboe::convertToText(result));
    }
}

void AudioEngine::stop() {
    if (!mPlaying) {
        return;
    }
    
    auto result = mAudioStream->requestStop();
    if (result == oboe::Result::OK) {
        mPlaying = false;
        LOGI("AudioEngine stopped");
    }
}

void AudioEngine::pause() {
    if (!mPlaying || mPaused) {
        return;
    }
    
    auto result = mAudioStream->requestPause();
    if (result == oboe::Result::OK) {
        mPaused = true;
        LOGI("AudioEngine paused");
    }
}

void AudioEngine::resume() {
    if (!mPlaying || !mPaused) {
        return;
    }
    
    auto result = mAudioStream->requestStart();
    if (result == oboe::Result::OK) {
        mPaused = false;
        LOGI("AudioEngine resumed");
    }
}

void AudioEngine::release() {
    stop();
    closeStream();
    mInitialized = false;
}

void AudioEngine::loadSound(const char* filename, int soundId) {
    std::lock_guard<std::mutex> lock(mMutex);
    mSoundManager->loadSound(filename, soundId);
    LOGI("Loading sound: %s (ID: %d)", filename, soundId);
}

void AudioEngine::loadMusic(const char* filename, int musicId) {
    std::lock_guard<std::mutex> lock(mMutex);
    // For now, load music as a sound
    mSoundManager->loadSound(filename, musicId);
    LOGI("Loading music: %s (ID: %d)", filename, musicId);
}

void AudioEngine::playSound(int soundId, float volume, float pan) {
    std::lock_guard<std::mutex> lock(mMutex);
    float adjustedVolume = volume * mSfxVolume * mMasterVolume;
    mSoundManager->playSound(soundId, adjustedVolume, pan);
    LOGI("Playing sound ID: %d, volume: %f", soundId, adjustedVolume);
}

void AudioEngine::stopSound(int soundId) {
    std::lock_guard<std::mutex> lock(mMutex);
    mSoundManager->stopSound(soundId);
}

void AudioEngine::setMasterVolume(float volume) {
    std::lock_guard<std::mutex> lock(mMutex);
    mMasterVolume = volume;
}

void AudioEngine::playMusic(int musicId, float volume, bool loop) {
    std::lock_guard<std::mutex> lock(mMutex);
    float adjustedVolume = volume * mMusicVolume * mMasterVolume;
    mSoundManager->playSound(musicId, adjustedVolume, 0.0f);
    LOGI("Playing music ID: %d, volume: %f, loop: %d", musicId, adjustedVolume, loop);
}

void AudioEngine::stopMusic() {
    std::lock_guard<std::mutex> lock(mMutex);
    mSoundManager->stopAllSounds();
}

void AudioEngine::setMusicVolume(float volume) {
    std::lock_guard<std::mutex> lock(mMutex);
    mMusicVolume = volume;
}

void AudioEngine::setSfxVolume(float volume) {
    std::lock_guard<std::mutex> lock(mMutex);
    mSfxVolume = volume;
}

void AudioEngine::setListenerPosition(float x, float y, float z) {
    std::lock_guard<std::mutex> lock(mMutex);
    mSpatialAudio->setListenerPosition(x, y, z);
}

void AudioEngine::playSound3D(int soundId, float x, float y, float z, float volume) {
    std::lock_guard<std::mutex> lock(mMutex);
    float adjustedVolume = volume * mSfxVolume * mMasterVolume;
    mSpatialAudio->playSound3D(soundId, x, y, z, adjustedVolume);
    LOGI("Playing 3D sound ID: %d at (%.2f, %.2f, %.2f), volume: %f", soundId, x, y, z, adjustedVolume);
}

void AudioEngine::enableReverb(bool enable) {
    std::lock_guard<std::mutex> lock(mMutex);
    // TODO: Enable/disable reverb effect
}

void AudioEngine::setReverbLevel(float level) {
    std::lock_guard<std::mutex> lock(mMutex);
    // TODO: Set reverb level
}

oboe::DataCallbackResult AudioEngine::onAudioReady(
    oboe::AudioStream* audioStream,
    void* audioData,
    int32_t numFrames
) {
    auto* floatData = static_cast<float*>(audioData);
    processAudio(floatData, numFrames);
    return oboe::DataCallbackResult::Continue;
}

void AudioEngine::processAudio(float* audioData, int32_t numFrames) {
    std::lock_guard<std::mutex> lock(mMutex);
    
    // Clear buffer
    memset(audioData, 0, sizeof(float) * numFrames * 2); // Stereo
    
    // Mix audio from sound manager
    mSoundManager->mixAudio(audioData, numFrames);
    
    // Apply spatial audio effects
    mSpatialAudio->process(audioData, numFrames);
}

} // namespace audio
} // namespace trashapp