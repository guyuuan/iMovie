plugins {
    id("imovie.android.lib")
    id("imovie.android.hilt")
    id("kotlinx-serialization")
}
android {
    namespace = "cn.chitanda.app.imovie.core.data"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:database"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))

    implementation(libs.androidx.core.ktx)

    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
}