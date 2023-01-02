plugins{
    `kotlin-dsl`
}
group = "cn.chitanda.app.imovie.buildlogic"
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin{
    plugins{
        register("androidAppCompose"){
            id = "imovie.android.app.compose"
            implementationClass = "AndroidAppComposeConventionPlugin"
        }
        register("androidApp"){
            id = "imovie.android.app"
            implementationClass = "AndroidAppConventionPlugin"
        }
        register("androidLibCompose"){
            id = "imovie.android.lib.compose"
            implementationClass = "AndroidLibComposeConventionPlugin"
        }
        register("androidLib"){
            id = "imovie.android.lib"
            implementationClass = "AndroidLibConventionPlugin"
        }
        register("androidFeature"){
            id = "imovie.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidHilt"){
            id= "imovie.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidTest"){
            id = "imovie.android.test"
            implementationClass = "AndroidTestConventionPlugin"
        }

    }
}