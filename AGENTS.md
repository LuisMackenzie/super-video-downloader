# AGENTS.md

Repository guide for coding agents working in `super-video-downloader`.

## Project Snapshot

- Android app in a single Gradle module: `:app`.
- Language: Kotlin.
- UI stack: Android Views + DataBinding, not Compose.
- Architecture: pragmatic MVVM with repositories, local/remote data sources, Room, Dagger 2.
- Async stack is mixed: Coroutines, RxJava 3, LiveData, `ObservableField`, and `SingleLiveEvent`.
- Media/network stack includes WorkManager, OkHttp, Retrofit, youtube-dl Android, ffmpeg, Media3, and custom Go/V2Ray native libs.
- There is no existing `AGENTS.md`; this file is newly created.
- No Cursor rules were found in `.cursor/rules/` or `.cursorrules`.
- No Copilot rules were found in `.github/copilot-instructions.md`.

## Environment Requirements

- JDK 21 (`.github/workflows/android.yml` uses Temurin 21).
- Android SDK with compile/target SDK 36 and min SDK 24.
- Android NDK `27.3.13750724`.
- Go 1.25.x for the native Go build path.
- `gradlew` should be run from repository root.

## Important Build Behavior

- `:app:preBuild` depends on Go native library generation.
- A normal Android build may trigger:
  - `cloneV2raySource`
  - `vendorGoDependencies`
  - per-ABI Go builds
  - copying generated `.so` files into `app/src/main/jniLibs`
- Release signing expects these env vars:
  - `KEYSTORE_PATH`
  - `KEYSTORE_PASSWORD`
  - `KEY_ALIAS`
  - `KEY_PASSWORD`
- Go binary detection can be overridden with:
  - env: `GO_EXECUTABLE=/path/to/go`
  - Gradle property: `-PGO_EXECUTABLE=/path/to/go`
- NDK path detection looks at:
  - `ANDROID_NDK_HOME`
  - `ANDROID_NDK_ROOT`
  - `ndk.dir` in `local.properties`
  - side-by-side NDK in the Android SDK

## Core Commands

- Build debug APK: `./gradlew :app:assembleDebug`
- Build all modules and checks: `./gradlew build --info`
- Build release APKs: `./gradlew :app:assembleRelease --info`
- Clean: `./gradlew clean`
- Android lint: `./gradlew :app:lint`
- Lint one variant: `./gradlew :app:lintDebug`
- Run all configured JVM unit tests: `./gradlew :app:testDebugUnitTest`
- Run instrumented tests on device/emulator: `./gradlew :app:connectedDebugAndroidTest`
- Build native Go libs only: `./gradlew :app:buildAllGoLibraries`
- Vendor Go deps only: `./gradlew :app:vendorGoDependencies`
- Prepare Go deps without full Android build: `./gradlew :app:prepareGoBuild`
- Generate coverage-related checks: `./gradlew check`

## Single-Test Commands

- Single JVM test class: `./gradlew :app:testDebugUnitTest --tests "com.myAllVideoBrowser.SomeTest"`
- Single JVM test method: `./gradlew :app:testDebugUnitTest --tests "com.myAllVideoBrowser.SomeTest.someMethod"`
- Single instrumentation test class: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.myAllVideoBrowser.SomeTest`
- Single instrumentation test method: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.myAllVideoBrowser.SomeTest#someMethod`

## Testing Caveat

- No `app/src/test` or `app/src/androidTest` Kotlin tests were found during analysis.
- `app/build.gradle.kts` currently contains `unitTests.all { it.exclude("**/*") }`.
- That means JVM test tasks are effectively configured to run zero tests unless the exclusion is changed.
- If you add unit tests, either remove that exclusion or confirm it is intentional before relying on `testDebugUnitTest`.

## Repository Layout

- `app/src/main/java/com/myAllVideoBrowser/ui/main/...`: screens, fragments, activities, view models.
- `app/src/main/java/com/myAllVideoBrowser/ui/component/...`: adapters, bindings, dialogs, reusable widgets.
- `app/src/main/java/com/myAllVideoBrowser/data/local/...`: Room DAOs, entities, local data sources, local models.
- `app/src/main/java/com/myAllVideoBrowser/data/remote/...`: remote data sources and service contracts.
- `app/src/main/java/com/myAllVideoBrowser/data/repository/...`: repository interfaces and implementations.
- `app/src/main/java/com/myAllVideoBrowser/di/...`: Dagger component, modules, scopes, qualifiers, ViewModel map keys.
- `app/src/main/java/com/myAllVideoBrowser/util/...`: helpers, logging, workers, downloaders, proxy/network utilities.
- `app/schemas/...`: Room schema history; update this when DB schema changes.

## Architecture Conventions

- Preserve the existing single-module architecture unless the task explicitly asks for a refactor.
- Prefer the existing pattern: `Repository` interface + `LocalDataSource` / `RemoteDataSource` + `RepositoryImpl`.
- Dagger 2 is the DI framework here; do not introduce Hilt unless explicitly requested.
- UI uses Fragments/Activities with DataBinding and `ViewModelProvider.Factory` injection.
- View models typically extend `BaseViewModel` and implement `start()` / `stop()`.
- UI state is often exposed via `ObservableField`, `ObservableBoolean`, `ObservableInt`, `LiveData`, or `SingleLiveEvent`.
- Do not introduce Compose, Flow-based UI state, or large architectural rewrites just because they are newer.
- Coroutines and RxJava are both first-class in this codebase; follow the surrounding file instead of force-converting one to the other.

## Kotlin Style

- Follow `kotlin.code.style=official` from `gradle.properties`.
- Use 4-space indentation and keep line wrapping consistent with surrounding code.
- Prefer trailing commas in multiline parameter and collection literals when already used nearby.
- Keep braces and declaration layout aligned with existing Kotlin conventions in the file.
- Use expression bodies for short provider/helper functions when they improve clarity.
- Use block bodies when logic, branching, or logging is non-trivial.

## Imports

- Prefer explicit imports.
- Avoid adding new wildcard imports even though a few existing files still use them.
- Keep AndroidX, third-party, and project imports grouped in a stable order.
- Remove unused imports as you edit files.
- If touching a file that already has star imports, do not churn the whole file unless the task benefits from cleanup.

## Naming

- Types use `PascalCase`.
- Functions, properties, and locals use `camelCase`.
- Constants use `UPPER_SNAKE_CASE`.
- Repository interfaces are named `XRepository`; implementations are `XRepositoryImpl`.
- Data sources are named `XLocalDataSource` and `XRemoteDataSource`.
- Dagger modules end with `Module`; qualifiers/scopes are annotation-style names such as `RemoteData`, `LocalData`, `ActivityScoped`.
- View models end with `ViewModel`; adapters end with `Adapter`; DAOs end with `Dao`.
- Event-like properties commonly end with `Event`.

## Types And APIs

- Add explicit types for public APIs and injected dependencies when useful for readability.
- Match existing mutability patterns: many models use `var` because Room/DataBinding mutate them.
- Prefer nullable return types or `Result.failure()` where surrounding code already uses them.
- Do not replace existing `ObservableField` or `SingleLiveEvent` usage unless the task requires it.
- When adding Room entities or converters, keep them compatible with existing serialization and migration patterns.

## Error Handling And Logging

- In UI/view-model code, follow the local pattern: catch exceptions, log them, and fail gracefully.
- Use `AppLogger` for app logging when possible.
- Some files still use `printStackTrace()`; do not expand that pattern unless you are working in the same area.
- Avoid crashing the app from UI-triggered paths for recoverable issues.
- In workers, return `Result.failure()` for operational failures rather than throwing after startup.
- In build logic, explicit `GradleException` is acceptable for missing required environment configuration.

## Concurrency Guidelines

- Use `viewModelScope` in view models.
- Use `Dispatchers.IO` for network, storage, and heavy file work.
- Preserve existing custom executors/dispatchers where a file already uses them.
- Be careful with blocking Rx calls such as `blockingGet()`; only use them where the current layer already depends on them.
- Do not introduce global coroutine scopes unless the file already follows that pattern.

## Android-Specific Guidance

- Respect DataBinding field names already consumed by XML layouts.
- Keep manifest/service/provider changes minimal and consistent with current exported/foreground-service usage.
- If you change Room schema, update migrations and verify `app/schemas` output.
- If you touch downloader/proxy/native build code, assume ABI, NDK, and Go configuration matters.
- WorkManager is manually initialized in `DLApplication`; avoid adding conflicting startup initialization.

## When Making Changes

- Prefer small, targeted edits over broad cleanup.
- Preserve package structure and naming in the current feature area.
- Follow existing file-local conventions before applying generic Kotlin best practices.
- If you add tests, document whether they are JVM or instrumentation tests and note any Gradle exclusion that must be changed.
- If a command fails because of missing NDK, Go, signing, or emulator prerequisites, report the missing prerequisite clearly instead of guessing.
