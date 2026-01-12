package com.trashapp.audio;

import android.content.Context;
import android.media.AudioManager;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Audio Asset Manager
 * Manages all audio assets and settings for the TRASH game
 */
public class AudioAssetManager {
    private static final String TAG = "AudioAssetManager";
    private static final String PREFS_NAME = "audio_settings";
    
    // Sound IDs
    public static final int SOUND_CARD_DEAL = 1;
    public static final int SOUND_CARD_FLIP = 2;
    public static final int SOUND_CARD_PLACE = 3;
    public static final int SOUND_BUTTON_CLICK = 4;
    public static final int SOUND_SHUFFLE = 5;
    public static final int SOUND_COIN = 6;
    public static final int SOUND_WIN = 7;
    public static final int SOUND_LOSE = 8;
    
    // Music tracks
    public static final int MUSIC_MAIN_MENU = 1;
    public static final int MUSIC_GAMEPLAY = 2;
    public static final int MUSIC_VICTORY = 3;
    
    // Volume settings (0.0f to 1.0f)
    private float masterVolume = 1.0f;
    private float musicVolume = 0.7f;
    private float sfxVolume = 0.8f;
    
    // Mute states
    private boolean masterMuted = false;
    private boolean musicMuted = false;
    private boolean sfxMuted = false;
    
    // Sound mappings
    private Map<Integer, String> soundMappings;
    private Map<Integer, String> musicMappings;
    
    private Context context;
    private AudioEngine audioEngine;
    private SharedPreferences prefs;
    
    public AudioAssetManager(Context context) {
        this.context = context;
        this.audioEngine = new AudioEngine();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.soundMappings = new HashMap<>();
        this.musicMappings = new HashMap<>();
        
        initializeSoundMappings();
        initializeMusicMappings();
        loadSettings();
    }
    
    /**
     * Initialize sound effect file mappings
     */
    private void initializeSoundMappings() {
        soundMappings.put(SOUND_CARD_DEAL, "sounds/card_deal.mp3");
        soundMappings.put(SOUND_CARD_FLIP, "sounds/card_flip.mp3");
        soundMappings.put(SOUND_CARD_PLACE, "sounds/card_place.mp3");
        soundMappings.put(SOUND_BUTTON_CLICK, "sounds/button_click.mp3");
        soundMappings.put(SOUND_SHUFFLE, "sounds/shuffle.mp3");
        soundMappings.put(SOUND_COIN, "sounds/coin.mp3");
        soundMappings.put(SOUND_WIN, "sounds/win.mp3");
        soundMappings.put(SOUND_LOSE, "sounds/lose.mp3");
    }
    
    /**
     * Initialize music track file mappings
     */
    private void initializeMusicMappings() {
        musicMappings.put(MUSIC_MAIN_MENU, "music/main_menu.mp3");
        musicMappings.put(MUSIC_GAMEPLAY, "music/gameplay.mp3");
        musicMappings.put(MUSIC_VICTORY, "music/victory.mp3");
    }
    
    /**
     * Initialize the audio engine
     */
    public void initialize() {
        audioEngine.initialize();
        loadAudioAssets();
        applyVolumeSettings();
        Log.d(TAG, "AudioAssetManager initialized");
    }
    
    /**
     * Load all audio assets into the native audio engine
     */
    private void loadAudioAssets() {
        // Load sound effects
        for (Map.Entry<Integer, String> entry : soundMappings.entrySet()) {
            int soundId = entry.getKey();
            String path = entry.getValue();
            int assetId = context.getResources().getIdentifier(
                path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.')),
                "raw",
                context.getPackageName()
            );
            
            if (assetId != 0) {
                audioEngine.loadSound(soundId, assetId);
                Log.d(TAG, "Loaded sound: " + soundId + " -> " + path);
            } else {
                Log.w(TAG, "Sound asset not found: " + path);
            }
        }
        
        // Load music tracks
        for (Map.Entry<Integer, String> entry : musicMappings.entrySet()) {
            int musicId = entry.getKey();
            String path = entry.getValue();
            int assetId = context.getResources().getIdentifier(
                path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.')),
                "raw",
                context.getPackageName()
            );
            
            if (assetId != 0) {
                audioEngine.loadMusic(musicId, assetId);
                Log.d(TAG, "Loaded music: " + musicId + " -> " + path);
            } else {
                Log.w(TAG, "Music asset not found: " + path);
            }
        }
    }
    
    /**
     * Play a sound effect
     * @param soundId The sound ID to play
     */
    public void playSound(int soundId) {
        if (!sfxMuted && !masterMuted) {
            float volume = masterVolume * sfxVolume;
            audioEngine.playSound(soundId, volume);
            Log.d(TAG, "Playing sound: " + soundId + " at volume " + volume);
        }
    }
    
    /**
     * Play a sound effect with 3D spatial positioning
     * @param soundId The sound ID to play
     * @param x X position (-1.0 to 1.0)
     * @param y Y position (-1.0 to 1.0)
     * @param z Z position (distance, 0.0 to 1.0)
     */
    public void playSound3D(int soundId, float x, float y, float z) {
        if (!sfxMuted && !masterMuted) {
            float volume = masterVolume * sfxVolume;
            audioEngine.playSound3D(soundId, volume, x, y, z);
            Log.d(TAG, "Playing 3D sound: " + soundId + " at (" + x + ", " + y + ", " + z + ")");
        }
    }
    
    /**
     * Play background music
     * @param musicId The music track ID to play
     * @param loop Whether to loop the music
     */
    public void playMusic(int musicId, boolean loop) {
        if (!musicMuted && !masterMuted) {
            float volume = masterVolume * musicVolume;
            audioEngine.playMusic(musicId, volume, loop);
            Log.d(TAG, "Playing music: " + musicId + " at volume " + volume + ", loop: " + loop);
        }
    }
    
    /**
     * Stop all music
     */
    public void stopMusic() {
        audioEngine.stopMusic();
        Log.d(TAG, "Music stopped");
    }
    
    /**
     * Pause all audio
     */
    public void pause() {
        audioEngine.pause();
        Log.d(TAG, "Audio paused");
    }
    
    /**
     * Resume all audio
     */
    public void resume() {
        audioEngine.resume();
        Log.d(TAG, "Audio resumed");
    }
    
    /**
     * Set master volume
     * @param volume Volume level (0.0f to 1.0f)
     */
    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0.0f, Math.min(1.0f, volume));
        applyVolumeSettings();
        saveSettings();
        Log.d(TAG, "Master volume set to: " + this.masterVolume);
    }
    
    /**
     * Set music volume
     * @param volume Volume level (0.0f to 1.0f)
     */
    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0.0f, Math.min(1.0f, volume));
        applyVolumeSettings();
        saveSettings();
        Log.d(TAG, "Music volume set to: " + this.musicVolume);
    }
    
    /**
     * Set SFX volume
     * @param volume Volume level (0.0f to 1.0f)
     */
    public void setSfxVolume(float volume) {
        this.sfxVolume = Math.max(0.0f, Math.min(1.0f, volume));
        applyVolumeSettings();
        saveSettings();
        Log.d(TAG, "SFX volume set to: " + this.sfxVolume);
    }
    
    /**
     * Apply all volume settings to the audio engine
     */
    private void applyVolumeSettings() {
        float finalMusicVolume = musicMuted || masterMuted ? 0.0f : masterVolume * musicVolume;
        float finalSfxVolume = sfxMuted || masterMuted ? 0.0f : masterVolume * sfxVolume;
        
        audioEngine.setMusicVolume(finalMusicVolume);
        audioEngine.setSfxVolume(finalSfxVolume);
        audioEngine.setMasterVolume(masterMuted ? 0.0f : masterVolume);
    }
    
    /**
     * Toggle master mute
     */
    public void toggleMasterMute() {
        masterMuted = !masterMuted;
        applyVolumeSettings();
        saveSettings();
        Log.d(TAG, "Master mute toggled to: " + masterMuted);
    }
    
    /**
     * Toggle music mute
     */
    public void toggleMusicMute() {
        musicMuted = !musicMuted;
        applyVolumeSettings();
        saveSettings();
        Log.d(TAG, "Music mute toggled to: " + musicMuted);
    }
    
    /**
     * Toggle SFX mute
     */
    public void toggleSfxMute() {
        sfxMuted = !sfxMuted;
        applyVolumeSettings();
        saveSettings();
        Log.d(TAG, "SFX mute toggled to: " + sfxMuted);
    }
    
    /**
     * Save audio settings to SharedPreferences
     */
    private void saveSettings() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("master_volume", masterVolume);
        editor.putFloat("music_volume", musicVolume);
        editor.putFloat("sfx_volume", sfxVolume);
        editor.putBoolean("master_muted", masterMuted);
        editor.putBoolean("music_muted", musicMuted);
        editor.putBoolean("sfx_muted", sfxMuted);
        editor.apply();
        Log.d(TAG, "Audio settings saved");
    }
    
    /**
     * Load audio settings from SharedPreferences
     */
    private void loadSettings() {
        masterVolume = prefs.getFloat("master_volume", 1.0f);
        musicVolume = prefs.getFloat("music_volume", 0.7f);
        sfxVolume = prefs.getFloat("sfx_volume", 0.8f);
        masterMuted = prefs.getBoolean("master_muted", false);
        musicMuted = prefs.getBoolean("music_muted", false);
        sfxMuted = prefs.getBoolean("sfx_muted", false);
        Log.d(TAG, "Audio settings loaded");
    }
    
    /**
     * Cleanup audio resources
     */
    public void cleanup() {
        audioEngine.cleanup();
        Log.d(TAG, "AudioAssetManager cleaned up");
    }
    
    // Getters for UI
    public float getMasterVolume() { return masterVolume; }
    public float getMusicVolume() { return musicVolume; }
    public float getSfxVolume() { return sfxVolume; }
    public boolean isMasterMuted() { return masterMuted; }
    public boolean isMusicMuted() { return musicMuted; }
    public boolean isSfxMuted() { return sfxMuted; }
    
    /**
     * Get the native audio engine instance
     */
    public AudioEngine getAudioEngine() {
        return audioEngine;
    }
}