plugins {
    alias(libs.plugins.chitanda.android.lib)
    alias(libs.plugins.chitanda.android.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}
room {
    schemaDirectory("$projectDir/schemas/")
}
android {
    namespace = "cn.chitanda.app.imovie.core.database"
}

dependencies {
    implementation(libs.room.runtime)
    implementation(libs.room.paging)
    implementation(libs.room.ktx)
    implementation(libs.room.gradle)
    implementation(libs.paging3.runtime)
    implementation(libs.kotlinx.datetime)
    ksp(libs.room.compiler)
}
