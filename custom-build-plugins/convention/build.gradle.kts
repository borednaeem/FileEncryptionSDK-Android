@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

group = "com.example.fileencryptor.custom-build-plugins"


gradlePlugin {
    plugins {
        register("androidHilt") {
            id = "FileEncryptor.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
    }
}
