package org.openobservatory.ooniprobe.activity.customwebsites.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import org.openobservatory.ooniprobe.activity.customwebsites.CustomWebsiteViewModel
import org.openobservatory.ooniprobe.databinding.EdittextUrlBinding


class CustomWebsiteRecyclerViewAdapter(
    private val onItemRemovedListener: ItemRemovedListener,
    private val viewModel: CustomWebsiteViewModel,
    var items: MutableList<String> = mutableListOf()
) : RecyclerView.Adapter<CustomWebsiteRecyclerViewAdapter.ViewHolder>() {

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
        holder.binding.editText.setText(items[position])
        holder.binding.delete.visibility = if (items.size > 1) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
        holder.binding.delete.setOnClickListener {
            onItemRemovedListener.onItemRemoved(holder.adapterPosition)
        }
        holder.binding.editText.addTextChangedListener {
            viewModel.updateUrlAt(position, it.toString())
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(val binding: EdittextUrlBinding) : RecyclerView.ViewHolder(binding.root)
}

interface ItemRemovedListener {
    fun onItemRemoved(position: Int)
}