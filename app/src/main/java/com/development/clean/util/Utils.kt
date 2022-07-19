@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.development.clean.util

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import android.widget.EditText
import java.io.DataOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

object Utils {

    //API 19 4.4 in October 2013
    fun fromKitKat() = fromSpecificVersion(Build.VERSION_CODES.KITKAT)

    //API 23 Marshmallow 6.0 in October 2015
    fun fromM() = fromSpecificVersion(Build.VERSION_CODES.M)
    fun beforeM() = beforeSpecificVersion(Build.VERSION_CODES.M)

    //API 24 Nougat 7.0 in August 2016
    fun fromN() = fromSpecificVersion(Build.VERSION_CODES.N)
    fun beforeN() = beforeSpecificVersion(Build.VERSION_CODES.N)

    //API 26 Oreo 8.0 in August 2017
    fun fromO() = fromSpecificVersion(Build.VERSION_CODES.O)
    fun beforeO() = beforeSpecificVersion(Build.VERSION_CODES.O)

    //API 28 Pie 9 in August 2018
    fun fromP() = fromSpecificVersion(Build.VERSION_CODES.P)
    fun beforeP() = beforeSpecificVersion(Build.VERSION_CODES.P)

    //API 29 10 in September 2019
    fun fromQ() = fromSpecificVersion(Build.VERSION_CODES.Q)
    fun beforeQ() = beforeSpecificVersion(Build.VERSION_CODES.Q)

    //API 30 11 in September 2020
    fun fromR() = fromSpecificVersion(Build.VERSION_CODES.R)
    fun beforeR() = beforeSpecificVersion(Build.VERSION_CODES.R)

    //API 31 12
    fun fromS() = fromSpecificVersion(Build.VERSION_CODES.S)
    fun beforeS() = beforeSpecificVersion(Build.VERSION_CODES.S)

    //API 32 12
    fun fromSv2() = fromSpecificVersion(Build.VERSION_CODES.S_V2)
    fun beforeSv2() = beforeSpecificVersion(Build.VERSION_CODES.S_V2)

    fun fromSpecificVersion(version: Int): Boolean = Build.VERSION.SDK_INT >= version
    fun beforeSpecificVersion(version: Int): Boolean = Build.VERSION.SDK_INT < version

    fun showKeyboardInDialog(dialog: Dialog, target: EditText) {
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        target.requestFocus()
    }

    private fun resourceIdToUri(
        context: Context,
        resourceId: Int
    ): Uri? {
        val ANDROID_RESOURCE = "android.resource://"
        val FOREWARD_SLASH = "/"
        return Uri.parse(ANDROID_RESOURCE + context.packageName + FOREWARD_SLASH + resourceId)
    }

    fun convertDpToPx(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        )
    }

    fun convertPxToDp(context: Context, px: Float): Float {
        return px / (context.resources
            .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun sendEmail(
        context: Context,
        subject: String?,
        body: String?,
        to: String
    ) {
        try {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
            //emailIntent.PutExtra(Android.Content.Intent.ExtraCc,
            //new string[] { cc });
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            emailIntent.putExtra(Intent.EXTRA_TEXT, body)
            emailIntent.type = "message/rfc822"
            context.startActivity(emailIntent)
        } catch (ignored: Exception) {
        }
    }

    fun isMainThread(): Boolean = Looper.myLooper() == Looper.getMainLooper()

    fun spannableSize(
        text: String,
        textSize: Int,
        isDip: Boolean,
        start: Int,
        end: Int
    ): SpannableString {
        val sp = SpannableString(text)
        sp.setSpan(AbsoluteSizeSpan(textSize, isDip), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        return sp
    }

    fun spannableBold(text: String, start: Int, end: Int): SpannableString {
        val sp = SpannableString(text)
        sp.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        return sp
    }

    private object ContextHandler {
        val handler = Handler(Looper.getMainLooper())
        val mainThread = Looper.getMainLooper().thread
    }

    fun runOnUiThread(action: () -> Unit) {
        if (ContextHandler.mainThread == Thread.currentThread()) action() else ContextHandler.handler.post { action() }
    }

    fun runDelayed(delay: Long, timeUnit: TimeUnit = TimeUnit.MILLISECONDS, action: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed(action, timeUnit.toMillis(delay))
    }

    fun runDelayedOnUiThread(
        delay: Long,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
        action: () -> Unit
    ) {
        ContextHandler.handler.postDelayed(action, timeUnit.toMillis(delay))
    }

    fun runCommand(vararg commands: String) {
        try {

            val su = Runtime.getRuntime().exec("su")
            val outputStream = DataOutputStream(su.outputStream)

            for (s in commands) {
                outputStream.writeBytes(s + "\n")
                outputStream.flush()
            }

            outputStream.writeBytes("exit\n")
            outputStream.flush()

            try {
                su.waitFor()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}