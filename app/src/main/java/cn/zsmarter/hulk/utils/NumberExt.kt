package cn.zsmarter.hulk.utils


/**
 * dp、sp、px相互换算
 */
private fun Number?.dpToPx() = (this?.toFloat() ?: 0f) * density
private fun Number?.spToPx() = (this?.toFloat() ?: 0f) * scaledDensity
fun Number?.pxToDp() = (this?.toFloat() ?: 0f) / density
fun Number?.pxToSp() = (this?.toFloat() ?: 0f) / scaledDensity
val Number?.dp: Int
    get() = ((this?.toFloat() ?: 0f) * density).toInt()
val Number?.sp: Int
    get() = ((this?.toFloat() ?: 0f) * scaledDensity).toInt()

val Number?.isTrue: Boolean
    get() = this != null && this != 0
val Number?.isFalse: Boolean
    get() = this.isTrue.not()


fun Number?.isNullOrZero() = this == null || this == 0
