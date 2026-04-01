plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
dependencies {
    implementation(project(":data"))
    implementation(project(":domain"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
    implementation(libs.arrow.either)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    // testImplementation(libs.mockito.kotlin)
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}
