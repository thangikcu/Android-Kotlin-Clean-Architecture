package com.development.clean.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<ViewBinding : ViewDataBinding, ViewModel : BaseViewModel>(@LayoutRes private val contentLayoutId: Int) :
    Fragment() {

    private var bindingComponent: DataBindingComponent? = DataBindingUtil.getDefaultComponent()

    private var _binding: ViewBinding? = null

    protected abstract val viewModel: ViewModel

    protected val binding: ViewBinding
        get() = checkNotNull(_binding) {
            "Fragment $this binding cannot be accessed before onCreateView() or after onDestroyView()"
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate<ViewBinding>(
                inflater,
                contentLayoutId,
                container,
                false,
                bindingComponent
            ).apply {
                lifecycleOwner = viewLifecycleOwner
            }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding?.unbind()
        _binding = null
    }
}

