import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
    alias(libs.plugins.kotlin.mutliplatform)
    id("com.android.library")
}

val jniLibDir = File(project.buildDir, listOf("generated", "jniLibs").joinToString(File.separator))

kotlin {
    androidTarget()
    val nativeConfigure: KotlinNativeTarget.() -> Unit = {
        compilations {
            val main by getting {
                cinterops {
                    val libffmpeg by creating{
                        val abi = abiDirName(target.konanTarget)
                        val libDir = project.file(
                            listOf(
                                "libs", abi,
                            ).joinToString(File.separator)
                        )
                        linkerOpts += listOf(
                            "-L${
                                libDir.absolutePath
                            }",
                        )
                        extraOpts("-verbose","-Xdisable-exception-prettifier")

                    }
                }
            }
        }
        binaries {
            sharedLib("knffmpeg") {
                val abi = abiDirName(target.konanTarget)
                val libDir = project.file(
                    listOf(
                        "libs", abi,
                    ).joinToString(File.separator)
                )
                linkerOpts += listOf(
                    "-v",
                    "-L${
                        libDir.absolutePath
                    }",
                )
                linkTask.doLast {
                    copy {
                        from(outputFile)
                        val typeName =
                            if (buildType == NativeBuildType.DEBUG) "Debug" else "Release"

                        into(
                            file(
                                listOf(
                                    jniLibDir, typeName, abiDirName(target.konanTarget)
                                ).joinToString(File.separator)
                            )
                        )
                    }
                }

                afterEvaluate {
                    val preBuild by tasks.getting
                    preBuild.dependsOn(linkTask)
                }
            }
        }

    }
    androidNativeArm64(nativeConfigure)
    androidNativeArm32(nativeConfigure)
//    androidNativeX64(nativeConfigure)
//    androidNativeX86(nativeConfigure)

    sourceSets {
        val androidNativeArm32Main by getting
        val androidNativeArm64Main by getting
//        val androidNativeX86Main by getting
//        val androidNativeX64Main by getting

        val nativeMain by creating {
            androidNativeArm32Main.dependsOn(this)
            androidNativeArm64Main.dependsOn(this)
//            androidNativeX86Main.dependsOn(this)
//            androidNativeX64Main.dependsOn(this)
        }
    }

}

fun abiDirName(target: KonanTarget) = when (target) {
    KonanTarget.ANDROID_ARM32 -> "armeabi-v7a"
    KonanTarget.ANDROID_ARM64 -> "arm64-v8a"
    KonanTarget.ANDROID_X86 -> "x86"
    KonanTarget.ANDROID_X64 -> "x86_64"
    else -> "unknown"
}

android {
    namespace = "cn.chitanda.lib.ffmpeg"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    sourceSets {

        getByName("debug") {
            jniLibs.srcDirs(
                "$jniLibDir/Debug",
                "libs"
            )
        }
        getByName("release") {
            jniLibs.srcDirs(
                "$jniLibDir/Release",
                "libs"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies{
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.runner)
}
