package cn.zsmarter.hulk.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import cn.zsmarter.hulk.R
import com.blankj.utilcode.util.BarUtils
import com.gyf.immersionbar.ktx.immersionBar

/**
 * Author: YP
 * Time: 2022/3/29
 * Describe:
 */
abstract class BaseActivity<VB : ViewBinding>(open val block: (LayoutInflater) -> VB) :
    AppCompatActivity() {

    protected val binding by lazy {
        block(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (isFull()) {
            immersionBar {}
            setTopBarMarginTop()?.let { BarUtils.addMarginTopEqualStatusBarHeight(it) }
        } else {
            immersionBar {
                fitsSystemWindows(true)
                statusBarColor(R.color.white)
                statusBarDarkFont(true)
            }
        }
        initView()
        initData()
        observer()
    }

    open fun initData() {}

    open fun observer() {}

    abstract fun initView()

    open fun isFull() = false

    /**
     * 如果是全屏显示，需要设置顶部状态栏距离的调用此方法
     */
    open fun setTopBarMarginTop(): View? = null
}