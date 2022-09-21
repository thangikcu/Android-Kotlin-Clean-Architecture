package com.development.clean.base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.development.clean.BR
import java.util.concurrent.Executors

/**
 * base recycler view adapter
 */
abstract class BaseRecyclerAdapter<Item : Any, ViewBinding : ViewDataBinding>(
    callBack: DiffUtil.ItemCallback<Item> = object : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    },
    open val onItemClick: ((Item, View, Int) -> Unit)? = null
) : ListAdapter<Item, BaseViewHolder<ViewBinding>>(
    AsyncDifferConfig.Builder(callBack)
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
        .build()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewBinding> {
        return BaseViewHolder(
            DataBindingUtil.inflate<ViewBinding>(
                LayoutInflater.from(parent.context),
                getLayoutRes(viewType),
                parent, false
            )
        ).apply {
            itemView.setOnClickListener {
                onItemClick?.invoke(
                    getItem(absoluteAdapterPosition),
                    itemView,
                    absoluteAdapterPosition
                )
            }
            bindFirstTime(binding)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewBinding>, position: Int) {
        val item: Item? = currentList.getOrNull(position)
        holder.binding.setVariable(BR.item, item)
        if (item != null) {
            bindView(holder.binding, item, position)
        }
        holder.binding.executePendingBindings()
    }

    /**
     * get layout res based on view type
     */
    protected abstract fun getLayoutRes(viewType: Int): Int

    /**
     * bind first time
     * use for set model_view_convention onClickListener, something only set one time
     */
    protected open fun bindFirstTime(binding: ViewBinding) {}

    /**
     * bind view
     */
    protected open fun bindView(binding: ViewBinding, item: Item, position: Int) {}
}

open class BaseViewHolder<ViewBinding : ViewDataBinding>(
    val binding: ViewBinding
) : RecyclerView.ViewHolder(binding.root)