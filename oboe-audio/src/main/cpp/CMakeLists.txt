cmake_minimum_required(VERSION 3.22.1)
project(&quot;oboe-audio&quot;)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Oboe library will be included as git submodule
# Include paths for Oboe
target_include_directories(trashaudio PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/oboe/include
    ${CMAKE_CURRENT_SOURCE_DIR}/include
)

# Create native library
add_library(trashaudio SHARED
    src/main/cpp/AudioEngine.cpp
    src/main/cpp/AudioManager.cpp
    src/main/cpp/jni_bridge.cpp
    src/main/cpp/AudioMixer.cpp
    src/main/cpp/SpatialAudio.cpp
    src/main/cpp/SoundManager.cpp
)

target_include_directories(trashaudio PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/oboe/include
    ${CMAKE_CURRENT_SOURCE_DIR}/include
)

target_link_libraries(trashaudio
    oboe
    log
    android
)

# Download Oboe if not present
if(NOT EXISTS ${CMAKE_CURRENT_SOURCE_DIR}/oboe)
    message(STATUS &quot;Cloning Oboe library...&quot;)
    execute_process(
        COMMAND git clone --depth 1 https://github.com/google/oboe.git
        WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
    )
    add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/oboe oboe-build)
else()
    if(NOT TARGET oboe)
        add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/oboe oboe-build)
    endif()
endif()