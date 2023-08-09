import com.android.build.gradle.internal.cxx.io.writeTextIfDifferent
import org.jetbrains.kotlin.gradle.plugin.mpp.DefaultCInteropSettings
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.kotlin.mutliplatform)
    id("com.android.library")
}

val jniLibDir = File(project.buildDir, listOf("generated", "jniLibs").joinToString(File.separator))

kotlin {
    androidTarget()
    val nativeConfigure: KotlinNativeTarget.() -> Unit = {
        compilations {
            val main by getting {
                val kotlinTarget = target
                cinterops {
                    val libavcodec by creating {
                        interopConfig(kotlinTarget)
                    }
//                    val libavformat by creating {
//                        interopConfig(target)
//                    }
//                    val libavutil by creating {
//                        interopConfig(target)
//                    }
//                    val libswresample by creating {
//                        interopConfig(target)
//                    }
//                    val libswscale by creating {
//                        interopConfig(target)
//                    }
                }
            }
        }
        binaries {
            sharedLib("knffmpeg") {
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
    androidNativeX64(nativeConfigure)
    androidNativeX86(nativeConfigure)

    sourceSets {
        val androidNativeArm32Main by getting
        val androidNativeArm64Main by getting
        val androidNativeX86Main by getting
        val androidNativeX64Main by getting

        val nativeMain by creating {
            androidNativeArm32Main.dependsOn(this)
            androidNativeArm64Main.dependsOn(this)
            androidNativeX86Main.dependsOn(this)
            androidNativeX64Main.dependsOn(this)
        }
    }

}

fun DefaultCInteropSettings.interopConfig(target: KotlinNativeTarget) {
    val abi = abiDirName(target.konanTarget)
    val defPath = defFile.absolutePath
    val packname = defPath.substringAfterLast(File.separator).removeSuffix(".def")
    val file =
        File(defPath.replace(".def", "_${abiDirName(target.konanTarget)}.def"))
//                        val file = File(defPath)
    val templateFile = File(defPath.removeSuffix(".def"))
    val text = templateFile.readText()
    if (!file.exists()) {
        file.writeText(
            text.replace(
                "{LINK_PATH}", "-L ${
                    project.file(
                        listOf(
                            "libs", abi
                        ).joinToString(File.separator)
                    ).absolutePath
                }"
            )
        )

    } else {
        file.writeTextIfDifferent(
            text.replace(
                "{LINK_PATH}", project.file(
                    listOf(
                        "libs", abi
                    ).joinToString(File.separator)
                ).absolutePath
            )
        )
    }
    defFile = file
    val libDir = project.file(
        listOf(
            "libs", abi, "include", packname
        ).joinToString(File.separator)
    )
    packageName = "$packname.${abi.replace("-", "_")}"
    compilerOpts += listOf(
        "-I${
            libDir.parentFile.absolutePath
        }",
        "-I${
            libDir.absolutePath
        }",
    )
    extraOpts("-verbose","-Xdisable-exception-prettifier")
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
    compileSdk = 33

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