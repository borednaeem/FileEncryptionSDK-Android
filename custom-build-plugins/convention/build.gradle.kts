@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
//    id("java-library")
    `kotlin-dsl`
     //alias(libs.plugins.org.jetbrains.kotlin.jvm)
//    alias(libs.plugins.)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

group = "com.example.fileencryptor.custom-build-plugins"


gradlePlugin {
    plugins {
    }
}
