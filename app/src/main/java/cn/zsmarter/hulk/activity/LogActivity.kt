package cn.zsmarter.hulk.activity

import androidx.recyclerview.widget.LinearLayoutManager
import cn.zsmarter.hulk.R
import cn.zsmarter.hulk.base.BaseActivity
import cn.zsmarter.hulk.databinding.ActivityLogLayoutBinding
import cn.zsmarter.hulk.entity.LogEntity
import cn.zsmarter.hulk.manager.LogDataManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * Author: YP
 * Time: 2022/3/29
 * Describe: 日志界面
 */
class LogActivity : BaseActivity<ActivityLogLayoutBinding>(ActivityLogLayoutBinding::inflate) {

    private val adapter = LogAdapter()
    private var isFirst = true;

    override fun initView() {
        binding.iToolbar.title.text = getString(R.string.journal)
        binding.iToolbar.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.rvList.layoutManager = LinearLayoutManager(this)
        binding.rvList.adapter = adapter
        adapter.setList(LogDataManager.logData)
    }

    override fun observer() {
        super.observer()
        LogDataManager.newLogData.observe(this) {
            if (isFirst) {
                isFirst = false
                return@observe
            }
            // 初次进入页面时会走一遍该流程，取消第一次拿取结果
            adapter.addData(0, it)
            binding.rvList.scrollToPosition(0)
        }
    }

    class LogAdapter : BaseQuickAdapter<LogEntity, BaseViewHolder>(R.layout.adapter_log_layout) {

        override fun convert(holder: BaseViewHolder, item: LogEntity) {
            holder.setText(R.id.tv_content, "[${item.time}] ${item.content}")
        }

    }
}