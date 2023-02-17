package cn.chitanda.app.imovie

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor
import org.gradle.api.Project

/**
 * @author: Chen
 * @createTime: 2023/2/17 16:19
 * @description:
 **/
enum class AppFlavors(
    val flavor: String,
    val dimension: FlavorDimension = FlavorDimension.ContentType
) {
    Debug("dev"),
    Release("prod")
}

enum class FlavorDimension {
    ContentType
}

fun Project.configureFlavors(
    commonExtension: CommonExtension<*, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: AppFlavors) -> Unit = {}
) {
    commonExtension.apply {
        flavorDimensions += FlavorDimension.ContentType.name
        productFlavors {
            AppFlavors.values().forEach {
                create(it.flavor) {
                    dimension = it.dimension.name
                    flavorConfigurationBlock(this, it)
                }
            }
        }
    }
}
