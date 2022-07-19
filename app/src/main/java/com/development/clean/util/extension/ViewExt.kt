@file:Suppress("unused")

package com.development.clean.util.extension

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.annotation.Px
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

fun View.fadeOut(fadeDuration: Long = 300, endAlpha: Float = 0f) {
    ValueAnimator.ofFloat(1f, endAlpha).apply {
        duration = fadeDuration
        addUpdateListener {
            val animatedValue = it.animatedValue as Float
            alpha = animatedValue
        }
    }.start()
}

fun View.fadeIn(fadeDuration: Long = 300) {
    ValueAnimator.ofFloat(0f, 1f).apply {
        duration = fadeDuration
        addUpdateListener {
            val animatedValue = it.animatedValue as Float
            alpha = animatedValue
        }
    }.start()
}

fun View.setPadding(@Px size: Int) {
    setPadding(size, size, size, size)
}

fun View.adjustToCompatibleWithStatusBar(usePadding: Boolean = true) {

    val statusBarHeight: Int

    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")

    statusBarHeight = if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0

    if (statusBarHeight > 0) {
        if (usePadding) {
            updatePadding(top = paddingTop + statusBarHeight)
        } else {
            updateMargin(top = marginTop + statusBarHeight)
        }
    }
}

fun View.getString(stringResId: Int): String = resources.getString(stringResId)

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    this.requestFocus()
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

/**
 * Try to hide the keyboard and returns whether it worked
 * https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
 */
fun View.hideKeyboard(): Boolean {
    try {
        clearFocus()
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return inputMethodManager.hideSoftInputFromWindow(
            windowToken,
            InputMethodManager.RESULT_UNCHANGED_SHOWN
        )
    } catch (ignored: RuntimeException) {
    }
    return false
}

fun View.updateMargin(
    @Px left: Int = marginLeft,
    @Px top: Int = marginTop,
    @Px right: Int = marginRight,
    @Px bottom: Int = marginBottom
): Boolean {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val lp = layoutParams as ViewGroup.MarginLayoutParams
        lp.setMargins(left, top, right, bottom)
        layoutParams = lp
        return true
    }
    return false
}

fun View.setHeight(value: Int) {
    val lp = layoutParams
    lp?.let {
        lp.height = value
        layoutParams = lp
    }
}

fun View.setWidth(value: Int) {
    val lp = layoutParams
    lp?.let {
        lp.width = value
        layoutParams = lp
    }
}

fun View.resize(width: Int, height: Int) {
    val lp = layoutParams
    lp?.let {
        lp.width = width
        lp.height = height
        layoutParams = lp
    }
}

fun ViewGroup.hideAllChild() {
    children.forEach {
        it.isVisible = false
    }
}

fun <T : ViewDataBinding> ViewGroup.bind(layoutId: Int): T {
    return DataBindingUtil.inflate(layoutInflater, layoutId, this, false)
}

val View.layoutInflater: LayoutInflater
    get() {
        return LayoutInflater.from(context)
    }

fun ViewGroup.inflateLayout(
    @LayoutRes layoutId: Int,
    attachToRoot: Boolean = true
): View {
    return context.inflateLayout(layoutId, this, attachToRoot)
}

@Deprecated("use View.drawToBitmap()")
fun View.toBitmap(scale: Float = 1f, config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap? {
    if (this is ImageView) {
        if (drawable is BitmapDrawable) return (drawable as BitmapDrawable).bitmap
    }
    this.clearFocus()
    val bitmap = createBitmapSafely((width * scale).toInt(), (height * scale).toInt(), config, 1)
    if (bitmap != null) {
        Canvas().run {
            setBitmap(bitmap)
            save()
            drawColor(Color.WHITE)
            scale(scale, scale)
            this@toBitmap.draw(this)
            restore()
            setBitmap(null)
        }
    }
    return bitmap
}

fun createBitmapSafely(width: Int, height: Int, config: Bitmap.Config, retryCount: Int): Bitmap? {
    try {
        return Bitmap.createBitmap(width, height, config)
    } catch (e: OutOfMemoryError) {
        e.printStackTrace()
        if (retryCount > 0) {
            System.gc()
            return createBitmapSafely(width, height, config, retryCount - 1)
        }
        return null
    }
}

/**
 * Register a callback to be invoked when the global layout state or the visibility of views
 * within the view tree changes
 *
 * @param callback The callback() to be invoked
 */
inline fun View.onGlobalLayout(crossinline callback: () -> Unit) = with(viewTreeObserver) {
    addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun onGlobalLayout() {
            removeOnGlobalLayoutListener(this)
            callback()
        }
    })
}

/**
 * Register a callback to be invoked after the view is measured
 *
 * @param callback The callback() to be invoked
 */
inline fun View.afterMeasured(crossinline callback: View.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                callback()
            }
        }
    })
}

var clickCount = 0
var lastClickTime = 0L

/**
 * Invoke the [action] after click [count] times.
 * The interval between two clicks is less than [interval] mills
 */
fun View.clickN(count: Int = 1, interval: Long = 1000, action: () -> Unit) {

    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (lastClickTime != 0L && (currentTime - lastClickTime > interval)) {
            clickCount = 1
            lastClickTime = currentTime
            return@setOnClickListener
        }

        ++clickCount
        lastClickTime = currentTime

        if (clickCount == count) {
            clickCount = 0
            lastClickTime = 0L
            action()
        }
    }
}