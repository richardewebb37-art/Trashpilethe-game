#include &lt;jni.h&gt;
#include &lt;android/log.h&gt;
#include &quot;GraphicsEngine.h&quot;

#define TAG &quot;GraphicsJNI&quot;
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)

extern &quot;C&quot; {

JNIEXPORT void JNICALL
Java_com_trashapp_skia_GraphicsEngine_nativeInitialize(
    JNIEnv* env,
    jobject thiz,
    jint width,
    jint height,
    jint msaaSamples
) {
    try {
        trashapp::graphics::GraphicsConfig config;
        config.width = width;
        config.height = height;
        config.enableVSync = true;
        config.msaaSamples = msaaSamples;
        
        trashapp::graphics::GraphicsEngine::getInstance().initialize(config);
    } catch (const std::exception&amp; e) {
        LOGE(&quot;Exception in nativeInitialize: %s&quot;, e.what());
    }
}

JNIEXPORT void JNICALL
Java_com_trashapp_skia_GraphicsEngine_nativeResize(
    JNIEnv* env,
    jobject thiz,
    jint width,
    jint height
) {
    try {
        trashapp::graphics::GraphicsEngine::getInstance().resize(width, height);
    } catch (const std::exception&amp; e) {
        LOGE(&quot;Exception in nativeResize: %s&quot;, e.what());
    }
}

JNIEXPORT void JNICALL
Java_com_trashapp_skia_GraphicsEngine_nativeRender(
    JNIEnv* env,
    jobject thiz
) {
    try {
        trashapp::graphics::GraphicsEngine::getInstance().render();
    } catch (const std::exception&amp; e) {
        LOGE(&quot;Exception in nativeRender: %s&quot;, e.what());
    }
}

JNIEXPORT void JNICALL
Java_com_trashapp_skia_GraphicsEngine_nativeClearScreen(
    JNIEnv* env,
    jobject thiz,
    jfloat r,
    jfloat g,
    jfloat b,
    jfloat a
) {
    try {
        trashapp::graphics::GraphicsEngine::getInstance().clearScreen(r, g, b, a);
    } catch (const std::exception&amp; e) {
        LOGE(&quot;Exception in nativeClearScreen: %s&quot;, e.what());
    }
}

JNIEXPORT void JNICALL
Java_com_trashapp_skia_GraphicsEngine_nativeRenderCard(
    JNIEnv* env,
    jobject thiz,
    jfloat x,
    jfloat y,
    jfloat width,
    jfloat height,
    jstring suit,
    jstring rank,
    jboolean faceUp
) {
    try {
        const char* suitChars = env-&gt;GetStringUTFChars(suit, nullptr);
        const char* rankChars = env-&gt;GetStringUTFChars(rank, nullptr);
        
        trashapp::graphics::GraphicsEngine::getInstance().renderCard(
            x, y, width, height, suitChars, rankChars, faceUp
        );
        
        env-&gt;ReleaseStringUTFChars(suit, suitChars);
        env-&gt;ReleaseStringUTFChars(rank, rankChars);
    } catch (const std::exception&amp; e) {
        LOGE(&quot;Exception in nativeRenderCard: %s&quot;, e.what());
    }
}

JNIEXPORT void JNICALL
Java_com_trashapp_skia_GraphicsEngine_nativeRenderCardBack(
    JNIEnv* env,
    jobject thiz,
    jfloat x,
    jfloat y,
    jfloat width,
    jfloat height
) {
    try {
        trashapp::graphics::GraphicsEngine::getInstance().renderCardBack(x, y, width, height);
    } catch (const std::exception&amp; e) {
        LOGE(&quot;Exception in nativeRenderCardBack: %s&quot;, e.what());
    }
}

JNIEXPORT void JNICALL
Java_com_trashapp_skia_GraphicsEngine_nativeAddParticleEffect(
    JNIEnv* env,
    jobject thiz,
    jstring effectType,
    jfloat x,
    jfloat y
) {
    try {
        const char* effectTypeChars = env-&gt;GetStringUTFChars(effectType, nullptr);
        trashapp::graphics::GraphicsEngine::getInstance().addParticleEffect(effectTypeChars, x, y);
        env-&gt;ReleaseStringUTFChars(effectType, effectTypeChars);
    } catch (const std::exception&amp; e) {
        LOGE(&quot;Exception in nativeAddParticleEffect: %s&quot;, e.what());
    }
}

JNIEXPORT void JNICALL
Java_com_trashapp_skia_GraphicsEngine_nativeUpdateParticles(
    JNIEnv* env,
    jobject thiz,
    jfloat deltaTime
) {
    try {
        trashapp::graphics::GraphicsEngine::getInstance().updateParticles(deltaTime);
    } catch (const std::exception&amp; e) {
        LOGE(&quot;Exception in nativeUpdateParticles: %s&quot;, e.what());
    }
}

JNIEXPORT void JNICALL
Java_com_trashapp_skia_GraphicsEngine_nativeSetWildWestTheme(
    JNIEnv* env,
    jobject thiz
) {
    try {
        trashapp::graphics::GraphicsEngine::getInstance().setWildWestTheme();
    } catch (const std::exception&amp; e) {
        LOGE(&quot;Exception in nativeSetWildWestTheme: %s&quot;, e.what());
    }
}

JNIEXPORT void JNICALL
Java_com_trashapp_skia_GraphicsEngine_nativeEnableWoodGrainEffect(
    JNIEnv* env,
    jobject thiz,
    jboolean enable
) {
    try {
        trashapp::graphics::GraphicsEngine::getInstance().enableWoodGrainEffect(enable);
    } catch (const std::exception&amp; e) {
        LOGE(&quot;Exception in nativeEnableWoodGrainEffect: %s&quot;, e.what());
    }
}

JNIEXPORT void JNICALL
Java_com_trashapp_skia_GraphicsEngine_nativeEnableVintageEffect(
    JNIEnv* env,
    jobject thiz,
    jboolean enable
) {
    try {
        trashapp::graphics::GraphicsEngine::getInstance().enableVintageEffect(enable);
    } catch (const std::exception&amp; e) {
        LOGE(&quot;Exception in nativeEnableVintageEffect: %s&quot;, e.what());
    }
}

} // extern &quot;C&quot;