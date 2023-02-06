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
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "iMovie"
include( ":app")
include( ":core:common")
include( ":core:data")
include( ":core:media")
include( ":core:design")
include( ":core:ui")
include( ":core:database")
include( ":core:model")
include( ":core:network")
include( ":feature:home")
include( ":feature:play")
include(":feature:search")
include(":feature:history")
include(":feature:recently")