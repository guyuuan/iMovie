package cn.chitanda.app.core.downloader.utils

import kotlinx.datetime.Clock

/**
 * @author: Chen
 * @createTime: 2023/6/5 10:01
 * @description:
 **/
fun nowMilliseconds() = Clock.System.now().toEpochMilliseconds()