package cn.zsmarter.hulk.manager

import cn.zsmarter.hulk.entity.CityEntity
import cn.zsmarter.hulk.utils.MoshiHelper
import com.blankj.utilcode.util.SPUtils
import java.util.*

/**
 * Author: YP
 * Time: 2022/3/29
 * Describe: 参数统一管理
 */
object ParamManager {
    var APP_ID: String = SPUtils.getInstance().getString("app_id", "hulk4015d")
        set(value) {
            field = value
            SPUtils.getInstance().put("app_id", field)
        }

    var APP_SECRET: String =
        SPUtils.getInstance().getString("app_secret", "4b15fbce858f42489b7d5f2d75062047")
        set(value) {
            field = value
            SPUtils.getInstance().put("app_secret", field)
        }

    var VERSION: String = SPUtils.getInstance().getString("version", "1.0.0.0")
        set(value) {
            field = value
            SPUtils.getInstance().put("version", field)
        }

    var USER_ID: String = SPUtils.getInstance().getString("uid")
        get() {
            if (field.isEmpty()) {
                field = "auid${getRandomStrForNumb()}"
                SPUtils.getInstance().put("uid", field)
            }
            return field
        }
        set(value) {
            field = value
            SPUtils.getInstance().put("uid", field)
        }

    var TEST_USER_ID: String = SPUtils.getInstance().getString("test_uid")
        get() {
            if (field.isEmpty()) {
                field = "auid${getRandomStrForNumb()}"
                SPUtils.getInstance().put("test_uid", field)
            }
            return field
        }
        set(value) {
            field = value
            SPUtils.getInstance().put("test_uid", field)
        }

    var ALIAS: String = SPUtils.getInstance().getString("alias")
        get() {
            if (field.isEmpty()) {
                field = "aalias${getRandomStrForNumb()}"
                SPUtils.getInstance().put("alias", field)
            }
            return field
        }
        set(value) {
            field = value
            SPUtils.getInstance().put("alias", field)
        }


    var TEST_ALIAS: String = SPUtils.getInstance().getString("test_alias")
        get() {
            if (field.isEmpty()) {
                field = "aalias${getRandomStrForNumb()}"
                SPUtils.getInstance().put("test_alias", field)
            }
            return field
        }
        set(value) {
            field = value
            SPUtils.getInstance().put("test_alias", field)
        }

    var PHONE: String? = SPUtils.getInstance().getString("phone", null)
        set(value) {
            field = value
            SPUtils.getInstance().put("phone", field)
        }

    var TEST_PHONE: String? = SPUtils.getInstance().getString("test_phone", null)
        set(value) {
            field = value
            SPUtils.getInstance().put("test_phone", field)
        }

    var LOCATION: CityEntity? = if (SPUtils.getInstance().getString("location").isNotEmpty()) {
        val json = SPUtils.getInstance().getString("location", "")
        MoshiHelper.fromJson(json)
    } else {
        null
    }
        set(value) {
            if (value == null) {
                return
            }
            field = value
            SPUtils.getInstance().put("location", MoshiHelper.toJson(field))
        }

    var TEST_LOCATION: CityEntity? = if (SPUtils.getInstance().getString("test_location").isNotEmpty()) {
        val json = SPUtils.getInstance().getString("test_location", "")
        MoshiHelper.fromJson(json)
    } else {
        null
    }
        set(value) {
            if (value == null) {
                return
            }
            field = value
            SPUtils.getInstance().put("test_location", MoshiHelper.toJson(field))
        }



    var EMAIL: String? = SPUtils.getInstance().getString("email", null)
        set(value) {
            field = value
            SPUtils.getInstance().put("email", field)
        }

    var TEST_EMAIL: String? = SPUtils.getInstance().getString("test_email", null)
        set(value) {
            field = value
            SPUtils.getInstance().put("test_email", field)
        }

    var WECHAT: String? = SPUtils.getInstance().getString("wechat", null)
        set(value) {
            field = value
            SPUtils.getInstance().put("wechat", field)
        }

    var TEST_WECHAT: String? = SPUtils.getInstance().getString("test_wechat", null)
        set(value) {
            field = value
            SPUtils.getInstance().put("test_wechat", field)
        }

    /**
     * 根据数量生成随机字符串
     */
    private fun getRandomStrForNumb(count: Int = 6): String {
        var result = ""
        if (count < 1) {
            return result
        }
         val random = Random()
        for (i in 1 until count) {
            result += random.nextInt(10)
        }
        return result
    }
}