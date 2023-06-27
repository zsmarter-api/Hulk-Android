package cn.zsmarter.hulk.activity

import androidx.core.view.isVisible
import cn.zsmarter.hulk.adapter.ViewPagerAdapter
import cn.zsmarter.hulk.base.BaseActivity
import cn.zsmarter.hulk.databinding.ActivitySettingLayoutBinding
import cn.zsmarter.hulk.fragment.SettingFragment
import cn.zsmarter.hulk.manager.NetManager
import cn.zsmarter.hulk.utils.initIndicator

/**
 * Author: YP
 * Time: 2022/3/29
 * Describe:设置界面
 */
class SettingActivity: BaseActivity<ActivitySettingLayoutBinding>(ActivitySettingLayoutBinding::inflate) {

    private val tabList = listOf(
        "生产环境",
        "测试环境"
    )

    override fun initView() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        val fragments = mutableListOf(
            SettingFragment.getInstance(SettingFragment.TYPE_PRODUCT)
        )
        if (NetManager.getCurrentAppType()) {
            fragments.add(
                SettingFragment.getInstance(SettingFragment.TYPE_TEST)
            )
        }
        binding.viewpager.adapter = ViewPagerAdapter(supportFragmentManager, fragments)
        if (NetManager.getCurrentAppType()) {
            binding.llTab.isVisible = true
            binding.magicIndicator.initIndicator(tabList, binding.viewpager, true)
            binding.viewpager.currentItem = NetManager.getCurrentNet().netId
        }
        binding.tvSave.setOnClickListener {
            fragments[binding.viewpager.currentItem].saveParamCache()
        }
    }
}