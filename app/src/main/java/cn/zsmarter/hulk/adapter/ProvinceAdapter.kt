package cn.zsmarter.hulk.adapter

import cn.zsmarter.hulk.R
import cn.zsmarter.hulk.base.BaseAdapter
import cn.zsmarter.hulk.databinding.AdapterAreaItemBinding
import cn.zsmarter.hulk.entity.ProvinceEntity

/**
 * Create By YP On 2022/6/14.
 * 用途：
 */
class ProvinceAdapter(private val itemClick: (ProvinceEntity) -> Unit):
    BaseAdapter<ProvinceEntity, AdapterAreaItemBinding>(R.layout.adapter_area_item) {

    override fun convert(viewBinding: AdapterAreaItemBinding, item: ProvinceEntity) {
        viewBinding.tvName.apply {
            text = item.name
            setOnClickListener {
                itemClick.invoke(item)
            }
        }
    }
}