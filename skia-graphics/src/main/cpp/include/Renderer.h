#pragma once

#include &lt;GLES3/gl3.h&gt;
#include &lt;EGL/egl.h&gt;
#include &lt;EGL/eglext.h&gt;
#include &lt;memory&gt;

namespace trashapp {
namespace graphics {

class Renderer {
public:
    Renderer();
    ~Renderer();
    
    bool initialize(int width, int height, int msaaSamples);
    void resize(int width, int height);
    void release();
    
    void beginFrame();
    void endFrame();
    void present();
    
    void clear(float r, float g, float b, float a);
    
private:
    bool initializeEGL();
    bool initializeGL();
    void cleanupEGL();
    
    // EGL
    EGLDisplay mDisplay = EGL_NO_DISPLAY;
    EGLContext mContext = EGL_NO_CONTEXT;
    EGLSurface mSurface = EGL_NO_SURFACE;
    EGLConfig mConfig = nullptr;
    
    // OpenGL state
    GLuint mFramebuffer = 0;
    GLuint mRenderbuffer = 0;
    int mWidth = 0;
    int mHeight = 0;
    int mMSAASamples = 4;
    
    bool mInitialized = false;
};

} // namespace graphics
} // namespace trashapp