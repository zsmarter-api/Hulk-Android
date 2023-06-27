package com.zsmarter.hulk.util

import android.graphics.Bitmap
import android.graphics.Matrix
import com.bumptech.glide.Glide
import com.zsmarter.hulk.push.utils.DeviceUtils
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.min

/**
 * Create By YP On 2022/9/5.
 * 用途：
 */
internal object LoadImageUtil {

    suspend fun bgLoadImage(url: String): Bitmap = suspendCoroutine {

        val futureTarget = Glide.with(DeviceUtils.getApplication())
            .asBitmap()
            .load(url)
            .submit()
        val bitmap = futureTarget.get()
        Glide.with(DeviceUtils.getApplication()).clear(futureTarget)
        it.resume(scaleImg(bitmap))
    }

    private fun scaleImg(bitmap: Bitmap): Bitmap {
        val application = DeviceUtils.getApplication()
        val width =
            application.resources.getDimensionPixelOffset(android.R.dimen.notification_large_icon_width)
        val height =
            application.resources.getDimensionPixelOffset(android.R.dimen.notification_large_icon_height)
        val bmpW = bitmap.width
        val bmpH = bitmap.height
        val bitmapParams  = if (bmpW > width || bmpH > height) {
            scaleImgMax(bitmap, width, height)
        } else if (bmpW < width && bmpH < height) {
            scaleImgMin(bitmap, width, height)
        } else {
            bitmap
        }
        return bitmapParams
    }

    /**
     * 图片缩小
     */
    private fun scaleImgMax(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        val bmpW = bitmap.width
        val bmpH = bitmap.height
        val scaleW = width.toFloat() / bmpW.toFloat()
        val scaleH = height.toFloat() / bmpH.toFloat()
        // 去宽高最大比例来缩放图片
//        val min = min(scaleW, scaleH)
        val matrix = Matrix()
        matrix.postScale(scaleW, scaleH)
        return Bitmap.createBitmap(bitmap, 0, 0, bmpW, bmpH, matrix, true)
    }

    private fun scaleImgMin(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        val bmpW = bitmap.width
        val bmpH = bitmap.height
        val scaleW = width.toFloat() / bmpW.toFloat()
        val scaleH = height.toFloat() / bmpH.toFloat()
        // 去宽高最大比例来拉伸图片
        val matrix = Matrix()
        matrix.postScale(scaleW, scaleH)
        return Bitmap.createBitmap(bitmap, 0, 0, bmpW, bmpH, matrix, true)
    }
}