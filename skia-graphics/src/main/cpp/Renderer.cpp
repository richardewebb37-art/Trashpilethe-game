#include &quot;Renderer.h&quot;
#include &lt;android/log.h&gt;

#define TAG &quot;Renderer&quot;
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)

namespace trashapp {
namespace graphics {

Renderer::Renderer() : mWidth(0), mHeight(0), mMSAASamples(4) {
}

Renderer::~Renderer() {
    release();
}

bool Renderer::initialize(int width, int height, int msaaSamples) {
    if (mInitialized) {
        LOGI(&quot;Renderer already initialized&quot;);
        return true;
    }
    
    mWidth = width;
    mHeight = height;
    mMSAASamples = msaaSamples;
    
    if (!initializeEGL()) {
        LOGE(&quot;Failed to initialize EGL&quot;);
        return false;
    }
    
    if (!initializeGL()) {
        LOGE(&quot;Failed to initialize OpenGL&quot;);
        return false;
    }
    
    mInitialized = true;
    LOGI(&quot;Renderer initialized: %dx%d with %dx MSAA&quot;, width, height, msaaSamples);
    return true;
}

bool Renderer::initializeEGL() {
    mDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (mDisplay == EGL_NO_DISPLAY) {
        LOGE(&quot;Failed to get EGL display&quot;);
        return false;
    }
    
    if (!eglInitialize(mDisplay, nullptr, nullptr)) {
        LOGE(&quot;Failed to initialize EGL&quot;);
        return false;
    }
    
    // Choose EGL config
    EGLint configAttribs[] = {
        EGL_RENDERABLE_TYPE, EGL_OPENGL_ES3_BIT,
        EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
        EGL_RED_SIZE, 8,
        EGL_GREEN_SIZE, 8,
        EGL_BLUE_SIZE, 8,
        EGL_ALPHA_SIZE, 8,
        EGL_DEPTH_SIZE, 24,
        EGL_STENCIL_SIZE, 8,
        EGL_SAMPLE_BUFFERS, mMSAASamples > 1 ? 1 : 0,
        EGL_SAMPLES, mMSAASamples,
        EGL_NONE
    };
    
    EGLint numConfigs = 0;
    if (!eglChooseConfig(mDisplay, configAttribs, &amp;mConfig, 1, &amp;numConfigs) || numConfigs == 0) {
        LOGE(&quot;Failed to choose EGL config&quot;);
        return false;
    }
    
    // Create EGL context
    EGLint contextAttribs[] = {
        EGL_CONTEXT_CLIENT_VERSION, 3,
        EGL_NONE
    };
    
    mContext = eglCreateContext(mDisplay, mConfig, EGL_NO_CONTEXT, contextAttribs);
    if (mContext == EGL_NO_CONTEXT) {
        LOGE(&quot;Failed to create EGL context&quot;);
        return false;
    }
    
    LOGI(&quot;EGL initialized successfully&quot;);
    return true;
}

bool Renderer::initializeGL() {
    // Set viewport
    glViewport(0, 0, mWidth, mHeight);
    
    // Enable blending
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    
    // Enable depth testing
    glEnable(GL_DEPTH_TEST);
    glDepthFunc(GL_LEQUAL);
    
    // Set clear color
    glClearColor(0.24f, 0.15f, 0.14f, 1.0f); // Wild West background
    
    // Check for errors
    GLenum error = glGetError();
    if (error != GL_NO_ERROR) {
        LOGE(&quot;OpenGL error after initialization: 0x%x&quot;, error);
        return false;
    }
    
    LOGI(&quot;OpenGL initialized successfully&quot;);
    return true;
}

void Renderer::resize(int width, int height) {
    mWidth = width;
    mHeight = height;
    glViewport(0, 0, width, height);
    LOGI(&quot;Renderer resized to %dx%d&quot;, width, height);
}

void Renderer::release() {
    if (!mInitialized) return;
    
    cleanupEGL();
    mInitialized = false;
    LOGI(&quot;Renderer released&quot;);
}

void Renderer::cleanupEGL() {
    if (mContext != EGL_NO_CONTEXT) {
        eglDestroyContext(mDisplay, mContext);
        mContext = EGL_NO_CONTEXT;
    }
    
    if (mSurface != EGL_NO_SURFACE) {
        eglDestroySurface(mDisplay, mSurface);
        mSurface = EGL_NO_SURFACE;
    }
    
    if (mDisplay != EGL_NO_DISPLAY) {
        eglTerminate(mDisplay);
        mDisplay = EGL_NO_DISPLAY;
    }
}

void Renderer::beginFrame() {
    if (!mInitialized) return;
    
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
}

void Renderer::endFrame() {
    if (!mInitialized) return;
    // Flush OpenGL commands
    glFlush();
}

void Renderer::present() {
    if (!mInitialized || mDisplay == EGL_NO_DISPLAY || mSurface == EGL_NO_SURFACE) {
        return;
    }
    
    // Swap buffers
    eglSwapBuffers(mDisplay, mSurface);
}

void Renderer::clear(float r, float g, float b, float a) {
    glClearColor(r, g, b, a);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
}

} // namespace graphics
} // namespace trashapp