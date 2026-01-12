#pragma once

#include <GLES3/gl3.h>
#include <vector>
#include <string>
#include <memory>

namespace trashapp {
namespace graphics {

struct Particle {
    float x, y, z;
    float vx, vy, vz;
    float life;
    float maxLife;
    float size;
    float r, g, b, a;
};

class ParticleEffect {
public:
    ParticleEffect();
    ~ParticleEffect();
    
    void initialize();
    void release();
    
    void spawn(const char* effectType, float x, float y);
    void update(float deltaTime);
    void render();
    
private:
    void createParticleGeometry();
    void updateParticle(Particle&amp; p, float deltaTime);
    
    std::vector<Particle> mParticles;
    
    // OpenGL objects
    GLuint mVertexArray = 0;
    GLuint mVertexBuffer = 0;
    GLuint mShaderProgram = 0;
    
    // Wild West particle types
    void spawnGoldCoin(float x, float y);
    void spawnDust(float x, float y);
    void spawnFireSpark(float x, float y);
    
    bool mInitialized = false;
};

} // namespace graphics
} // namespace trashapp