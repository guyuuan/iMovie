@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("./build-logic/libs.versions.toml"))
        }
    }
}
rootProject.name = "iMovie"
include( ":app")
include( ":ffmpeg")
include( ":core:common")
include( ":core:data")
include( ":core:media")
include( ":core:design")
include( ":core:ui")
include( ":core:database")
include( ":core:model")
include( ":core:network")
include(":core:datastore")
include(":core:downloader")

include( ":feature:home")
include( ":feature:play")
include(":feature:search")
include(":feature:history")
include(":feature:recently")
include(":feature:setting")
