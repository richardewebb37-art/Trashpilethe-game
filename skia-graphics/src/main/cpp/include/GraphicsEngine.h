#pragma once

#include &lt;memory&gt;
#include &quot;Renderer.h&quot;
#include &quot;ShaderManager.h&quot;
#include &quot;CardRenderer.h&quot;
#include &quot;ParticleEffect.h&quot;

namespace trashapp {
namespace graphics {

struct GraphicsConfig {
    int width;
    int height;
    bool enableVSync;
    int msaaSamples;
};

class GraphicsEngine {
public:
    static GraphicsEngine&amp; getInstance();
    
    // Lifecycle
    void initialize(const GraphicsConfig&amp; config);
    void resize(int width, int height);
    void render();
    void release();
    
    // Rendering
    void clearScreen(float r, float g, float b, float a);
    void presentFrame();
    
    // Card rendering
    void renderCard(float x, float y, float width, float height, 
                    const char* suit, const char* rank, bool faceUp);
    void renderCardBack(float x, float y, float width, float height);
    
    // Effects
    void addParticleEffect(const char* effectType, float x, float y);
    void updateParticles(float deltaTime);
    
    // Shaders
    void loadShader(const char* name, const char* vertexSrc, const char* fragmentSrc);
    void useShader(const char* name);
    
    // Wild West theme
    void setWildWestTheme();
    void enableWoodGrainEffect(bool enable);
    void enableVintageEffect(bool enable);
    
private:
    GraphicsEngine();
    ~GraphicsEngine();
    GraphicsEngine(const GraphicsEngine&amp;) = delete;
    GraphicsEngine&amp; operator=(const GraphicsEngine&amp;) = delete;
    
    // Components
    std::unique_ptr&lt;Renderer&gt; mRenderer;
    std::unique_ptr&lt;ShaderManager&gt; mShaderManager;
    std::unique_ptr&lt;CardRenderer&gt; mCardRenderer;
    std::unique_ptr&lt;ParticleEffect&gt; mParticleEffect;
    
    // State
    GraphicsConfig mConfig;
    bool mInitialized = false;
    bool mWoodGrainEnabled = false;
    bool mVintageEffectEnabled = false;
};

} // namespace graphics
} // namespace trashapp