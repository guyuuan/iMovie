plugins {
    id("chitanda.android.feature")
    id("chitanda.android.lib.compose")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "cn.chitanda.app.imovie.feature.setting"
}

dependencies {
    implementation(project(":core:datastore"))
}