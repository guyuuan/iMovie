buildscript {
    repositories {
        google()
        mavenCentral()
    }

}
plugins {
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.android.application) apply false
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.kotlin.jvm) apply false
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.kotlin.serialization) apply false
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.hilt) apply false
}