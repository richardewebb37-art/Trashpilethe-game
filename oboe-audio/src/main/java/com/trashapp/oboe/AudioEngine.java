package com.trashapp.oboe;

/**
 * Java wrapper for Oboe Audio Engine
 * Provides JNI bridge to native C++ audio engine
 */
public class AudioEngine {
    static {
        System.loadLibrary(&quot;trashaudio&quot;);
    }
    
    private static AudioEngine instance;
    
    private AudioEngine() {}
    
    public static synchronized AudioEngine getInstance() {
        if (instance == null) {
            instance = new AudioEngine();
        }
        return instance;
    }
    
    // Lifecycle methods
    public native void nativeInitialize();
    public native void nativeStart();
    public native void nativeStop();
    
    // Sound playback
    public native void nativePlaySound(int soundId, float volume, float pan);
    public native void nativePlaySound3D(int soundId, float x, float y, float z, float volume);
    public native void nativeSetListenerPosition(float x, float y, float z);
    
    // Volume control
    public native void nativeSetMasterVolume(float volume);
    public native void nativePlayMusic(String filename, boolean loop);
    public native void nativeStopMusic();
    public native void nativeSetMusicVolume(float volume);
    
    // Audio effects
    public native void nativeEnableReverb(boolean enable);
    public native void nativeSetReverbLevel(float level);
    
    // Java wrapper methods for convenience
    public void initialize() {
        nativeInitialize();
    }
    
    public void start() {
        nativeStart();
    }
    
    public void stop() {
        nativeStop();
    }
    
    public void playSound(int soundId) {
        playSound(soundId, 1.0f, 0.0f);
    }
    
    public void playSound(int soundId, float volume) {
        playSound(soundId, volume, 0.0f);
    }
    
    public void playSound(int soundId, float volume, float pan) {
        nativePlaySound(soundId, volume, pan);
    }
    
    public void playSound3D(int soundId, float x, float y, float z) {
        playSound3D(soundId, x, y, z, 1.0f);
    }
    
    public void playSound3D(int soundId, float x, float y, float z, float volume) {
        nativePlaySound3D(soundId, x, y, z, volume);
    }
    
    public void setListenerPosition(float x, float y, float z) {
        nativeSetListenerPosition(x, y, z);
    }
    
    public void setMasterVolume(float volume) {
        nativeSetMasterVolume(volume);
    }
    
    public void playMusic(String filename) {
        playMusic(filename, true);
    }
    
    public void playMusic(String filename, boolean loop) {
        nativePlayMusic(filename, loop);
    }
    
    public void stopMusic() {
        nativeStopMusic();
    }
    
    public void setMusicVolume(float volume) {
        nativeSetMusicVolume(volume);
    }
    
    public void enableReverb(boolean enable) {
        nativeEnableReverb(enable);
    }
    
    public void setReverbLevel(float level) {
        nativeSetReverbLevel(level);
    }
}