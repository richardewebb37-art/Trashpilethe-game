cmake_minimum_required(VERSION 3.22.1)
project(&quot;skia-graphics&quot;)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Include paths for Skia
target_include_directories(trashgraphics PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/skia/include
    ${CMAKE_CURRENT_SOURCE_DIR}/include
)

# Create native library
add_library(trashgraphics SHARED
    src/main/cpp/GraphicsEngine.cpp
    src/main/cpp/Renderer.cpp
    src/main/cpp/ShaderManager.cpp
    src/main/cpp/CardRenderer.cpp
    src/main/cpp/ParticleEffect.cpp
    src/main/cpp/jni_bridge.cpp
)

target_include_directories(trashgraphics PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/skia/include/core
    ${CMAKE_CURRENT_SOURCE_DIR}/skia/include/gpu
    ${CMAKE_CURRENT_SOURCE_DIR}/skia/include/effects
    ${CMAKE_CURRENT_SOURCE_DIR}/include
)

# Link Skia library (will be built from source)
target_link_libraries(trashgraphics
    skia
    log
    android
    EGL
    GLESv3
)

# Download and build Skia if not present
if(NOT EXISTS ${CMAKE_CURRENT_SOURCE_DIR}/skia)
    message(STATUS &quot;Cloning Skia library...&quot;)
    execute_process(
        COMMAND git clone --depth 1 https://skia.googlesource.com/skia.git
        WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
    )
    
    # Build Skia for Android
    execute_process(
        COMMAND python3 tools/git-sync-deps
        WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/skia
    )
    
    add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/skia skia-build)
else()
    if(NOT TARGET skia)
        add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/skia skia-build)
    endif()
endif()