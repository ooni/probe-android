package org.openobservatory.ooniprobe.activity.customwebsites.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.openobservatory.ooniprobe.databinding.EdittextUrlBinding


class CustomWebsiteRecyclerViewAdapter(
    private val onItemChangedListener: ItemChangedListener,
) : ListAdapter<String, CustomWebsiteRecyclerViewAdapter.ViewHolder>(URL_DIFF_CALLBACK) {

    companion object {
        private val URL_DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        return ViewHolder(
            EdittextUrlBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder, position: Int
    ) {
        holder.binding.editText.setText(getItem(position))
        holder.binding.delete.visibility = View.VISIBLE
        holder.binding.delete.setOnClickListener {
            onItemChangedListener.onItemRemoved(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }
        holder.binding.editText.addTextChangedListener {
            onItemChangedListener.onItemUpdated(position, it.toString())
        }
    }

    class ViewHolder(val binding: EdittextUrlBinding) : RecyclerView.ViewHolder(binding.root)
}

interface ItemChangedListener {
    fun onItemRemoved(position: Int)
    fun onItemUpdated(position: Int, toString: String)
}