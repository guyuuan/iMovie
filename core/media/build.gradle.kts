plugins {
    alias(libs.plugins.chitanda.android.lib)
    alias(libs.plugins.chitanda.android.hilt)
}

android {
    namespace = "cn.chitanda.app.imovie.core.media"
}
dependencies {
    implementation(project(":core:model"))
    implementation(libs.coil.kt)
    api(libs.androidx.media3.hls)
    api(libs.androidx.media3.exoplayer)
    api(libs.androidx.media3.ui)
    api(libs.androidx.media3.session)
}