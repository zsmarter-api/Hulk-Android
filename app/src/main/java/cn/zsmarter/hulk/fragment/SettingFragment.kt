package cn.zsmarter.hulk.fragment

import android.os.Bundle
import cn.zsmarter.hulk.base.BaseFragment
import cn.zsmarter.hulk.databinding.FragmentSettingBinding
import cn.zsmarter.hulk.dialog.AreaPickerDialogFragment
import cn.zsmarter.hulk.entity.CityEntity
import cn.zsmarter.hulk.manager.NetManager
import cn.zsmarter.hulk.manager.ParamManager
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kongzue.dialogx.dialogs.TipDialog
import com.kongzue.dialogx.dialogs.WaitDialog

/**
 * Create By YP On 2022/6/28.
 * 用途：
 */
class SettingFragment : BaseFragment<FragmentSettingBinding>() {

    private var currentType = TYPE_PRODUCT
    private var selectLocation: CityEntity? = null

    companion object {

        private const val KEY_TYPE = "type"
        const val TYPE_PRODUCT = 0
        const val TYPE_TEST = 1

        fun getInstance(type: Int) = SettingFragment().apply {
            arguments = Bundle().apply {
                putInt(KEY_TYPE, type)
            }
        }
    }

    override fun initViews() {
        super.initViews()
        currentType = arguments?.getInt(KEY_TYPE, TYPE_PRODUCT) ?: TYPE_PRODUCT
        val userId: String
        val alias: String
        val phone: String?
        val location: String?
        val email: String?
        val wechat: String?
        if (currentType == TYPE_PRODUCT) {
            userId = ParamManager.USER_ID
            alias = ParamManager.ALIAS
            phone = ParamManager.PHONE
            location = ParamManager.LOCATION?.name
            email = ParamManager.EMAIL
            wechat = ParamManager.WECHAT
        } else {
            userId = ParamManager.TEST_USER_ID
            alias = ParamManager.TEST_ALIAS
            phone = ParamManager.TEST_PHONE
            location = ParamManager.TEST_LOCATION?.name
            email = ParamManager.TEST_EMAIL
            wechat = ParamManager.TEST_WECHAT
        }
        viewBinding.apply {
            tvContentAppId.text = ParamManager.APP_ID
            tvContentAppSecret.text = ParamManager.APP_SECRET

            etUid.setText(userId)
            etAlias.setText(alias)
            etPhone.setText(phone)
            tvArea.text = location
            etEmail.setText(email)
            etWechat.setText(wechat)
        }
        viewBinding.tvArea.setOnClickListener {
            AreaPickerDialogFragment { data ->
                selectLocation = data
                viewBinding.tvArea.text = selectLocation?.name
            }.show(parentFragmentManager)
        }
    }

    /**
     * 设置参数缓存
     */
    fun saveParamCache() {
        val isUpdateUser = currentType == NetManager.getCurrentNet().netId
        viewBinding.apply {
            ParamManager.apply {
                if (currentType == TYPE_PRODUCT) {
                    USER_ID = etUid.text.toString().trim()
                    ALIAS = etAlias.text.toString().trim()
                    PHONE = etPhone.text.toString().trim()
                    LOCATION = selectLocation
                    EMAIL = etEmail.text.toString()
                    WECHAT = etWechat.text.toString()
                } else {
                    TEST_USER_ID = etUid.text.toString().trim()
                    TEST_ALIAS = etAlias.text.toString().trim()
                    TEST_PHONE = etPhone.text.toString().trim()
                    TEST_LOCATION = selectLocation
                    TEST_EMAIL = etEmail.text.toString()
                    TEST_WECHAT = etWechat.text.toString()
                }
            }
            NetManager.saveCurrentNet(currentType)
        }
        TipDialog.show("保存成功", WaitDialog.TYPE.SUCCESS)
        LiveEventBus.get<Boolean>("updateData").post(isUpdateUser)
    }
}