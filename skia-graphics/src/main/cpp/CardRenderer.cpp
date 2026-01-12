#include &quot;CardRenderer.h&quot;
#include &lt;android/log.h&gt;
#include &lt;cstring&gt;

#define TAG &quot;CardRenderer&quot;
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)

namespace trashapp {
namespace graphics {

CardRenderer::CardRenderer() {
}

CardRenderer::~CardRenderer() {
    release();
}

void CardRenderer::initialize() {
    if (mInitialized) {
        LOGI(&quot;CardRenderer already initialized&quot;);
        return;
    }
    
    createCardGeometry();
    setupCardMaterials();
    
    mInitialized = true;
    LOGI(&quot;CardRenderer initialized&quot;);
}

void CardRenderer::release() {
    if (!mInitialized) return;
    
    if (mVertexArray != 0) {
        glDeleteVertexArrays(1, &amp;mVertexArray);
        mVertexArray = 0;
    }
    if (mVertexBuffer != 0) {
        glDeleteBuffers(1, &amp;mVertexBuffer);
        mVertexBuffer = 0;
    }
    if (mIndexBuffer != 0) {
        glDeleteBuffers(1, &amp;mIndexBuffer);
        mIndexBuffer = 0;
    }
    if (mTexture != 0) {
        glDeleteTextures(1, &amp;mTexture);
        mTexture = 0;
    }
    if (mShaderProgram != 0) {
        glDeleteProgram(mShaderProgram);
        mShaderProgram = 0;
    }
    
    mInitialized = false;
}

void CardRenderer::createCardGeometry() {
    // Card vertices (x, y, u, v)
    float vertices[] = {
        // Position    // UV
        0.0f, 1.0f,   0.0f, 1.0f,  // Top-left
        1.0f, 1.0f,   1.0f, 1.0f,  // Top-right
        0.0f, 0.0f,   0.0f, 0.0f,  // Bottom-left
        1.0f, 0.0f,   1.0f, 0.0f   // Bottom-right
    };
    
    // Card indices
    unsigned int indices[] = {
        0, 1, 2,  // First triangle
        1, 3, 2   // Second triangle
    };
    
    // Create VAO
    glGenVertexArrays(1, &amp;mVertexArray);
    glBindVertexArray(mVertexArray);
    
    // Create VBO
    glGenBuffers(1, &amp;mVertexBuffer);
    glBindBuffer(GL_ARRAY_BUFFER, mVertexBuffer);
    glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);
    
    // Create EBO
    glGenBuffers(1, &amp;mIndexBuffer);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mIndexBuffer);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices, GL_STATIC_DRAW);
    
    // Set vertex attributes
    // Position
    glVertexAttribPointer(0, 2, GL_FLOAT, GL_FALSE, 4 * sizeof(float), (void*)0);
    glEnableVertexAttribArray(0);
    
    // UV
    glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, 4 * sizeof(float), (void*)(2 * sizeof(float)));
    glEnableVertexAttribArray(1);
    
    glBindVertexArray(0);
}

void CardRenderer::setupCardMaterials() {
    // Load card texture (placeholder - will be replaced with real texture)
    glGenTextures(1, &amp;mTexture);
    glBindTexture(GL_TEXTURE_2D, mTexture);
    
    // Set texture parameters
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    
    // Create placeholder texture data
    unsigned char textureData[256 * 256 * 4];
    for (int i = 0; i < 256 * 256 * 4; i += 4) {
        textureData[i] = 245;     // R - Parchment color
        textureData[i + 1] = 222; // G
        textureData[i + 2] = 179; // B
        textureData[i + 3] = 255; // A
    }
    
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 256, 256, 0, GL_RGBA, GL_UNSIGNED_BYTE, textureData);
    glGenerateMipmap(GL_TEXTURE_2D);
    
    // Load card shader
    const char* vertexShaderSrc = R&quot;(
        #version 300 es
        layout(location = 0) in vec2 aPosition;
        layout(location = 1) in vec2 aTexCoord;
        
        uniform mat4 uProjection;
        uniform vec2 uPosition;
        uniform vec2 uScale;
        
        out vec2 vTexCoord;
        
        void main() {
            vec2 pos = aPosition * uScale + uPosition;
            gl_Position = uProjection * vec4(pos, 0.0, 1.0);
            vTexCoord = aTexCoord;
        }
    )&quot;;
    
    const char* fragmentShaderSrc = R&quot;(
        #version 300 es
        precision mediump float;
        
        in vec2 vTexCoord;
        uniform sampler2D uTexture;
        uniform vec4 uColor;
        uniform float uBorder;
        uniform vec3 uBorderColor;
        
        out vec4 FragColor;
        
        void main() {
            vec4 texColor = texture(uTexture, vTexCoord);
            
            // Add card border
            float borderSize = 0.02;
            float border = step(1.0 - borderSize, vTexCoord.x) + 
                          step(1.0 - borderSize, vTexCoord.y) +
                          step(borderSize, 1.0 - vTexCoord.x) +
                          step(borderSize, 1.0 - vTexCoord.y);
            
            vec3 finalColor = mix(texColor.rgb * uColor.rgb, uBorderColor, border * 0.3);
            FragColor = vec4(finalColor, texColor.a * uColor.a);
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

void CardRenderer::renderFace(float x, float y, float width, float height,
                               const char* suit, const char* rank, bool vintageEffect) {
    if (!mInitialized) return;
    
    glUseProgram(mShaderProgram);
    glBindVertexArray(mVertexArray);
    
    // Set uniforms
    GLint projLoc = glGetUniformLocation(mShaderProgram, &quot;uProjection&quot;);
    GLint posLoc = glGetUniformLocation(mShaderProgram, &quot;uPosition&quot;);
    GLint scaleLoc = glGetUniformLocation(mShaderProgram, &quot;uScale&quot;);
    GLint colorLoc = glGetUniformLocation(mShaderProgram, &quot;uColor&quot;);
    
    // Projection matrix (orthographic)
    float projMatrix[16] = {
        2.0f / 1920.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 2.0f / 1080.0f, 0.0f, 0.0f,
        0.0f, 0.0f, -1.0f, 0.0f,
        -1.0f, -1.0f, 0.0f, 1.0f
    };
    
    glUniformMatrix4fv(projLoc, 1, GL_FALSE, projMatrix);
    glUniform2f(posLoc, x, y);
    glUniform2f(scaleLoc, width, height);
    glUniform4f(colorLoc, 1.0f, 1.0f, 1.0f, 1.0f);
    
    // Draw card
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    
    glBindVertexArray(0);
}

void CardRenderer::renderBack(float x, float y, float width, float height, bool woodGrain) {
    if (!mInitialized) return;
    
    glUseProgram(mShaderProgram);
    glBindVertexArray(mVertexArray);
    
    // Set uniforms
    GLint projLoc = glGetUniformLocation(mShaderProgram, &quot;uProjection&quot;);
    GLint posLoc = glGetUniformLocation(mShaderProgram, &quot;uPosition&quot;);
    GLint scaleLoc = glGetUniformLocation(mShaderProgram, &quot;uScale&quot;);
    GLint colorLoc = glGetUniformLocation(mShaderProgram, &quot;uColor&quot;);
    GLint borderLoc = glGetUniformLocation(mShaderProgram, &quot;uBorder&quot;);
    GLint borderColorLoc = glGetUniformLocation(mShaderProgram, &quot;uBorderColor&quot;);
    
    // Projection matrix
    float projMatrix[16] = {
        2.0f / 1920.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 2.0f / 1080.0f, 0.0f, 0.0f,
        0.0f, 0.0f, -1.0f, 0.0f,
        -1.0f, -1.0f, 0.0f, 1.0f
    };
    
    glUniformMatrix4fv(projLoc, 1, GL_FALSE, projMatrix);
    glUniform2f(posLoc, x, y);
    glUniform2f(scaleLoc, width, height);
    
    // Dark red card back color
    glUniform4f(colorLoc, 0.545f, 0.0f, 0.0f, 1.0f);
    glUniform1f(borderLoc, 1.0f);
    glUniform3f(borderColorLoc, 0.4f, 0.4f, 0.4f);
    
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    
    glBindVertexArray(0);
}

} // namespace graphics
} // namespace trashapp