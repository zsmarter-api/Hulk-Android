package cn.zsmarter.hulk.utils

import android.content.Context
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.viewpager.widget.ViewPager
import cn.zsmarter.hulk.R
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ConvertUtils
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView

/**
 * Create By YP On 2022/5/12.
 * 用途：指示器便捷函数
 */

/**
 *
 * @param tabList 展示的tab文字内容
 * @param vp 绑定的ViewPager
 */
fun MagicIndicator.initIndicator(
    tabList: List<String>,
    vp: ViewPager,
    isAdjustMode: Boolean = false
) {
    setBackgroundColor(ColorUtils.getColor(R.color.white))
    val commonNavigator = CommonNavigator(rootView.context).apply {
        this.isAdjustMode = isAdjustMode
        adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return tabList.size
            }

            override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                val titleView = SimplePagerTitleView(context).apply {
                    text = tabList[index]
                    normalColor = ColorUtils.getColor(R.color.gray_717787)
                    selectedColor = ColorUtils.getColor(R.color.black_293040)
                    val padding = ConvertUtils.dp2px(12f)
                    setPadding(padding, padding, padding, padding)
                    textSize = 16f
                    setOnClickListener {
                        vp.currentItem = index
                    }
                }
                return titleView
            }

            override fun getIndicator(context: Context?): IPagerIndicator {
                val indicator = LinePagerIndicator(context).apply {
                    mode = LinePagerIndicator.MODE_EXACTLY
                    lineHeight = ConvertUtils.dp2px(3f).toFloat()
                    lineWidth = ConvertUtils.dp2px(45f).toFloat()
//                    roundRadius = ConvertUtils.dp2px(1f).toFloat()
                    startInterpolator = AccelerateInterpolator()
                    endInterpolator = DecelerateInterpolator(2f)
                    setColors(ColorUtils.getColor(R.color.blue_477BEF))
                }
                return indicator
            }

        }
    }
    navigator = commonNavigator
    ViewPagerHelper.bind(this, vp)
}