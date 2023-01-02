plugins {
    id("imovie.android.lib")
    id("imovie.android.hilt")
}

android {
    namespace = "cn.chitanda.app.imovie.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
//    testImplementation(project(":core:testing"))
}
