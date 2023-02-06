plugins{
    id("imovie.android.lib")
    id("imovie.android.hilt")
    id("kotlinx-serialization")
}

android {
    namespace= "cn.chitanda.app.imovie.core.network"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
}