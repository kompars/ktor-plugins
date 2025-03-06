rootProject.name = "ktor-plugins"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("modules:content-security-policy")
include("modules:hotwire-turbo")
include("modules:interceptor")
include("modules:openid-connect")
include("modules:signed-url")
include("modules:transactions")
