plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.maven.publish)
}

repositories {
    mavenCentral()

    // TODO: remove after dev.whyoleg.cryptography release 0.4.0
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")}

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
