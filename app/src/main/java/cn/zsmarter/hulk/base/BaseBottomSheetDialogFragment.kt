package cn.zsmarter.hulk.base

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import cn.zsmarter.hulk.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetDialogFragment<VB : ViewBinding> : BottomSheetDialogFragment() {

    lateinit var viewBinding: VB
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CommonBottomSheetDialogFragmentTheme)
    }

    @Suppress("DEPRECATION")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // 去除BottomSheet默认的白色背景以实现圆角
        val bottomSheet: View = (dialog as BottomSheetDialog).delegate
            .findViewById(com.google.android.material.R.id.design_bottom_sheet)
            ?: return
        // 初始完全展开
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)
        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
        behavior.isDraggable = isDraggable()
        resetPeekHeight()?.let { behavior.peekHeight = it }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).also {
            (it as BottomSheetDialog).dismissWithAnimation = dismissWithAnimation()
        }
    }

    open fun dismissWithAnimation() = false

    open fun isDraggable() = true

    /**
     * 重新设置的peekHeight，折叠后的高度
     */
    open fun resetPeekHeight(): Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return viewBinding(inflater, container).also { viewBinding = it }.root
    }

    abstract fun viewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        initView()
        initData()
    }

    open fun observe() {}

    open fun initView() {}

    open fun initData() {}

    fun show(manager: FragmentManager) {
        if (this.isAdded.not()) {
            kotlin.runCatching { super.show(manager, this.javaClass.simpleName) }
        }
    }
}