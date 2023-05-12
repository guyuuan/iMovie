package cn.chitanda.app.core.downloader.decrypt

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * @author: Chen
 * @createTime: 5/12/23 17:00
 * @description:
 **/

fun getCipher(key: ByteArray, method: String): Cipher {
    return Cipher.getInstance("AES").apply {
        init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"))
    }
}