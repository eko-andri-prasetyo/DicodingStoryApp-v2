package com.dicoding.storyapp.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.databinding.ItemStoryBinding

class StoryAdapter(
    private val onClick: (ListStoryItem) -> Unit
) : PagingDataAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    inner class StoryViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListStoryItem) {
            binding.tvItemName.text = item.name ?: "-"
            Glide.with(binding.root)
                .load(item.photoUrl)
                .centerCrop()
                .into(binding.ivItemPhoto)

            binding.root.setOnClickListener { onClick(item) }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean = oldItem == newItem
        }
    }
}
