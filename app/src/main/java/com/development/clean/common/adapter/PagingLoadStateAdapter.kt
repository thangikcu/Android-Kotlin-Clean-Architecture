package com.development.clean.common.adapter

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.development.clean.R
import com.development.clean.databinding.ItemLoadStateBinding
import com.development.clean.util.extension.bind

class PagingLoadStateAdapter : LoadStateAdapter<PagingLoadStateAdapter.LoadStateViewHolder>() {

    class LoadStateViewHolder(binding: ItemLoadStateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(@Suppress("UNUSED_PARAMETER") loadState: LoadState) {
        }

        init {
        }

        companion object {
            fun create(viewGroup: ViewGroup) =
                LoadStateViewHolder(viewGroup.bind(R.layout.item_load_state))
        }
    }

    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return loadState is LoadState.Loading
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        return LoadStateViewHolder.create(parent)
    }
}