package cn.chitanda.app.imovie.core.network

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

/**
 *@author: Chen
 *@createTime: 2022/11/19 13:20
 *@description:
 **/
@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val appDispatcher:AppDispatchers)
enum class AppDispatchers{
    IO
}