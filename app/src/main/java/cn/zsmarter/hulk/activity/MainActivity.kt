package cn.zsmarter.hulk.activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import cn.zsmarter.hulk.BuildConfig
import cn.zsmarter.hulk.R
import cn.zsmarter.hulk.base.BaseActivity
import cn.zsmarter.hulk.databinding.ActivityMainLayoutBinding
import cn.zsmarter.hulk.dialog.CenterHintDialog
import cn.zsmarter.hulk.entity.LogEntity
import cn.zsmarter.hulk.entity.MsgEntity
import cn.zsmarter.hulk.manager.LogDataManager
import cn.zsmarter.hulk.manager.NetManager
import cn.zsmarter.hulk.manager.ParamManager
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jeremyliao.liveeventbus.LiveEventBus
import com.zsmarter.hulk.Configuration
import com.zsmarter.hulk.Hulk
import com.zsmarter.hulk.callback.ActionLogsCallback
import com.zsmarter.hulk.callback.ManufacturersCallback
import com.zsmarter.hulk.callback.NotificationClick
import com.zsmarter.hulk.huawei_push.HuaWeiPush
import com.zsmarter.hulk.meizu_push.MeiZuPush
import com.zsmarter.hulk.notification.NotificationEntity
import com.zsmarter.hulk.oppo_push.OPPOPush
import com.zsmarter.hulk.push.utils.DeviceUtils
import com.zsmarter.hulk.socket.*
import com.zsmarter.hulk.vivo_push.VivoPush
import com.zsmarter.hulk.xiaomi_push.XiaoMiPush
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

/**
 * Author: YP
 * Time: 2022/3/29
 * Describe:
 */
class MainActivity : BaseActivity<ActivityMainLayoutBinding>(ActivityMainLayoutBinding::inflate) {

    private var isOpen = false
    private var rotateAnimator: ObjectAnimator? = null
    private var clickTime = 0L
    private var backTime = 0L

    override fun isFull() = true

    private var isClickCancel = false
    private var isShowingDialog = false

    override fun setTopBarMarginTop() = binding.ivLogo

    private var initCallBack: ((isSuccess: Boolean) -> Unit)? = null
    private val adapter = MainMsgAdapter()

    @SuppressLint("SetTextI18n")
    override fun initView() {
        if (NetManager.getCurrentAppType()) {
            binding.tvEnvironment.isVisible = true
            binding.tvEnvironment.text = NetManager.getCurrentNet().netName
        }
        binding.ivSetting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        binding.ivLog.setOnClickListener {
            startActivity(Intent(this, LogActivity::class.java))
        }
        binding.tvClear.setOnClickListener {
            adapter.setList(null)
        }
        binding.tvUid.text = "Uid：${
            if (NetManager.getCurrentNet().netId == 0) {
                ParamManager.USER_ID
            } else {
                ParamManager.TEST_USER_ID
            }
        }"
        binding.ivCopy.setOnClickListener {
            if (NetManager.isProduct()) {
                ClipboardUtils.copyText(ParamManager.USER_ID)
            } else {
                ClipboardUtils.copyText(ParamManager.TEST_USER_ID)
            }
            ToastUtils.showShort("复制成功")
        }
        binding.ivSwitch.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            val diff = currentTime - clickTime
            if (diff < 1000) {
                ToastUtils.showShort("请不要点击过于频繁")
                return@setOnClickListener
            } else {
                clickTime = currentTime
            }
            if (isOpen) {
                stopAnimation()
            } else {
                startAnimation()
            }
        }
        binding.rvList.layoutManager = LinearLayoutManager(this)
        binding.rvList.adapter = adapter

        startAnimation()
    }

    private fun startAnimation() {
        initPush {
            if (it) {
                binding.ivRotate.visibility = View.VISIBLE
                binding.tvRunning.visibility = View.VISIBLE
                binding.ivRoundBg.setBackgroundResource(R.drawable.shape_white_round_bg)
                if (rotateAnimator == null) {
                    rotateAnimator =
                        ObjectAnimator.ofFloat(binding.ivRotate, "rotation", 0f, 359f).apply {
                            repeatCount = ValueAnimator.INFINITE
                            duration = 3000
                            interpolator = LinearInterpolator()
                        }
                }
                rotateAnimator?.start()
                isOpen = true
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun observer() {
        super.observer()
        LiveEventBus.get<Boolean>("updateData").observe(this) {
            binding.tvEnvironment.text = NetManager.getCurrentNet().netName
            binding.tvUid.text = "Uid：${
                if (NetManager.getCurrentNet().netId == 0) {
                    ParamManager.USER_ID
                } else {
                    ParamManager.TEST_USER_ID
                }
            }"
            if (Hulk.isConnected().not()) {
                return@observe
            }
            if (it) {
                updateUserInfo()
            } else {
                stopAnimation()
                startAnimation()
            }
        }
    }

    private fun stopAnimation() {
        binding.ivRotate.visibility = View.GONE
        binding.tvRunning.visibility = View.GONE
        binding.ivRoundBg.setBackgroundResource(R.drawable.shape_blue_round_bg)
        rotateAnimator?.cancel()
        Hulk.disconnect()
        isOpen = false
    }

    private fun initPush(callBack: ((isSuccess: Boolean) -> Unit)? = null) {
        initCallBack = callBack
        val host = NetManager.getCurrentNet().host
        val port = NetManager.getCurrentNet().port.toInt()
        val appId = ParamManager.APP_ID
        val appSecret = ParamManager.APP_SECRET
//        val host = "47.108.95.11"
//        val port = 28888
//        val appId = "hulk01973k"
//        val appSecret = "cc1a5b418bad4670b5ec1300893b9899"
        val version = ParamManager.VERSION

        val configuration = Configuration(
            hulkHost = host,
            hulkPort = port,
            hulkDeviceInfo = HulkDeviceInfo(
                appKey = appId,
                appSecret = appSecret,
                appVersion = version,
            ),
            hulkIsSSL = false,
            printEnable = BuildConfig.DEBUG,
            notificationIcon = R.drawable.hulk_logo_blue,
            hulkHeartbeat = 55000
        )
        // 操作日志打印
        Hulk.actionLogsCallBack = object : ActionLogsCallback {
            override fun actionLog(type: String, content: String) {
                addLogData("$type:$content")
            }
        }
        // 厂商初始化回调
        Hulk.manufacturersCallback = object : ManufacturersCallback {
            override fun initManufacturers(type: String, token: String) {
                addLogData("$type token=$token")
            }

        }
        // 通知栏消息点击
        Hulk.notificationClick = object : NotificationClick {
            override fun click(msgContent: String) {
                // 自定义跳转Activity等等操作
            }
        }

        Hulk.init(application, configuration, object : HulkMsgListener() {
            override fun onToken(tid: String?) {
                if (tid == null) {
                    addItemToList(MsgEntity("初始化失败"))
                    addLogData("推送初始化失败，未获取到tid")
                    initCallBack?.invoke(false)
                } else {
                    addItemToList(MsgEntity("初始化成功", tid))
                    addLogData("推送初始化成功：tid=$tid")
                    Hulk.onLauncherActivityIntent(intent)
                    updateUserInfo()
                    initCallBack?.invoke(true)
                }
            }

            override fun onNotificationOpen(isOpen: Boolean) {
                if (isShowingDialog || isClickCancel) {
                    return
                }
                if (isOpen.not()) {
                    isShowingDialog = true
                    CenterHintDialog.Build(supportFragmentManager)
                        .setCancel("取消")
                        .setConfirm("去设置")
                        .setContent("打开推送获得实时消息和\n推送通知")
                        .setTitle("温馨提示")
                        .setOnCancelClick {
                            isShowingDialog = false
                            isClickCancel = true
                        }
                        .setOnConfirmClick {
                            isShowingDialog = false
                            isClickCancel = true
                            DeviceUtils.openNotificationSetting(this@MainActivity)
                        }
                        .build()
                        .show()
                }
            }

            override fun onTranceMsg(str: String) {
                addItemToList(MsgEntity("透传消息", "接收透传消息：$str"))
                addLogData("收到透传推送：$str")
            }

            override fun onMessage(data: NotificationEntity) {
                addItemToList(MsgEntity(data.title ?: "", data.alert, id = data.m_id))
                addLogData("收到通知栏推送：$data")
            }

            override fun onError(code: Int, msg: String) {
                addItemToList(MsgEntity("推送初始化错误", "code:$code msg:$msg"))
                addLogData(msg)
            }

            override fun onWithdrawn(msgId: Long) {
                addItemToList(isWithDrawn = true, msgId = msgId)
                addLogData("msg_id=$msgId 的消息已被撤回")
            }
        })

        OPPOPush.init(
            application,
            "bf56f9ce1aa5476b81255a26e2ba21e8",
            "61b0d7f657784b21972720dcfa5c82ec"
        )
        HuaWeiPush.init(application)
        MeiZuPush.init(application, "149589", "203f99b6eeef47288b943c6824e5dab1")
        XiaoMiPush.init(application, "2882303761518659656", "5921865996656")
        // 银行项目
//        XiaoMiPush.init(application, "2882303761517944829", "5201794434829")
        VivoPush.init(application)
//        FCMPush.init()
    }

    private fun addItemToList(msg: MsgEntity? = null, isWithDrawn: Boolean = false, msgId: Long? = null) {
        lifecycleScope.launch(Dispatchers.Main) {
            if (isWithDrawn.not()) {
                if (msg == null) return@launch
                binding.clEmptyView.visibility = View.GONE
                adapter.addData(0, msg)
                binding.rvList.scrollToPosition(0)
            } else {
                val data = adapter.data.find { it.id == msgId } ?: return@launch
                adapter.remove(data)
                adapter.addData(0, MsgEntity("消息已被撤回"))
            }
        }
    }

    private fun addLogData(msg: String) {
        LogDataManager.addData(
            LogEntity(
                TimeUtils.getNowString(TimeUtils.getSafeDateFormat("HH:mm")),
                msg
            )
        )
    }

    override fun onBackPressed() {
        exitApp()
    }

    private fun updateUserInfo() {
        val userId: String?
        val alias: String?
        val phone: String?
        val location: String?
        val email: String?
        val wechat: String?
        if (NetManager.getCurrentNet().netId == 0) {
            userId = ParamManager.USER_ID
            alias = ParamManager.ALIAS
            phone = ParamManager.PHONE
            location = ParamManager.LOCATION?.code
            email = ParamManager.EMAIL
            wechat = ParamManager.WECHAT
        } else {
            userId = ParamManager.TEST_USER_ID
            alias = ParamManager.TEST_ALIAS
            phone = ParamManager.TEST_PHONE
            location = ParamManager.TEST_LOCATION?.code
            email = ParamManager.TEST_EMAIL
            wechat = ParamManager.TEST_WECHAT
        }
        val hulkUserInfo = HulkUserInfo(
            userId = userId,
            alias = alias,
            phone = phone,
            location = location
        ).apply {
            val channelList = mutableListOf<ThirdParty>()
            if (email != null) {
                channelList.add(ThirdParty(ChannelType.email, email))
            }
            if (wechat != null) {
                channelList.add(ThirdParty(ChannelType.wechat, wechat))
            }
            thirdParty = channelList
        }
        Hulk.updateUserInfo(hulkUserInfo)
    }

    private fun exitApp() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backTime > 2000) {
            ToastUtils.showShort("再次返回退出程序")
            backTime = currentTime
        } else {
            finish()
            exitProcess(0)
        }
    }

    class MainMsgAdapter :
        BaseQuickAdapter<MsgEntity, BaseViewHolder>(R.layout.adapter_msg_layout) {

        override fun convert(holder: BaseViewHolder, item: MsgEntity) {
            item.content?.let {
                holder.getView<TextView>(R.id.tv_content).visibility = View.VISIBLE
                holder.setText(R.id.tv_content, it)
            }
            holder.setText(R.id.tv_title, item.title)
            holder.setText(R.id.tv_time, item.time)
        }

    }
}