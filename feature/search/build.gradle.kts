plugins {
    id("imovie.android.feature")
    id("imovie.android.lib.compose")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "cn.chitanda.app.imovie.feature.search"
}
dependencies {
    implementation(libs.paging3.runtime)
    implementation(libs.paging3.compose)
}