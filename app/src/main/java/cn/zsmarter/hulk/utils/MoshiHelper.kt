package cn.zsmarter.hulk.utils

import android.net.Uri
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * moshi json 解析
 */
object MoshiHelper {

    /**json解析moshi对象**/
    val moshi: Moshi = Moshi.Builder()
        .add(UriAdapter())
        .addLast(KotlinJsonAdapterFactory())
        .build()

    /**
     *  json string 解析成对象，可能为null
     *  type不传默认为T::class.java，只能是单层泛型
     *  @param [json] json string
     *  @param [type] 类型
     */
    inline fun <reified T> fromJson(json: String, type: Type = getType<T>()): T? {
        return moshi.adapter<T>(type).fromJson(json)
    }

    /**
     * 将对象转换为json string
     * @param [t] 实体
     * @param [type] 类型
     */
    inline fun <reified T> toJson(t: T, type: Type = getType<T>()): String {
        return moshi.adapter<T>(type).toJson(t)
    }

    /**
     * 以rwaType新建泛型类型，比如rowType为X，则新建泛型为X<T>
     * @param [rwaType] 主类型，T为泛型类型
     */
    inline fun <reified T> newParameterizedType(rwaType: Type): Type {
        return Types.newParameterizedType(rwaType, getType<T>())
    }

    /**
     * 获取指定泛型T的Type
     */
    inline fun <reified T> getType(): Type {
        return (object : TypeToken<T>() {}).type
    }

    /**
     * 泛型类型获取，根据指定的泛型类型T，获取对应的ParameterizedType
     * 比如: (object : MoshiHelper.TypeToken<ApiResult<List<Map<String,Int>>>>() {}).type
     */
    abstract class TypeToken<T> {

        /**
         * 泛型T对应的Type
         */
        var type: Type

        init {
            val parameterizedType = this.javaClass.genericSuperclass
                    as ParameterizedType
            type = parameterizedType.actualTypeArguments[0]
        }
    }
}

class UriAdapter {
    @ToJson
    fun toJson(uri: Uri) = uri.toString()

    @FromJson
    fun fromJson(uriString: String): Uri = Uri.parse(uriString)
}