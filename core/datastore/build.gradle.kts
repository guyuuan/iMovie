//import com.google.protobuf.gradle.builtins
//import com.google.protobuf.gradle.generateProtoTasks
//import com.google.protobuf.gradle.protobuf
//import com.google.protobuf.gradle.protoc

plugins {
    alias(libs.plugins.protobuf)
    alias(libs.plugins.chitanda.android.lib)
    alias(libs.plugins.chitanda.android.hilt)
}

android {
    namespace = "cn.chitanda.app.imovie.core.datastore"
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                val java by registering {
                    option("lite")
                }
                val kotlin by registering {
                    option("lite")
                }
            }
        }
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.dataStore.core)
    implementation(libs.protobuf.kotlin.lite)
}