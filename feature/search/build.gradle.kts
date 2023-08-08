plugins {
    alias(libs.plugins.chitanda.android.feature)
    alias(libs.plugins.chitanda.android.lib.compose)

}
android {
    namespace = "cn.chitanda.app.imovie.feature.search"
}
dependencies {
    implementation(libs.paging3.runtime)
    implementation(libs.paging3.compose)
}