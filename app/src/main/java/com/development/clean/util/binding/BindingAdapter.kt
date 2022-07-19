@file:Suppress("unused")

package com.development.clean.util.binding

import android.content.res.ColorStateList
import android.net.Uri
import android.os.SystemClock
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.development.clean.common.Constants

@BindingAdapter("onRefreshListener")
fun SwipeRefreshLayout.customRefreshListener(listener: SwipeRefreshLayout.OnRefreshListener?) {
    if (listener != null) setOnRefreshListener(listener)
}

@BindingAdapter("isRefreshing")
fun SwipeRefreshLayout.customRefreshing(refreshing: Boolean?) {
    isRefreshing = refreshing == true
}

@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}

@BindingAdapter("onScrollListener")
fun RecyclerView.customScrollListener(listener: RecyclerView.OnScrollListener?) {
    if (listener != null) addOnScrollListener(listener)
}

@BindingAdapter("glideSrc")
fun ImageView.setGlideSrc(@DrawableRes src: Int?) {
    Glide.with(context).load(src).into(this)
}

@BindingAdapter("loadUri")
fun ImageView.loadLocalImage(uri: Uri?) {
    Glide.with(context).load(uri).into(this)
}

@BindingAdapter(value = ["loadImageLocal"])
fun ImageView.loadLocalImage(imageName: String?) {
    if (imageName.isNullOrBlank().not()) {
        Glide.with(context)
            .load(resources.getIdentifier(imageName, "drawable", context.packageName))
            .into(this)
    }
}

@BindingAdapter("setTextAndSelected")
fun TextView.setTextAndSelected(text: String?) {
    this.text = text
    isSelected = true
}

@BindingAdapter("onSingleClick")
fun View.setSingleClick(execution: (View) -> Unit) {
    setOnClickListener(object : View.OnClickListener {
        var lastClickTime: Long = 0

        override fun onClick(p0: View?) {
            if (SystemClock.elapsedRealtime() - lastClickTime < Constants.THRESHOLD_CLICK_TIME) {
                return
            }
            lastClickTime = SystemClock.elapsedRealtime()
            execution.invoke(this@setSingleClick)
        }
    })
}

@BindingAdapter("loadUrl")
fun WebView.loadWebUrl(url: String?) {
    url?.apply {
        loadUrl(url)
    }
}

@BindingAdapter("backgroundTint")
fun TextView.customBackgroundTint(color: Int?) {
    if (color == null) return
    background?.setTint(color)
}

@BindingAdapter("tint")
fun ImageView.customTint(color: Int?) {
    if (color == null) return
    imageTintList = ColorStateList.valueOf(color)
}