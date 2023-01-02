package cn.chitanda.app.imovie.core.decoder

import android.net.Uri
import javax.inject.Inject

/**
 *@author: Chen
 *@createTime: 2022/11/22 17:09
 *@description:
 **/
class UriDecoder @Inject constructor() : StringDecoder {
    override fun decodeString(encodedString: String): String = Uri.decode(encodedString)
}