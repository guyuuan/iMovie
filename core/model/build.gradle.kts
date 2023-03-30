plugins {
    id("chitanda.android.lib")
    id("kotlinx-serialization")
}
android {
    namespace = "cn.chitanda.app.imovie.core.model"
    buildTypes {
        val release by getting {
            proguardFiles(getDefaultProguardFile("proguard-android.txt"),"proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)
}