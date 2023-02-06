plugins{
    id("imovie.android.lib")
    id("kotlinx-serialization")
}
android {
    namespace = "cn.chitanda.app.imovie.core.model"
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)
}