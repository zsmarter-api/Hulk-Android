package cn.zsmarter.hulk.manager

import com.blankj.utilcode.util.SPUtils

/**
 * Create By YP On 2022/10/17.
 * 用途：
 */
object SharedPreferencesManager {

    private const val PRIVACY_CONFIRM_STATE = "privacy_confirm_state"
    private const val IS_FIRST_INIT = "is_first_init"

    fun saveIsFirstInit(isFirst: Boolean) {
        SPUtils.getInstance().put(IS_FIRST_INIT, isFirst)
    }

    fun getIsFirstInit() = SPUtils.getInstance().getBoolean(IS_FIRST_INIT, true)

    fun savePrivacyConfirmState(isConfirm: Boolean) {
        SPUtils.getInstance().put(PRIVACY_CONFIRM_STATE, isConfirm)
    }

    fun getPrivacyConfirmState() = SPUtils.getInstance().getBoolean(PRIVACY_CONFIRM_STATE)
}