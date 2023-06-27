package cn.zsmarter.hulk.activity

import android.content.Intent
import android.os.Bundle
import cn.zsmarter.hulk.base.BaseActivity
import cn.zsmarter.hulk.databinding.ActivityInitSetBinding
import cn.zsmarter.hulk.manager.NetManager
import cn.zsmarter.hulk.manager.ParamManager
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.ToastUtils

/**
 * Author: YP
 * Time: 2022/6/7
 * Describe:初始设置手机号码界面
 */
class InitSetActivity: BaseActivity<ActivityInitSetBinding>(ActivityInitSetBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isTaskRoot && intent.action != null) {
            finish()
            return
        }
    }

    override fun isFull() = true

    override fun setTopBarMarginTop() = binding.ivLogo

    override fun initView() {
        val isHasPhone = if (NetManager.isProduct()) {
            ParamManager.PHONE.isNullOrEmpty().not()
        } else {
            ParamManager.TEST_PHONE.isNullOrEmpty().not()
        }
        if (isHasPhone) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding.tvComplete.setOnClickListener {
            if (binding.etPhone.text.isNullOrEmpty()) {
                ToastUtils.showShort("请先输入手机号")
                return@setOnClickListener
            }
            val phone = binding.etPhone.text.toString().trim()
            if (RegexUtils.isMobileSimple(phone).not()) {
                ToastUtils.showShort("请输入正确的手机号码")
                return@setOnClickListener
            }
            if (NetManager.isProduct()) {
                ParamManager.PHONE = phone
            } else {
                ParamManager.TEST_PHONE = phone
            }
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding.tvSkip.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}