package cn.chitanda.app.imovie.core.decoder

/**
 *@author: Chen
 *@createTime: 2022/11/22 17:08
 *@description:
 **/
interface StringDecoder {
    fun decodeString(encodedString: String):String
}