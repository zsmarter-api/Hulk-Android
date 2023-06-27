package cn.zsmarter.hulk.manager

import cn.zsmarter.hulk.BuildConfig
import com.blankj.utilcode.util.SPUtils

/**
 * Author: YP
 * Time: 2022/6/7
 * Describe:
 */
object NetManager {

    private const val KEY_CURRENT_NET = "key_current_net"
    private val sp by lazy {
        SPUtils.getInstance()
    }
    private val nets = listOf(
        CurrentNet(0,"生产环境", "47.108.219.214", "8443"),
        CurrentNet(1, "测试环境", "47.108.219.214", "10639"),
    )

    fun isProduct(): Boolean {
        return sp.getInt(KEY_CURRENT_NET, 0) == 0
    }

    fun saveCurrentNet(id: Int? = null) {
        val current = id ?: if (isProduct()) {
            1
        } else {
            0
        }
        sp.put(KEY_CURRENT_NET, current)
    }

    fun getCurrentNet(): CurrentNet {
        return nets[sp.getInt(KEY_CURRENT_NET, 0)]
    }

    fun getNetEnvById(id: Int): CurrentNet {
        return nets[id]
    }

    fun getCurrentAppType() = BuildConfig.DEBUG
//    fun getCurrentAppType() = false
}

data class CurrentNet(
    val netId: Int,
    val netName: String,
    val host: String,
    val port: String
)