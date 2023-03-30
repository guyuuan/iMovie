plugins {
    id("chitanda.android.feature")
    id("chitanda.android.lib.compose")
    id("org.jetbrains.kotlin.android")
}
android{
    namespace = "cn.chitanda.app.imovie.feature.history"
}
dependencies{
    implementation(libs.paging3.runtime)
    implementation(libs.paging3.compose)
    implementation(libs.kotlinx.datetime)
}