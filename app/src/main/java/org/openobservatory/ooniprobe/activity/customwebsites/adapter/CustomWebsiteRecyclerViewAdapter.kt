package org.openobservatory.ooniprobe.activity.customwebsites.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.openobservatory.ooniprobe.databinding.EdittextUrlBinding
import org.openobservatory.ooniprobe.activity.customwebsites.CustomWebsiteActivity


/**
 * [RecyclerView.Adapter] that can display an editable list of [String] on the [CustomWebsiteActivity].
 */
class CustomWebsiteRecyclerViewAdapter(
    private val onItemChangedListener: ItemChangedListener,
) : ListAdapter<String, CustomWebsiteRecyclerViewAdapter.ViewHolder>(URL_DIFF_CALLBACK) {

    companion object {
        /**
         * Used to calculate the difference between two lists.
         * This is used by the adapter to figure out if it needs to update the UI
         * The adapter will update the UI if this function returns false
         */
        private val URL_DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

    /**
     * Called (by the layout manager) when RecyclerView needs a new [ViewHolder] of the given type to represent an item.
     * This new ViewHolder should be constructed with a new View that can represent the items of the given type.
     * You can either create a new View manually or inflate it from an XML layout file.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            EdittextUrlBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method should update the contents of the [ViewHolder.itemView] to reflect the item at the given position.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class ViewHolder(val binding: EdittextUrlBinding) : RecyclerView.ViewHolder(binding.root)
}

/**
 * Interface to listen for changes in the list of [CustomWebsiteActivity]
 */
interface ItemChangedListener {
    /**
     * Called when an item is removed from the list
     * @param position The position of the item in the list
     */
    fun onItemRemoved(position: Int)

    /**
     * Called when an item is updated in the list
     * @param position The position of the item in the list
     * @param item The updated string
     */
    fun onItemUpdated(position: Int, item: String)
}