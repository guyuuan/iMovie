plugins{
    alias(libs.plugins.chitanda.android.lib)
    alias(libs.plugins.chitanda.android.lib.compose)
}

android{
    namespace = "cn.chitanda.app.imovie.core.design"
}

dependencies{
    implementation(libs.androidx.core.ktx)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.compose.material.iconsExtended)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material3.windowSizeClass)
    debugApi(libs.androidx.compose.ui.tooling)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.compose.ui.util)
    api(libs.androidx.compose.runtime)
}