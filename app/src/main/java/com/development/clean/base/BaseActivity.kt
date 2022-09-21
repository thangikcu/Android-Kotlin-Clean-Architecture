package com.development.clean.base

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<ViewBinding : ViewDataBinding, ViewModel : BaseViewModel>
constructor(@LayoutRes private val contentLayoutId: Int) :
    AppCompatActivity() {

    init {
        addOnContextAvailableListener {
            binding.notifyChange()
        }
    }

    protected abstract val viewModel: ViewModel

    private var bindingComponent: DataBindingComponent? = DataBindingUtil.getDefaultComponent()

    protected val binding: ViewBinding by lazy(LazyThreadSafetyMode.NONE) {
        DataBindingUtil.setContentView<ViewBinding>(this, contentLayoutId, bindingComponent).apply {
            lifecycleOwner = this@BaseActivity
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }
}