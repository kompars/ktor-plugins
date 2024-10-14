plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.maven.publish)
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(11)
    explicitApi()

    jvm()
    linuxX64()
    linuxArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.cryptography.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.ktor.server.core)
            implementation(libs.ktor.server.auth)
        }
    }
}
