#pragma once

#include <GLES3/gl3.h>
#include <string>
#include <memory>

namespace trashapp {
namespace graphics {

class CardRenderer {
public:
    CardRenderer();
    ~CardRenderer();
    
    void initialize();
    void release();
    
    void renderFace(float x, float y, float width, float height, 
                   const char* suit, const char* rank, bool vintageEffect);
    void renderBack(float x, float y, float width, float height, bool woodGrain);
    
private:
    void createCardGeometry();
    void setupCardMaterials();
    
    // OpenGL objects
    GLuint mVertexArray = 0;
    GLuint mVertexBuffer = 0;
    GLuint mIndexBuffer = 0;
    GLuint mTexture = 0;
    GLuint mShaderProgram = 0;
    
    // Card geometry
    static const int CARD_VERTICES = 4;
    static const int CARD_INDICES = 6;
    
    bool mInitialized = false;
};

} // namespace graphics
} // namespace trashapp