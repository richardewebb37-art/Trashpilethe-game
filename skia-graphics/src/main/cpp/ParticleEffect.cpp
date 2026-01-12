#include &quot;ParticleEffect.h&quot;
#include &lt;android/log.h&gt;
#include &lt;cmath&gt;
#include &lt;random&gt;

#define TAG &quot;ParticleEffect&quot;
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)

namespace trashapp {
namespace graphics {

static std::random_device rd;
static std::mt19937 gen(rd());

ParticleEffect::ParticleEffect() {
}

ParticleEffect::~ParticleEffect() {
    release();
}

void ParticleEffect::initialize() {
    if (mInitialized) {
        LOGI(&quot;ParticleEffect already initialized&quot;);
        return;
    }
    
    createParticleGeometry();
    
    mInitialized = true;
    LOGI(&quot;ParticleEffect initialized&quot;);
}

void ParticleEffect::release() {
    if (!mInitialized) return;
    
    if (mVertexArray != 0) {
        glDeleteVertexArrays(1, &amp;mVertexArray);
        mVertexArray = 0;
    }
    if (mVertexBuffer != 0) {
        glDeleteBuffers(1, &amp;mVertexBuffer);
        mVertexBuffer = 0;
    }
    if (mShaderProgram != 0) {
        glDeleteProgram(mShaderProgram);
        mShaderProgram = 0;
    }
    
    mParticles.clear();
    mInitialized = false;
}

void ParticleEffect::createParticleGeometry() {
    // Simple quad for particle rendering
    float vertices[] = {
        -0.5f, -0.5f,
         0.5f, -0.5f,
        -0.5f,  0.5f,
         0.5f,  0.5f
    };
    
    glGenVertexArrays(1, &amp;mVertexArray);
    glBindVertexArray(mVertexArray);
    
    glGenBuffers(1, &amp;mVertexBuffer);
    glBindBuffer(GL_ARRAY_BUFFER, mVertexBuffer);
    glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);
    
    glVertexAttribPointer(0, 2, GL_FLOAT, GL_FALSE, 2 * sizeof(float), (void*)0);
    glEnableVertexAttribArray(0);
    
    glBindVertexArray(0);
    
    // Load particle shader
    const char* vertexShaderSrc = R&quot;(
        #version 300 es
        layout(location = 0) in vec2 aPosition;
        
        uniform mat4 uProjection;
        uniform vec2 uPosition;
        uniform float uSize;
        uniform float uRotation;
        
        out vec2 vTexCoord;
        
        void main() {
            float cosR = cos(uRotation);
            float sinR = sin(uRotation);
            vec2 rotatedPos = vec2(
                aPosition.x * cosR - aPosition.y * sinR,
                aPosition.x * sinR + aPosition.y * cosR
            );
            
            vec2 pos = rotatedPos * uSize + uPosition;
            gl_Position = uProjection * vec4(pos, 0.0, 1.0);
            gl_PointSize = uSize * 10.0;
        }
    )&quot;;
    
    const char* fragmentShaderSrc = R&quot;(
        #version 300 es
        precision mediump float;
        
        uniform vec4 uColor;
        uniform float uAlpha;
        
        out vec4 FragColor;
        
        void main() {
            // Circular particle
            vec2 coord = gl_PointCoord - vec2(0.5);
            float dist = length(coord);
            
            if (dist > 0.5) {
                discard;
            }
            
            // Soft edge
            float alpha = 1.0 - smoothstep(0.3, 0.5, dist);
            
            FragColor = vec4(uColor.rgb, uColor.a * alpha * uAlpha);
        }
    )&quot;;
    
    // Compile shaders
    GLuint vertexShader = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(vertexShader, 1, &amp;vertexShaderSrc, nullptr);
    glCompileShader(vertexShader);
    
    GLuint fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(fragmentShader, 1, &amp;fragmentShaderSrc, nullptr);
    glCompileShader(fragmentShader);
    
    // Create program
    mShaderProgram = glCreateProgram();
    glAttachShader(mShaderProgram, vertexShader);
    glAttachShader(mShaderProgram, fragmentShader);
    glLinkProgram(mShaderProgram);
    
    // Cleanup
    glDeleteShader(vertexShader);
    glDeleteShader(fragmentShader);
}

void ParticleEffect::spawn(const char* effectType, float x, float y) {
    if (strcmp(effectType, &quot;gold_coin&quot;) == 0) {
        spawnGoldCoin(x, y);
    } else if (strcmp(effectType, &quot;dust&quot;) == 0) {
        spawnDust(x, y);
    } else if (strcmp(effectType, &quot;fire_spark&quot;) == 0) {
        spawnFireSpark(x, y);
    }
}

void ParticleEffect::spawnGoldCoin(float x, float y) {
    std::uniform_real_distribution<float> distX(-100, 100);
    std::uniform_real_distribution<float> distY(-200, -50);
    std::uniform_real_distribution<float> distVel(-50, 50);
    
    Particle p;
    p.x = x;
    p.y = y;
    p.z = 0;
    p.vx = distVel(gen);
    p.vy = distY(gen);
    p.vz = 0;
    p.life = 2.0f;
    p.maxLife = 2.0f;
    p.size = 20.0f;
    p.r = 1.0f;  // Gold
    p.g = 0.84f;
    p.b = 0.0f;
    p.a = 1.0f;
    
    mParticles.push_back(p);
}

void ParticleEffect::spawnDust(float x, float y) {
    std::uniform_real_distribution<float> distX(-50, 50);
    std::uniform_real_distribution<float> distY(-30, 30);
    
    for (int i = 0; i < 5; i++) {
        Particle p;
        p.x = x + distX(gen);
        p.y = y + distY(gen);
        p.z = 0;
        p.vx = (gen() % 100 - 50) * 0.5f;
        p.vy = (gen() % 100 - 50) * 0.5f;
        p.vz = 0;
        p.life = 1.5f;
        p.maxLife = 1.5f;
        p.size = 5.0f + (gen() % 10);
        p.r = 0.62f;  // Dust brown
        p.g = 0.45f;
        p.b = 0.33f;
        p.a = 0.6f;
        
        mParticles.push_back(p);
    }
}

void ParticleEffect::spawnFireSpark(float x, float y) {
    std::uniform_real_distribution<float> distVel(-100, 100);
    
    Particle p;
    p.x = x;
    p.y = y;
    p.z = 0;
    p.vx = distVel(gen) * 0.3f;
    p.vy = -100 - (gen() % 50);
    p.vz = 0;
    p.life = 1.0f;
    p.maxLife = 1.0f;
    p.size = 8.0f + (gen() % 8);
    p.r = 1.0f;  // Orange fire
    p.g = 0.5f + (gen() % 50) * 0.01f;
    p.b = 0.0f;
    p.a = 1.0f;
    
    mParticles.push_back(p);
}

void ParticleEffect::update(float deltaTime) {
    for (auto&amp; p : mParticles) {
        updateParticle(p, deltaTime);
    }
    
    // Remove dead particles
    mParticles.erase(
        std::remove_if(mParticles.begin(), mParticles.end(),
            [](const Particle&amp; p) { return p.life <= 0; }),
        mParticles.end()
    );
}

void ParticleEffect::updateParticle(Particle&amp; p, float deltaTime) {
    // Physics
    p.x += p.vx * deltaTime;
    p.y += p.vy * deltaTime;
    p.z += p.vz * deltaTime;
    
    // Gravity
    p.vy -= 200 * deltaTime;
    
    // Life
    p.life -= deltaTime;
    
    // Fade out
    p.a = p.life / p.maxLife;
    
    // Shrink
    p.size *= 0.99f;
}

void ParticleEffect::render() {
    if (mParticles.empty()) return;
    
    glUseProgram(mShaderProgram);
    glBindVertexArray(mVertexArray);
    
    // Enable blending for particles
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    
    // Projection matrix
    float projMatrix[16] = {
        2.0f / 1920.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 2.0f / 1080.0f, 0.0f, 0.0f,
        0.0f, 0.0f, -1.0f, 0.0f,
        -1.0f, -1.0f, 0.0f, 1.0f
    };
    
    GLint projLoc = glGetUniformLocation(mShaderProgram, &quot;uProjection&quot;);
    GLint posLoc = glGetUniformLocation(mShaderProgram, &quot;uPosition&quot;);
    GLint sizeLoc = glGetUniformLocation(mShaderProgram, &quot;uSize&quot;);
    GLint colorLoc = glGetUniformLocation(mShaderProgram, &quot;uColor&quot;);
    GLint alphaLoc = glGetUniformLocation(mShaderProgram, &quot;uAlpha&quot;);
    
    glUniformMatrix4fv(projLoc, 1, GL_FALSE, projMatrix);
    
    // Render each particle
    for (const auto&amp; p : mParticles) {
        glUniform2f(posLoc, p.x, p.y);
        glUniform1f(sizeLoc, p.size);
        glUniform4f(colorLoc, p.r, p.g, p.b, p.a);
        glUniform1f(alphaLoc, p.a);
        
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    }
    
    glBindVertexArray(0);
}

} // namespace graphics
} // namespace trashapp