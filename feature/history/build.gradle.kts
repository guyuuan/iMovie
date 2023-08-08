plugins {
    alias(libs.plugins.chitanda.android.feature)
    alias(libs.plugins.chitanda.android.lib.compose)
}
android{
    namespace = "cn.chitanda.app.imovie.feature.history"
}
dependencies{
    implementation(libs.paging3.runtime)
    implementation(libs.paging3.compose)
    implementation(libs.kotlinx.datetime)
}