pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    includeBuild("custom-build-plugins")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "FileEncryptor"
include(":app")
include(":encryptionlib")
include(":feature:encrypt")
include(":core:common")
include(":core:domain")
include(":EasyLogging")
include(":feature:decrypt")
include(":feature:home")
