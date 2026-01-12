#include &lt;jni.h&gt;
#include &lt;android/log.h&gt;
#include &quot;AudioEngine.h&quot;

#define TAG &quot;AudioJNI&quot;
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)

extern &quot;C&quot; {

JNIEXPORT void JNICALL
Java_com_trashapp_oboe_AudioEngine_nativeInitialize(
    JNIEnv* env,
    jobject thiz
) {
    try {
        trashapp::audio::AudioEngine::getInstance().initialize();
    } catch (const std::exception&amp; e) {
        LOGE(&quot;Exception in nativeInitialize: %s&quot;, e.what());
    }
}

JNIEXPORT void JNICALL
Java_com_trashapp_oboe_AudioEngine_nativeStart(
    JNIEnv* env,
    jobject thiz
) {
    try {
        trashapp::audio::AudioEngine::getInstance().start();
    } catch (const std::exception&amp; e) {
        LOGE(&quot;Exception in nativeStart: %s&quot;, e.what());
    }
}

JNIEXPORT void JNICALL
Java_com_trashapp_oboe_AudioEngine_nativeStop(
    JNIEnv* env,
    jobject thiz
) {
    try {
        trashapp::audio::AudioEngine::getInstance().stop();
    } catch (const std::exception&amp; e) {
        LOGE(&quot;Exception in nativeStop: %s&quot;, e.what());
    }
}

JNIEXPORT void JNICALL
Java_com_trashapp_oboe_AudioEngine_nativePlaySound(
    JNIEnv* env,
    jobject thiz,
    jint soundId,
    jfloat volume,
    jfloat pan
) {
    try {
        trashapp::audio::AudioEngine::getInstance().playSound(soundId, volume, pan);
    } catch (const std::exception&amp; e) {
        LOGE(&quot;Exception in nativePlaySound: %s&quot;, e.what());
    }
}

JNIEXPORT void JNICALL
Java_com_trashapp_oboe_AudioEngine_nativePlaySound3D(
    JNIEnv* env,
    jobject thiz,
    jint soundId,
    jfloat x,
    jfloat y,
    jfloat z,
    jfloat volume
) {
    try {
        trashapp::audio::AudioEngine::getInstance().playSound3D(soundId, x, y, z, volume);
    } catch (const std::exception&amp; e) {
        LOGE(&quot;Exception in nativePlaySound3D: %s&quot;, e.what());
    }
}

JNIEXPORT void JNICALL
Java_com_trashapp_oboe_AudioEngine_nativeSetListenerPosition(
    JNIEnv* env,
    jobject thiz,
    jfloat x,
    jfloat y,
    jfloat z
) {
    try {
        trashapp::audio::AudioEngine::getInstance().setListenerPosition(x, y, z);
    } catch (const std::exception&amp; e) {
        LOGE(&quot;Exception in nativeSetListenerPosition: %s&quot;, e.what());
    }
}

JNIEXPORT void JNICALL
Java_com_trashapp_oboe_AudioEngine_nativeSetMasterVolume(
    JNIEnv* env,
    jobject thiz,
    jfloat volume
) {
    try {
        trashapp::audio::AudioEngine::getInstance().setMasterVolume(volume);
    } catch (const std::exception&amp; e) {
        LOGE(&quot;Exception in nativeSetMasterVolume: %s&quot;, e.what());
    }
}

JNIEXPORT void JNICALL
Java_com_trashapp_oboe_AudioEngine_nativePlayMusic(
    JNIEnv* env,
    jobject thiz,
    jstring filename,
    jboolean loop
) {
    try {
        const char* filenameChars = env-&gt;GetStringUTFChars(filename, nullptr);
        trashapp::audio::AudioEngine::getInstance().playMusic(filenameChars, loop);
        env-&gt;ReleaseStringUTFChars(filename, filenameChars);
    } catch (const std::exception&amp; e) {
        LOGE(&quot;Exception in nativePlayMusic: %s&quot;, e.what());
    }
}

JNIEXPORT void JNICALL
Java_com_trashapp_oboe_AudioEngine_nativeStopMusic(
    JNIEnv* env,
    jobject thiz
) {
    try {
        trashapp::audio::AudioEngine::getInstance().stopMusic();
    } catch (const std::exception&amp; e) {
        LOGE(&quot;Exception in nativeStopMusic: %s&quot;, e.what());
    }
}

JNIEXPORT void JNICALL
Java_com_trashapp_oboe_AudioEngine_nativeSetMusicVolume(
    JNIEnv* env,
    jobject thiz,
    jfloat volume
) {
    try {
        trashapp::audio::AudioEngine::getInstance().setMusicVolume(volume);
    } catch (const std::exception&amp; e) {
        LOGE(&quot;Exception in nativeSetMusicVolume: %s&quot;, e.what());
    }
}

JNIEXPORT void JNICALL
Java_com_trashapp_oboe_AudioEngine_nativeEnableReverb(
    JNIEnv* env,
    jobject thiz,
    jboolean enable
) {
    try {
        trashapp::audio::AudioEngine::getInstance().enableReverb(enable);
    } catch (const std::exception&amp; e) {
        LOGE(&quot;Exception in nativeEnableReverb: %s&quot;, e.what());
    }
}

JNIEXPORT void JNICALL
Java_com_trashapp_oboe_AudioEngine_nativeSetReverbLevel(
    JNIEnv* env,
    jobject thiz,
    jfloat level
) {
    try {
        trashapp::audio::AudioEngine::getInstance().setReverbLevel(level);
    } catch (const std::exception&amp; e) {
        LOGE(&quot;Exception in nativeSetReverbLevel: %s&quot;, e.what());
    }
}

} // extern &quot;C&quot;