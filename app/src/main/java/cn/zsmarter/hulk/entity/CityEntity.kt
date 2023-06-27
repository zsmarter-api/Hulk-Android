package cn.zsmarter.hulk.entity

import androidx.annotation.Keep

/**
 * Create By YP On 2022/6/13.
 * 用途：
 */
@Keep
data class ProvinceEntity(
    val name: String,
    val code: String,
    var city: MutableList<CityEntity> = mutableListOf()
)

@Keep
data class CityEntity(
    val name: String,
    val code: String
)