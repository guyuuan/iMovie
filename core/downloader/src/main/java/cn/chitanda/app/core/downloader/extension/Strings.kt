package cn.chitanda.app.core.downloader.extension

import java.math.BigInteger
import java.security.MessageDigest

/**
 * @author: Chen
 * @createTime: 2023/5/31 15:22
 * @description:
 **/
fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1,md.digest(toByteArray())).toString(16).padStart(32, '0')
}