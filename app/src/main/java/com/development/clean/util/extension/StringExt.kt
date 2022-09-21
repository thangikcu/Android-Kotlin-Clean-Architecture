@file:Suppress("unused")

package com.development.clean.util.extension

import android.graphics.Color
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.util.Patterns
import android.view.View
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun String.dbQuery(): String {
    return "%${replace(' ', '%')}%"
}

fun String.linkify(
    linkColor: Int = Color.BLUE,
    linkClickAction: ((link: String) -> Unit)? = null
): SpannableStringBuilder {
    val builder = SpannableStringBuilder(this)
    val matcher = Patterns.WEB_URL.matcher(this)
    while (matcher.find()) {
        val start = matcher.start()
        val end = matcher.end()
        builder.setSpan(ForegroundColorSpan(linkColor), start, end, 0)
        val onClick = object : ClickableSpan() {
            override fun onClick(p0: View) {
                linkClickAction?.invoke(matcher.group())
            }
        }
        builder.setSpan(onClick, start, end, 0)
    }
    return builder
}

fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

fun String.dateInFormat(format: String): Date? {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    var parsedDate: Date? = null
    try {
        parsedDate = dateFormat.parse(this)
    } catch (ignored: ParseException) {
        ignored.printStackTrace()
    }
    return parsedDate
}

fun String.equalsIgnoreCase(other: String) = this.lowercase(Locale.getDefault()).contentEquals(
    other.lowercase(
        Locale.getDefault()
    )
)

//region Validation
fun String.isPhone(): Boolean {
    val p = "^1([34578])\\d{9}\$".toRegex()
    return matches(p)
}

fun String.isEmail(): Boolean {
    val p = "^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)\$".toRegex()
    return matches(p)
}

fun String.isNumeric(): Boolean {
    val p = "^[0-9]+$".toRegex()
    return matches(p)
}
//endregion

//region Encryption
fun String.decodeFromBase64(): String {
    return Base64.decode(this, Base64.DEFAULT).toString(Charsets.UTF_8)
}

fun String.encodeToBase64(): String {
    return Base64.encodeToString(this.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
}

fun String.md5() = encrypt(this, "MD5")

fun String.sha1() = encrypt(this, "SHA-1")

private fun encrypt(string: String?, type: String): String {
    if (string.isNullOrEmpty()) {
        return ""
    }
    val md5: MessageDigest
    return try {
        md5 = MessageDigest.getInstance(type)
        val bytes = md5.digest(string.toByteArray())
        bytes2Hex(bytes)
    } catch (e: NoSuchAlgorithmException) {
        ""
    }
}

private fun bytes2Hex(bts: ByteArray): String {
    var des = ""
    var tmp: String
    for (i in bts.indices) {
        tmp = Integer.toHexString(bts[i].toInt() and 0xFF)
        if (tmp.length == 1) {
            des += "0"
        }
        des += tmp
    }
    return des
}
//endregion