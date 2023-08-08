plugins{
    alias(libs.plugins.chitanda.android.feature)
    alias(libs.plugins.chitanda.android.lib.compose)

}

android{
    namespace = "cn.chitanda.app.imovie.feature.play"
}

dependencies{
    implementation(project(":core:media"))
    implementation(libs.androidx.media3.hls)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.okhttp)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.appcompat)
}