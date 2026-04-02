plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
dependencies {
    implementation(project(":domain"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
    implementation(libs.arrow.either)

    // Networking + HTML parsing (embed resolver)
    // OKHttp3
    implementation(libs.okHttpRuntime)
    implementation(libs.jsoup)

    // Moshi converters
    implementation(libs.moshi.kotlin)
    implementation(libs.converter.moshi)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}
