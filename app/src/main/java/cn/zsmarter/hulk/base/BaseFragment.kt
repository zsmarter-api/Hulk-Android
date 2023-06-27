package cn.zsmarter.hulk.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * Create By YP On 2022/4/28.
 * 用途：Fragment基类
 */
abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    protected lateinit var viewBinding: VB

    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        initViews()
        initData()
    }

    open fun observe() {}
    open fun initViews() {}
    open fun initData() {}
}