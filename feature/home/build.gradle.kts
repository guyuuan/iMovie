plugins {
    alias(libs.plugins.chitanda.android.feature)
    alias(libs.plugins.chitanda.android.lib.compose)
}
android{
    namespace = "cn.chitanda.app.imovie.feature.home"
}

dependencies {
    implementation(libs.accompanist.flowlayout)
}

dependencies {
    api(project(":feature:recently"))
    api(project(":feature:search"))
    api(project(":feature:history"))
    api(project(":feature:setting"))
}