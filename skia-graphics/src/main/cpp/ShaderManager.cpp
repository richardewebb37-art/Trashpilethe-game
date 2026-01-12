#include &quot;ShaderManager.h&quot;
#include &lt;android/log.h&gt;

#define TAG &quot;ShaderManager&quot;
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)

namespace trashapp {
namespace graphics {

void ShaderProgram::use() const {
    if (program != 0) {
        glUseProgram(program);
    }
}

void ShaderProgram::cleanup() {
    if (vertexShader != 0) {
        glDeleteShader(vertexShader);
        vertexShader = 0;
    }
    if (fragmentShader != 0) {
        glDeleteShader(fragmentShader);
        fragmentShader = 0;
    }
    if (program != 0) {
        glDeleteProgram(program);
        program = 0;
    }
}

ShaderManager::ShaderManager() {
}

ShaderManager::~ShaderManager() {
    release();
}

void ShaderManager::initialize() {
    mInitialized = true;
    LOGI(&quot;ShaderManager initialized&quot;);
}

void ShaderManager::release() {
    for (auto&amp; pair : mShaders) {
        pair.second->cleanup();
    }
    mShaders.clear();
    mInitialized = false;
    LOGI(&quot;ShaderManager released&quot;);
}

bool ShaderManager::loadShader(const char* name, const char* vertexSrc, const char* fragmentSrc) {
    auto shader = std::make_unique<ShaderProgram>();
    
    // Compile vertex shader
    shader->vertexShader = compileShader(GL_VERTEX_SHADER, vertexSrc);
    if (shader->vertexShader == 0) {
        LOGE(&quot;Failed to compile vertex shader: %s&quot;, name);
        return false;
    }
    
    // Compile fragment shader
    shader->fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentSrc);
    if (shader->fragmentShader == 0) {
        LOGE(&quot;Failed to compile fragment shader: %s&quot;, name);
        shader->cleanup();
        return false;
    }
    
    // Link program
    if (!linkProgram(*shader)) {
        LOGE(&quot;Failed to link shader program: %s&quot;, name);
        shader->cleanup();
        return false;
    }
    
    mShaders[name] = std::move(shader);
    LOGI(&quot;Shader loaded successfully: %s&quot;, name);
    return true;
}

void ShaderManager::useShader(const char* name) {
    auto it = mShaders.find(name);
    if (it != mShaders.end()) {
        it->second->use();
    }
}

ShaderProgram* ShaderManager::getShader(const char* name) {
    auto it = mShaders.find(name);
    if (it != mShaders.end()) {
        return it->second.get();
    }
    return nullptr;
}

GLuint ShaderManager::compileShader(GLenum type, const char* source) {
    GLuint shader = glCreateShader(type);
    glShaderSource(shader, 1, &amp;source, nullptr);
    glCompileShader(shader);
    
    GLint success = 0;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &amp;success);
    
    if (success == GL_FALSE) {
        GLint logLength = 0;
        glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &amp;logLength);
        
        if (logLength > 0) {
            char* log = new char[logLength];
            glGetShaderInfoLog(shader, logLength, nullptr, log);
            LOGE(&quot;Shader compilation error: %s&quot;, log);
            delete[] log;
        }
        
        glDeleteShader(shader);
        return 0;
    }
    
    return shader;
}

bool ShaderManager::linkProgram(ShaderProgram&amp; shader) {
    shader.program = glCreateProgram();
    glAttachShader(shader.program, shader.vertexShader);
    glAttachShader(shader.program, shader.fragmentShader);
    glLinkProgram(shader.program);
    
    GLint success = 0;
    glGetProgramiv(shader.program, GL_LINK_STATUS, &amp;success);
    
    if (success == GL_FALSE) {
        GLint logLength = 0;
        glGetProgramiv(shader.program, GL_INFO_LOG_LENGTH, &amp;logLength);
        
        if (logLength > 0) {
            char* log = new char[logLength];
            glGetProgramInfoLog(shader.program, logLength, nullptr, log);
            LOGE(&quot;Program link error: %s&quot;, log);
            delete[] log;
        }
        
        return false;
    }
    
    return true;
}

} // namespace graphics
} // namespace trashapp