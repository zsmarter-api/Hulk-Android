package cn.zsmarter.hulk.manager

import androidx.lifecycle.MutableLiveData
import cn.zsmarter.hulk.entity.LogEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Author: YP
 * Time: 2022/3/31
 * Describe: 日志数据管理
 */


object LogDataManager {

    val logData = mutableListOf<LogEntity>()
    val newLogData = MutableLiveData<LogEntity>()

    fun addData(log: LogEntity, index: Int = 0) {
        MainScope().launch(Dispatchers.Main) {

            logData.add(index, log)
            newLogData.value = log
        }
    }
}