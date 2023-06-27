@file:JvmName("LogUtil")

package com.zsmarter.hulk.util

import android.util.Log
import com.zsmarter.hulk.socket.ZTPSocketManager

internal fun printLogI(msg: String?) {
    if (ZTPSocketManager.printLogEnable) {
        Log.i("HulkPush", msg.toString())
    }
}

internal fun printLogE(msg: String?) {
    if (ZTPSocketManager.printLogEnable) {
        Log.e("HulkPush", msg.toString())
    }
}