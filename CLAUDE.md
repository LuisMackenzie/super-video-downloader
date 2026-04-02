# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Android video/audio downloader app with built-in browser, player, and proxy support. Downloads from YouTube, Facebook, Twitter, Instagram, and 1000+ other sites. Supports HLS/DASH streams and live captures.

## Build Commands

```bash
# Build debug APK
./gradlew :app:assembleDebug

# Build release APK (requires signing env vars)
./gradlew :app:assembleRelease

# Clean build artifacts
./gradlew clean

# Run lint
./gradlew :app:lint

# Build Go native libraries (triggered automatically by preBuild)
./gradlew :app:buildAllGoLibraries

# Vendor Go dependencies only
./gradlew :app:vendorGoDependencies
```

## Environment Requirements

- **JDK 21** (Temurin recommended)
- **Android SDK**: compile/target SDK 36, min SDK 24
- **Android NDK**: version `27.3.13750724`
- **Go**: 1.25.x (for V2Ray native libs)

### NDK Path Detection (in order)
1. `ANDROID_NDK_HOME` env var
2. `ANDROID_NDK_ROOT` env var
3. `ndk.dir` in `local.properties`
4. Side-by-side NDK in Android SDK

### Go Binary Override
```bash
export GO_EXECUTABLE=/opt/homebrew/bin/go
# or
./gradlew -PGO_EXECUTABLE=/path/to/go :app:assembleDebug
```

### Release Signing (env vars required)
- `KEYSTORE_PATH`
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

## Architecture

Single-module Android app (`:app`) using:
- **Language**: Kotlin
- **UI**: Android Views + DataBinding (NOT Compose)
- **Architecture**: MVVM with Repository pattern
- **DI**: Dagger 2 (manual component setup, NO Hilt)
- **Database**: Room with migrations
- **Async**: Mixed Coroutines and RxJava 3
- **Network**: OkHttp + Retrofit
- **Media**: youtube-dl-android, FFmpeg, Media3 ExoPlayer
- **Native**: Go/V2Ray libs for proxy functionality

### Package Structure
```
com.mackenzie.downhub/
├── DLApplication.kt          # Application class, DI entry point
├── data/
│   ├── local/                # Room DAOs, entities, local data sources
│   ├── remote/               # Retrofit services, remote data sources
│   └── repository/           # Repository interfaces and implementations
├── di/                       # Dagger components, modules, scopes, qualifiers
├── ui/
│   ├── main/                 # Activities, fragments, view models
│   └── component/            # Adapters, bindings, dialogs, widgets
└── util/                     # Helpers, workers, downloaders, proxy utils
```

### Key Patterns
- Repository interface + `LocalDataSource`/`RemoteDataSource` + `RepositoryImpl`
- View models extend `BaseViewModel` with `start()`/`stop()` lifecycle
- UI state via `ObservableField`, `LiveData`, `SingleLiveEvent`
- Room migrations in `DatabaseModule.kt`, schemas in `app/schemas/`

## Testing

**Note**: Unit tests are currently excluded in `app/build.gradle.kts`:
```kotlin
testOptions {
    unitTests.all { it.exclude("**/*") }
}
```

If adding tests, remove or modify this exclusion first.

```bash
# JVM tests (after fixing exclusion)
./gradlew :app:testDebugUnitTest

# Single test class
./gradlew :app:testDebugUnitTest --tests "com.mackenzie.downhub.SomeTest"

# Instrumentation tests
./gradlew :app:connectedDebugAndroidTest
```

## Code Conventions

- **Style**: Official Kotlin style (`kotlin.code.style=official`)
- **Indentation**: 4 spaces
- **Imports**: Explicit imports, avoid wildcards
- **Naming**: `PascalCase` for types, `camelCase` for functions/properties
- **Repositories**: `XRepository` interface, `XRepositoryImpl` implementation
- **Data sources**: `XLocalDataSource`, `XRemoteDataSource`
- **ViewModels**: End with `ViewModel`, adapters end with `Adapter`

## Important Notes

- **Do NOT introduce Jetpack Compose** - this project uses Views + DataBinding
- **Do NOT introduce Hilt** - uses manual Dagger 2 setup
- **Do NOT force-convert RxJava ↔ Coroutines** - both are first-class; follow surrounding code
- WorkManager is manually initialized in `DLApplication` - avoid conflicting initialization
- Go native libraries are built automatically during `preBuild` for all ABIs (arm64-v8a, armeabi-v7a, x86, x86_64)

## Detailed Guidance

For comprehensive conventions including Kotlin style, error handling, concurrency, and Android-specific guidance, see [AGENTS.md](AGENTS.md).