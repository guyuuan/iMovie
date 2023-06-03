plugins {
    id("chitanda.android.lib")
    id("chitanda.android.hilt")
}
android {
    namespace = "cn.chitanda.app.imovie.core.downloader"
}

dependencies {
    implementation(libs.retrofit.core)
    implementation(libs.okhttp.core)
    implementation(libs.okio.core)
    implementation(libs.kotlinx.datetime)
    testImplementation(libs.kotlinx.coroutines.test)
    implementation(libs.androidx.media3.hls)
    implementation(libs.androidx.media3.exoplayer)
}