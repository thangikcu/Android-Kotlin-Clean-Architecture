@file:Suppress("unused")

package com.development.clean.util.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.development.clean.util.debug.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun Exception.safeLog() {
    Timber.e(this)
}

inline fun <reified T : Any> Activity.launchActivity(
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivityForResult(intent, requestCode, options)
}

inline fun <reified T : Any> Context.launchActivity(
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivity(intent, options)
}

inline fun <reified T : Any> newIntent(context: Context): Intent =
    Intent(context, T::class.java)

fun TextView.underLine() {
    paint.flags = paint.flags or Paint.UNDERLINE_TEXT_FLAG
    paint.isAntiAlias = true
}

fun TextView.deleteLine() {
    paint.flags = paint.flags or Paint.STRIKE_THRU_TEXT_FLAG
    paint.isAntiAlias = true
}

fun TextView.bold() {
    paint.isFakeBoldText = true
    paint.isAntiAlias = true
}

fun TextView.setColorOfSubstring(substring: String, color: Int) {
    try {
        val spannable = SpannableString(text)
        val start = text.indexOf(substring)
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, color)),
            start,
            start + substring.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        text = spannable
    } catch (e: Exception) {
    }
}

fun TextView.font(font: String) {
    typeface = Typeface.createFromAsset(context.assets, "fonts/$font.ttf")
}

fun TextView.setDrawableLeft(drawable: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0)
}

fun Char.decimalValue(): Int {
    if (!isDigit())
        throw IllegalArgumentException("Out of range")
    return this.code - '0'.code
}

inline fun SpannableStringBuilder.withSpan(
    vararg spans: Any,
    action: SpannableStringBuilder.() -> Unit
):
    SpannableStringBuilder {
    val from = length
    action()

    for (span in spans) {
        setSpan(span, from, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    return this
}

fun Int.twoDigitTime() = if (this < 10) "0" + toString() else toString()

fun Editable.replaceAll(newValue: String) {
    replace(0, length, newValue)
}

fun Editable.replaceAllIgnoreFilters(newValue: String) {
    val currentFilters = filters
    filters = emptyArray()
    replaceAll(newValue)
    filters = currentFilters
}

fun Bitmap.toBase64(): String {
    var result = ""
    val baos = ByteArrayOutputStream()
    try {
        compress(Bitmap.CompressFormat.JPEG, 100, baos)
        baos.flush()
        baos.close()
        val bitmapBytes = baos.toByteArray()
        result = android.util.Base64.encodeToString(bitmapBytes, android.util.Base64.DEFAULT)
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            baos.flush()
            baos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return result
}

fun Bitmap.resize(w: Number, h: Number): Bitmap {
    val width = width
    val height = height
    val scaleWidth = w.toFloat() / width
    val scaleHeight = h.toFloat() / height
    val matrix = Matrix()
    matrix.postScale(scaleWidth, scaleHeight)
    if (width > 0 && height > 0) {
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }
    return this
}

fun Bitmap.saveFile(path: String) {
    val f = File(path)
    if (!f.exists()) {
        f.createNewFile()
    }
    val stream = FileOutputStream(f)
    compress(Bitmap.CompressFormat.PNG, 100, stream)
    stream.flush()
    stream.close()
}

val <T : Any> T.TAG: String
    get() {
        return this::class.simpleName!!
    }