#include &quot;GraphicsEngine.h&quot;
#include &lt;android/log.h&gt;

#define TAG &quot;GraphicsEngine&quot;
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)

namespace trashapp {
namespace graphics {

GraphicsEngine::GraphicsEngine() {
    mRenderer = std::make_unique&lt;Renderer&gt;();
    mShaderManager = std::make_unique&lt;ShaderManager&gt;();
    mCardRenderer = std::make_unique&lt;CardRenderer&gt;();
    mParticleEffect = std::make_unique&lt;ParticleEffect&gt;();
}

GraphicsEngine::~GraphicsEngine() {
    release();
}

GraphicsEngine&amp; GraphicsEngine::getInstance() {
    static GraphicsEngine instance;
    return instance;
}

void GraphicsEngine::initialize(const GraphicsConfig&amp; config) {
    if (mInitialized) {
        LOGI(&quot;GraphicsEngine already initialized&quot;);
        return;
    }
    
    mConfig = config;
    
    // Initialize renderer
    if (!mRenderer->initialize(config.width, config.height, config.msaaSamples)) {
        LOGE(&quot;Failed to initialize renderer&quot;);
        return;
    }
    
    // Initialize shader manager
    mShaderManager->initialize();
    
    // Initialize card renderer
    mCardRenderer->initialize();
    
    // Initialize particle effects
    mParticleEffect->initialize();
    
    // Load Wild West shaders
    setWildWestTheme();
    
    mInitialized = true;
    LOGI(&quot;GraphicsEngine initialized: %dx%d&quot;, config.width, config.height);
}

void GraphicsEngine::resize(int width, int height) {
    if (mRenderer) {
        mRenderer->resize(width, height);
    }
    mConfig.width = width;
    mConfig.height = height;
}

void GraphicsEngine::render() {
    if (!mInitialized) return;
    
    // Render current frame
    mRenderer->beginFrame();
    
    // Update and render particles
    mParticleEffect->render();
    
    mRenderer->endFrame();
    presentFrame();
}

void GraphicsEngine::release() {
    if (!mInitialized) return;
    
    mParticleEffect->release();
    mCardRenderer->release();
    mShaderManager->release();
    mRenderer->release();
    
    mInitialized = false;
    LOGI(&quot;GraphicsEngine released&quot;);
}

void GraphicsEngine::clearScreen(float r, float g, float b, float a) {
    if (mRenderer) {
        mRenderer->clear(r, g, b, a);
    }
}

void GraphicsEngine::presentFrame() {
    if (mRenderer) {
        mRenderer->present();
    }
}

void GraphicsEngine::renderCard(float x, float y, float width, float height,
                                const char* suit, const char* rank, bool faceUp) {
    if (faceUp) {
        mCardRenderer->renderFace(x, y, width, height, suit, rank, mVintageEffectEnabled);
    } else {
        mCardRenderer->renderBack(x, y, width, height, mWoodGrainEnabled);
    }
}

void GraphicsEngine::renderCardBack(float x, float y, float width, float height) {
    mCardRenderer->renderBack(x, y, width, height, mWoodGrainEnabled);
}

void GraphicsEngine::addParticleEffect(const char* effectType, float x, float y) {
    mParticleEffect->spawn(effectType, x, y);
}

void GraphicsEngine::updateParticles(float deltaTime) {
    mParticleEffect->update(deltaTime);
}

void GraphicsEngine::loadShader(const char* name, const char* vertexSrc, const char* fragmentSrc) {
    mShaderManager->loadShader(name, vertexSrc, fragmentSrc);
}

void GraphicsEngine::useShader(const char* name) {
    mShaderManager->useShader(name);
}

void GraphicsEngine::setWildWestTheme() {
    // Load Wild West themed shaders
    const char* woodVertexShader = R&quot;(
        attribute vec4 position;
        attribute vec2 texCoord;
        varying vec2 vTexCoord;
        uniform mat4 projection;
        
        void main() {
            gl_Position = projection * position;
            vTexCoord = texCoord;
        }
    )&quot;;
    
    const char* woodFragmentShader = R&quot;(
        precision mediump float;
        varying vec2 vTexCoord;
        uniform float time;
        uniform sampler2D texture;
        
        void main() {
            vec4 texColor = texture2D(texture, vTexCoord);
            
            // Add wood grain effect
            float grain = sin(vTexCoord.y * 50.0 + time * 0.5) * 0.05;
            grain += cos(vTexCoord.x * 30.0) * 0.03;
            
            vec4 finalColor = texColor + vec4(grain, grain * 0.8, grain * 0.6, 0.0);
            gl_FragColor = finalColor;
        }
    )&quot;;
    
    const char* vintageFragmentShader = R&quot;(
        precision mediump float;
        varying vec2 vTexCoord;
        uniform sampler2D texture;
        
        void main() {
            vec4 texColor = texture2D(texture, vTexCoord);
            
            // Sepia tone
            float gray = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));
            vec3 sepia = vec3(gray * 1.2, gray * 1.0, gray * 0.8);
            
            // Add vignette
            vec2 center = vec2(0.5, 0.5);
            float dist = distance(vTexCoord, center);
            float vignette = 1.0 - dist * 0.8;
            
            vec4 finalColor = vec4(sepia * vignette, texColor.a);
            gl_FragColor = finalColor;
        }
    )&quot;;
    
    mShaderManager->loadShader(&quot;wood_grain&quot;, woodVertexShader, woodFragmentShader);
    mShaderManager->loadShader(&quot;vintage&quot;, woodVertexShader, vintageFragmentShader);
}

void GraphicsEngine::enableWoodGrainEffect(bool enable) {
    mWoodGrainEnabled = enable;
}

void GraphicsEngine::enableVintageEffect(bool enable) {
    mVintageEffectEnabled = enable;
}

} // namespace graphics
} // namespace trashapp