package com.trashapp.skia;

/**
 * Java wrapper for Skia Graphics Engine
 * Provides JNI bridge to native C++ graphics engine
 */
public class GraphicsEngine {
    static {
        System.loadLibrary(&quot;trashgraphics&quot;);
    }
    
    private static GraphicsEngine instance;
    
    private GraphicsEngine() {}
    
    public static synchronized GraphicsEngine getInstance() {
        if (instance == null) {
            instance = new GraphicsEngine();
        }
        return instance;
    }
    
    // Lifecycle methods
    public native void nativeInitialize(int width, int height, int msaaSamples);
    public native void nativeResize(int width, int height);
    public native void nativeRender();
    public native void nativeClearScreen(float r, float g, float b, float a);
    
    // Card rendering
    public native void nativeRenderCard(float x, float y, float width, float height,
                                        String suit, String rank, boolean faceUp);
    public native void nativeRenderCardBack(float x, float y, float width, float height);
    
    // Particle effects
    public native void nativeAddParticleEffect(String effectType, float x, float y);
    public native void nativeUpdateParticles(float deltaTime);
    
    // Wild West theme
    public native void nativeSetWildWestTheme();
    public native void nativeEnableWoodGrainEffect(boolean enable);
    public native void nativeEnableVintageEffect(boolean enable);
    
    // Java wrapper methods for convenience
    public void initialize(int width, int height) {
        initialize(width, height, 4);
    }
    
    public void initialize(int width, int height, int msaaSamples) {
        nativeInitialize(width, height, msaaSamples);
    }
    
    public void resize(int width, int height) {
        nativeResize(width, height);
    }
    
    public void render() {
        nativeRender();
    }
    
    public void clearScreen(float r, float g, float b, float a) {
        nativeClearScreen(r, g, b, a);
    }
    
    public void renderCard(float x, float y, float width, float height,
                          String suit, String rank, boolean faceUp) {
        nativeRenderCard(x, y, width, height, suit, rank, faceUp);
    }
    
    public void renderCardBack(float x, float y, float width, float height) {
        nativeRenderCardBack(x, y, width, height);
    }
    
    public void addParticleEffect(String effectType, float x, float y) {
        nativeAddParticleEffect(effectType, x, y);
    }
    
    public void updateParticles(float deltaTime) {
        nativeUpdateParticles(deltaTime);
    }
    
    public void setWildWestTheme() {
        nativeSetWildWestTheme();
    }
    
    public void enableWoodGrainEffect(boolean enable) {
        nativeEnableWoodGrainEffect(enable);
    }
    
    public void enableVintageEffect(boolean enable) {
        nativeEnableVintageEffect(enable);
    }
}