package cn.zsmarter.hulk.dialog

import androidx.annotation.StringRes
import androidx.core.view.isGone
import androidx.fragment.app.FragmentManager
import cn.zsmarter.hulk.databinding.DialogCenterHintBinding
import com.yu.baseandroid.base.BaseDialog

/**
 * Create By YP On 2022/5/30.
 * 用途：中心提示弹窗
 */
class CenterHintDialog(private val fm: FragmentManager): BaseDialog<DialogCenterHintBinding>() {

    private var title: String? = null
    private var titleRes: Int = 0
    private var content: String? = null
    private var contentRes: Int = 0
    private var confirm: String? = null
    private var confirmRes: Int = 0
    private var cancel: String? = null
    private var cancelRes: Int = 0
    private var onConfirmClick: (() -> Unit)? = null
    private var onCancelClick: (() -> Unit)? = null


    override fun widthPercent() = 0.8f

    override fun initViews() {
        super.initViews()
        viewBinding.apply {
            when {
                title.isNullOrEmpty().not() -> {
                    tvTitle.isGone = false
                    tvTitle.text = title
                }
                titleRes != 0 -> {
                    tvTitle.isGone = false
                    tvTitle.text = getString(titleRes)
                }
                else -> {
                    tvTitle.isGone = true
                }
            }
            when {
                content.isNullOrEmpty().not() -> {
                    tvContent.isGone = false
                    tvContent.text = content
                }
                contentRes != 0 -> {
                    tvContent.isGone = false
                    tvContent.text = getString(contentRes)
                }
                else -> {
                    tvContent.isGone = true
                }
            }
            when {
                cancel.isNullOrEmpty().not() -> {
                    tvCancel.isGone = false
                    tvCancel.text = cancel
                }
                cancelRes != 0 -> {
                    tvCancel.isGone = false
                    tvCancel.text = getString(cancelRes)
                }
                else -> {
                    tvCancel.isGone = true
                    vBottomCenterLine.isGone = true
                }
            }
            when {
                confirm.isNullOrEmpty().not() -> {
                    tvConfirm.isGone = false
                    tvConfirm.text = confirm
                }
                confirmRes != 0 -> {
                    tvConfirm.isGone = false
                    tvConfirm.text = getString(confirmRes)
                }
                else -> {
                    tvConfirm.isGone = true
                    vBottomCenterLine.isGone = true
                }
            }
            tvConfirm.setOnClickListener {
                onConfirmClick?.invoke()
                dismissAllowingStateLoss()
            }
            tvCancel.setOnClickListener {
                onCancelClick?.invoke()
                dismissAllowingStateLoss()
            }
        }
        isCancelable = false
    }

    fun show() {
        show(fm)
    }

    class Build(private val fm: FragmentManager) {

        private var title: String? = null
        private var titleRes: Int = 0
        private var content: String? = null
        private var contentRes: Int = 0
        private var confirm: String? = null
        private var confirmRes: Int = 0
        private var cancel: String? = null
        private var cancelRes: Int = 0
        private var onConfirmClick: (() -> Unit)? = null
        private var onCancelClick: (() -> Unit)? = null

        fun setTitle(title: String): Build {
            this.title = title
            return this
        }

        fun setTitleRes(@StringRes titleRes: Int): Build {
            this.titleRes = titleRes
            return this
        }

        fun setContent(content: String): Build {
            this.content = content
            return this
        }

        fun setContentRes(@StringRes contentRes: Int): Build {
            this.contentRes = contentRes
            return this
        }

        fun setConfirm(confirm: String): Build {
            this.confirm = confirm
            return this
        }

        fun setConfirmRes(@StringRes confirmRes: Int): Build {
            this.confirmRes = confirmRes
            return this
        }

        fun setCancel(cancel: String): Build {
            this.cancel = cancel
            return this
        }

        fun setCancelRes(@StringRes cancelRes:Int): Build {
            this.cancelRes = cancelRes
            return this
        }

        fun setOnConfirmClick(onConfirmClick: () -> Unit): Build {
            this.onConfirmClick = onConfirmClick
            return this
        }

        fun setOnCancelClick(onCancelClick: () -> Unit): Build {
            this.onCancelClick = onCancelClick
            return this
        }

        fun build() = CenterHintDialog(fm).also {
            it.title = title
            it.titleRes = titleRes
            it.content = content
            it.contentRes = contentRes
            it.cancel = cancel
            it.cancelRes = cancelRes
            it.confirm = confirm
            it.confirmRes = confirmRes
            it.onCancelClick = onCancelClick
            it.onConfirmClick = onConfirmClick
        }
    }
}