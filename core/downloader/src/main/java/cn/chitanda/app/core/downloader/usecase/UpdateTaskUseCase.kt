package cn.chitanda.app.core.downloader.usecase

import cn.chitanda.app.core.downloader.task.DownloadTask

/**
 * @author: Chen
 * @createTime: 2023/6/12 11:28
 * @description:
 **/
typealias UpdateFunction<T> = (t: T) -> Unit

class UpdateTaskUseCase<T : DownloadTask>(private val call: UpdateFunction<T>) {
    operator fun invoke(task: T) {
        call(task)
    }
}