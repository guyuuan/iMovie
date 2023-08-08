plugins {
    alias(libs.plugins.chitanda.android.feature)
    alias(libs.plugins.chitanda.android.lib.compose)

}
android {
    namespace = "cn.chitanda.app.imovie.feature.setting"
}

dependencies {
    implementation(project(":core:datastore"))
}