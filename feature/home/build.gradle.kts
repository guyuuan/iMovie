plugins {
    id("chitanda.android.feature")
    id("chitanda.android.lib.compose")
    id("org.jetbrains.kotlin.android")
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