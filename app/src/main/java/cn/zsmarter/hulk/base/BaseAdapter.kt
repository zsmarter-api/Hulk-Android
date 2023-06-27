package cn.zsmarter.hulk.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import java.lang.reflect.ParameterizedType

/**
 * Create By YP On 2022/5/30.
 * 用途：RecyclerView基类
 */
abstract class BaseAdapter<T, VB : ViewBinding>(@LayoutRes resId: Int) :
    BaseQuickAdapter<T, ViewBindingViewHolder<VB>>(resId) {

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewBindingViewHolder<VB> {
        //  ParameterizedType 表示参数化类型，带有类型参数的类型，即常说的泛型，如：List<T>、Map<Integer, String>、List<? extends Number>。
        val parameterizedType = javaClass.genericSuperclass as ParameterizedType
        val clazz = parameterizedType.actualTypeArguments[1] as Class<*>
        //通过反射加载类的inflate方法
        val declaredMethod = clazz.getDeclaredMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        //通过调用对应方法并强转成VB
        val viewBinding =
            declaredMethod.invoke(null, LayoutInflater.from(parent.context), parent, false) as VB
        return ViewBindingViewHolder(viewBinding)
    }

    override fun convert(holder: ViewBindingViewHolder<VB>, item: T) {
        convert(holder.viewBinding, item)
    }

    abstract fun convert(viewBinding: VB, item: T)
}


class ViewBindingViewHolder<T : ViewBinding>(val viewBinding: T) : BaseViewHolder(viewBinding.root)