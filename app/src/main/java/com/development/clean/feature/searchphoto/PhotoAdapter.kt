package com.development.clean.feature.searchphoto

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.development.clean.R
import com.development.clean.databinding.ItemPhotoBinding
import com.development.clean.util.extension.bind
import com.development.clean.util.extension.showToast

class PhotoAdapter :
    PagingDataAdapter<SearchPhotoResponse.Photo, PhotoAdapter.PhotoViewHolder>(PhotoDiffCallback()) {

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(parent.bind(R.layout.item_photo))
    }

    class PhotoViewHolder(private val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                binding.photo?.id?.let { id -> it.context.showToast(id) }
            }
        }

        fun bind(item: SearchPhotoResponse.Photo) {
            binding.apply {
                photo = item
                executePendingBindings()
            }
        }
    }
}

private class PhotoDiffCallback : DiffUtil.ItemCallback<SearchPhotoResponse.Photo>() {
    override fun areItemsTheSame(
        oldItem: SearchPhotoResponse.Photo,
        newItem: SearchPhotoResponse.Photo
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: SearchPhotoResponse.Photo,
        newItem: SearchPhotoResponse.Photo
    ): Boolean {
        return oldItem == newItem
    }
}
