package com.zsmarter.hulk.net

import com.zsmarter.hulk.util.printLogI
import com.zsmarter.hulk.util.printLogE
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Author: Yu Peng
 * Time: 2021/12/2
 * Describe:
 */
internal object HttpHelper {

    /**
     * 异步请求
     */
    fun httpGet(path: String){
        MainScope().launch {
            kotlin.runCatching {
                val url = URL(path)
                val conn = (url.openConnection() as HttpURLConnection)
                    .also {
                        it.requestMethod = "GET"
                        it.readTimeout = 5000
                        it.connectTimeout = 5000
                    }
                if (conn.responseCode == 200) {
                    val inS = conn.inputStream
                    val b = ByteArray(1024)
                    var len: Int
                    // 创建字节数组输出流，读取输入流的文本数据时，同步把数据写入数组输出流
                    val bos = ByteArrayOutputStream()
                    while (true) {
                        len = inS.read(b)
                        if (len == -1) {
                            break
                        } else {
                            bos.write(b, 0, len)
                        }
                    }
                    // 把字节数组输出流的数据转换成字节数组
                    val text = String(bos.toByteArray(), charset("utf-8"))
                    printLogI("get请求：$url \r\n 结果：$text")
                }
            }.onSuccess {
            }.onFailure {
                printLogE("get请求失败，error：${it.message}")
            }
        }
    }
}