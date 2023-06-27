package com.zsmarter.hulk.callback

import androidx.annotation.Keep

/**
 * Create By YP On 2023/2/8.
 * 用途：厂商初始化的回调
 */
@Keep
interface ManufacturersCallback {

    companion object {
        const val TYPE_XIAO_MI = "mi"
        const val TYPE_VIVO = "vivo"
        const val TYPE_HUA_WEI = "hw"
        const val TYPE_MEI_ZU = "meizu"
        const val TYPE_OPPO = "oppo"
    }

    fun initManufacturers(type: String, token: String)
}