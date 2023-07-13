buildscript {
    repositories {
        google()
        mavenCentral()
    }

}
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.mutliplatform) apply false
    alias(libs.plugins.hilt) apply false
}
true