@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("chitanda.android.lib")
    id("chitanda.android.hilt")
    alias(libs.plugins.ksp)
}
ksp{
    arg("room.schemaLocation",File(projectDir, "schemas").path)
}
android {
    namespace = "cn.chitanda.app.imovie.core.downloader"
}

dependencies {
    implementation(libs.retrofit.core)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)
    implementation(libs.okio.core)
    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.media3.hls)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.room.runtime)
    implementation(libs.room.paging)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.runner)
}