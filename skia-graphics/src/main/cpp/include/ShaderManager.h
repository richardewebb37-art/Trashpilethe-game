#pragma once

#include <GLES3/gl3.h>
#include <string>
#include <unordered_map>
#include <memory>

namespace trashapp {
namespace graphics {

struct ShaderProgram {
    GLuint vertexShader = 0;
    GLuint fragmentShader = 0;
    GLuint program = 0;
    
    void use() const;
    void cleanup();
};

class ShaderManager {
public:
    ShaderManager();
    ~ShaderManager();
    
    void initialize();
    void release();
    
    bool loadShader(const char* name, const char* vertexSrc, const char* fragmentSrc);
    void useShader(const char* name);
    
    ShaderProgram* getShader(const char* name);
    
private:
    GLuint compileShader(GLenum type, const char* source);
    bool linkProgram(ShaderProgram&amp; shader);
    
    std::unordered_map<std::string, std::unique_ptr<ShaderProgram>> mShaders;
    bool mInitialized = false;
};

} // namespace graphics
} // namespace trashapp