//import com.google.protobuf.gradle.builtins
//import com.google.protobuf.gradle.generateProtoTasks
//import com.google.protobuf.gradle.protobuf
//import com.google.protobuf.gradle.protoc
import com.google.protobuf.gradle.*

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.protobuf)
    id("imovie.android.lib")
    id("imovie.android.hilt")
}

android {
    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
    }
    namespace = "cn.chitanda.app.imovie.core.datastore"

//    sourceSets {
//        val main by getting
//        main.java{
//            srcDir("build/generated/source/proto/main/java")
//        }
//        main.kotlin{
//            srcDir("build/generated/source/proto/main/kotlin")
//        }
//        main.proto{
//            srcDir("src/main/proto")
//        }
//    }
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