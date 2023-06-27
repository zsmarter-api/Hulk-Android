package cn.zsmarter.hulk.dialog

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import cn.zsmarter.hulk.R
import cn.zsmarter.hulk.adapter.CityAdapter
import cn.zsmarter.hulk.adapter.ProvinceAdapter
import cn.zsmarter.hulk.base.BaseBottomSheetDialogFragment
import cn.zsmarter.hulk.databinding.AreaPickerDialogFragmentBinding
import cn.zsmarter.hulk.entity.CityEntity
import cn.zsmarter.hulk.entity.ProvinceEntity
import cn.zsmarter.hulk.utils.MoshiHelper
import cn.zsmarter.hulk.utils.dp
import com.blankj.utilcode.util.ColorUtils

/**
 * 省市区选择器
 */
@Suppress("BlockingMethodInNonBlockingContext")
class AreaPickerDialogFragment(result:(CityEntity) -> Unit) :
    BaseBottomSheetDialogFragment<AreaPickerDialogFragmentBinding>() {

    private var currentTab = 1
    private val provinceAdapter = ProvinceAdapter {
        cityAdapter.setList(it.city)
        viewBinding.tvCity.isVisible = true
        viewBinding.tvProvince.text = it.name
        switchTab()
    }
    private val cityAdapter = CityAdapter {
        result.invoke(it)
        dismissAllowingStateLoss()
    }

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = AreaPickerDialogFragmentBinding.inflate(inflater, container, false)

    override fun initView() {
        super.initView()
        viewBinding.tvProvince.setOnClickListener {
            if (currentTab == 1) {
                return@setOnClickListener
            }
            switchTab()
        }
        viewBinding.tvCity.setOnClickListener {
            if (currentTab == 2) {
                return@setOnClickListener
            }
            switchTab()
        }
        viewBinding.ivClose.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun switchTab() {
        val familyName = "sans-serif-medium"
        val normalTypeface = Typeface.create("", Typeface.NORMAL);
        val mediumTypeface = Typeface.create(familyName, Typeface.NORMAL);
        if (currentTab == 1) {
            viewBinding.tvCity.setCompoundDrawablesRelativeWithIntrinsicBounds(0 ,0, 0, R.drawable.shape_bottom_line)
            viewBinding.tvProvince.setCompoundDrawablesRelativeWithIntrinsicBounds(0 ,0, 0, R.drawable.shape_bottom_invisible_line)
            viewBinding.tvCity.setTextColor(ColorUtils.getColor(R.color.black_293040))
            viewBinding.tvProvince.setTextColor(ColorUtils.getColor(R.color.gray_717787))
            viewBinding.tvCity.typeface = mediumTypeface
            viewBinding.tvProvince.typeface = normalTypeface
            viewBinding.rvProvince.isVisible = false
            viewBinding.rvCity.isVisible = true
            currentTab = 2
        } else {
            viewBinding.tvCity.setCompoundDrawablesRelativeWithIntrinsicBounds(0 ,0, 0, R.drawable.shape_bottom_invisible_line)
            viewBinding.tvProvince.setCompoundDrawablesRelativeWithIntrinsicBounds(0 ,0, 0, R.drawable.shape_bottom_line)
            viewBinding.tvProvince.setTextColor(ColorUtils.getColor(R.color.black_293040))
            viewBinding.tvCity.setTextColor(ColorUtils.getColor(R.color.gray_717787))
            viewBinding.tvCity.typeface = normalTypeface
            viewBinding.tvProvince.typeface = mediumTypeface
            viewBinding.rvProvince.isVisible = true
            viewBinding.rvCity.isVisible = false
            currentTab = 1
        }
    }

    override fun initData() {
        super.initData()
        val provinceJson = resources.assets
            .open("area.json")
            .use { it.readBytes() }
            .toString(Charsets.UTF_8)
        val provinceList = MoshiHelper.fromJson<List<ProvinceEntity>>(provinceJson)

        viewBinding.rvProvince.adapter = provinceAdapter
        viewBinding.rvCity.adapter = cityAdapter
        provinceAdapter.setList(provinceList)

    }
}