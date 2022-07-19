@file:Suppress("unused")

package com.development.clean.util.extension

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.development.clean.util.Utils
import com.google.android.material.snackbar.Snackbar
import kotlin.math.roundToInt

val Activity.isKeyboardVisible: Boolean
    get() {
        val KEYBOARD_VISIBLE_THRESHOLD_DP = 100
        val r = Rect()
        val activityRoot =
            (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(
                0
            )
        val visibleThreshold =
            Utils.convertDpToPx(this, KEYBOARD_VISIBLE_THRESHOLD_DP.toFloat()).roundToInt()
        activityRoot.getWindowVisibleDisplayFrame(r)
        val heightDiff = activityRoot.rootView.height - r.height()
        return heightDiff > visibleThreshold
    }

fun Activity.hideKeyboard(v: View? = null) {
    val view = v ?: currentFocus ?: View(this)

    view.hideKeyboard()
}

fun Activity.showSnackBar(message: CharSequence, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(
        findViewById(android.R.id.content),
        message,
        duration
    ).show()
}

fun Activity.showSnackBar(@StringRes stringRes: Int, duration: Int = Snackbar.LENGTH_SHORT) {
    showSnackBar(getText(stringRes), duration)
}