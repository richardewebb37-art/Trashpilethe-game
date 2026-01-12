plugins {
    id(&quot;com.android.library&quot;)
}

android {
    namespace = &quot;com.trashapp.skia&quot;
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        
        ndk {
            abiFilters.addAll(listOf(&quot;arm64-v8a&quot;, &quot;armeabi-v7a&quot;, &quot;x86_64&quot;))
        }
        
        externalNativeBuild {
            cmake {
                cppFlags(&quot;-std=c++17&quot;, &quot;-O3&quot;, &quot;-DNDEBUG&quot;)
                arguments(&quot;-DANDROID_STL=c++_shared&quot;)
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    externalNativeBuild {
        cmake {
            path = file(&quot;src/main/cpp/CMakeLists.txt&quot;)
            version = &quot;3.22.1&quot;
        }
    }
}

dependencies {
    implementation(&quot;androidx.core:core-ktx:1.12.0&quot;)
}