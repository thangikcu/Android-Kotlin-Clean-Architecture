/**
 * Copyright (C) 2018 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("unused")

package com.development.clean.util.extension

import android.view.View
import android.widget.Toast
import androidx.core.view.postDelayed
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) =
    beginTransaction().func().commit()

fun Fragment.awaitResume(block: () -> Unit) {
    lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) {
            lifecycle.removeObserver(this)
            block.invoke()
        }
    })
}

inline fun Fragment.postDelayed(delayInMillis: Long, crossinline action: () -> Unit) {
    view?.postDelayed(delayInMillis, action)
}

fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    context?.showToast(message, duration)
}

fun Fragment.hideKeyboard(view: View? = null) {
    activity?.hideKeyboard(view)
}