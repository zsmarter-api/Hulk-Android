package cn.zsmarter.hulk

import androidx.multidex.MultiDexApplication
import com.kongzue.dialogx.DialogX

/**
 * Author: YP
 * Time: 2022/6/8
 * Describe:
 */
class App : MultiDexApplication(){

    override fun onCreate() {
        super.onCreate()
        DialogX.init(this)
    }
}