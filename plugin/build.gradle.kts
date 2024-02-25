plugins {
    alias(libs.plugins.kotlin.dsl)
}

repositories {
    mavenLocal()
    mavenCentral()

    google()
    gradlePluginPortal()
}

dependencies {
    arrayOf(
        libs.kotlin.plugin,
        libs.kotlin.serialization.plugin,
        libs.allure.plugin,
    ).forEach { api(it) }
}
