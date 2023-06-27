package cn.zsmarter.hulk.adapter

import cn.zsmarter.hulk.R
import cn.zsmarter.hulk.base.BaseAdapter
import cn.zsmarter.hulk.databinding.AdapterAreaItemBinding
import cn.zsmarter.hulk.entity.CityEntity

/**
 * Create By YP On 2022/6/14.
 * 用途：
 */
class CityAdapter(private val itemClick: (CityEntity) -> Unit): BaseAdapter<CityEntity, AdapterAreaItemBinding>(R.layout.adapter_area_item) {
    override fun convert(viewBinding: AdapterAreaItemBinding, item: CityEntity) {
        viewBinding.tvName.apply {
            text = item.name
            setOnClickListener {
                itemClick.invoke(item)
            }
        }
    }
}