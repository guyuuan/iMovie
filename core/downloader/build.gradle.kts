plugins {
    id("chitanda.android.lib")
    id("chitanda.android.hilt")
}
android {
    namespace = "cn.chitanda.app.imovie.core.downloader"
}

dependencies {
    implementation(libs.okhttp.core)
    implementation(libs.okio.core)
    testImplementation(libs.kotlinx.coroutines.test)
}