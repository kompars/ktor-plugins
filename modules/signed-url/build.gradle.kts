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

    compilerOptions {
        optIn = listOf("kotlin.time.ExperimentalTime")
    }

    jvm()
    linuxX64()
    linuxArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.cryptography.core)
            implementation(libs.ktor.server.core)
            implementation(libs.ktor.server.auth)
        }
    }
}
