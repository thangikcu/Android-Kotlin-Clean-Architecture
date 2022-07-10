package com.development.hiltpractices.base

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class BaseActivity constructor(@LayoutRes contentLayoutId: Int) :
    AppCompatActivity(contentLayoutId) {

}