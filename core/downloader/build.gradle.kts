@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("chitanda.android.lib")
    id("chitanda.android.hilt")
    alias(libs.plugins.ksp)
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
    implementation(libs.room.runtime)
    implementation(libs.room.paging)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.runner)
}