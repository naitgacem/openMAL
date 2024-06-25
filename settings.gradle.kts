pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.scijava.org/content/repositories/public/")
    }
}

rootProject.name = "open MAL"
include(":app")
include(":openmalnet")
include(":domain")
