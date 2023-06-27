package com.zsmarter.hulk.notification

import androidx.annotation.Keep

/**
 * Create By YP On 2022/7/11.
 * 用途：
 */
@Keep
data class WithdrawEntity(
    val title: String,
    val m_id: Long,
    val task_id: Long
)
