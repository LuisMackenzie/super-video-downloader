plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.allopen) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.coveralls) apply false
}

// Force javapoet 1.13.0 in the build classpath so Hilt's AggregateDepsTask
// does not get the older 1.10.0 bundled by databinding-compiler-common.
buildscript {
    configurations.configureEach {
        resolutionStrategy {
            force("com.squareup:javapoet:1.13.0")
        }
    }
}


tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}
