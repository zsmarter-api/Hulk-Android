package com.yu.baseandroid.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import cn.zsmarter.hulk.R
import com.blankj.utilcode.util.ScreenUtils
import java.lang.reflect.ParameterizedType

/**
 * Create By YP On 2022/4/28.
 * 用途：Dialog基类
 */
@Suppress("UNCHECKED_CAST")
abstract class BaseDialog<VB : ViewBinding> : DialogFragment() {

    protected lateinit var viewBinding: VB

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.CommonDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            //  ParameterizedType 表示参数化类型，带有类型参数的类型，即常说的泛型，如：List<T>、Map<Integer, String>、List<? extends Number>。
            val parameterizedType = javaClass.genericSuperclass as ParameterizedType
            val clazz = parameterizedType.actualTypeArguments[0] as Class<*>
            //通过反射加载类的inflate方法
            val declaredMethod = clazz.getDeclaredMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.java
            )
            //通过调用对应方法并强转成VB
            viewBinding = declaredMethod.invoke(null, inflater, container, false) as VB
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return viewBinding.root
    }

    override fun onStart() {
        super.onStart()
        // 设置弹窗的宽高
        dialog?.window?.setLayout(
            (ScreenUtils.getScreenWidth() * widthPercent()).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        initViews()
        initData()
    }

    /**
     * 屏幕宽度比例
     */
    open fun widthPercent() = 0.75f

    open fun initViews() {}
    open fun initData() {}
    open fun observe() {}

    /**
     * 显示弹窗
     */
    open fun show(fragmentManager: FragmentManager) {
        if (isAdded.not()) {
            kotlin.runCatching {
                show(fragmentManager, System.currentTimeMillis().toString())
            }
        }
    }
}