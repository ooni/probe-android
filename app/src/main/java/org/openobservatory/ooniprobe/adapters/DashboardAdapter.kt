package org.openobservatory.ooniprobe.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.openobservatory.ooniprobe.common.AbstractDescriptor
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.databinding.ItemSeperatorBinding
import org.openobservatory.ooniprobe.databinding.ItemTestsuiteBinding
import org.openobservatory.ooniprobe.model.database.InstalledDescriptor

class DashboardAdapter(
    private val items: List<Any>,
    private val onClickListener: View.OnClickListener,
    private val preferenceManager: PreferenceManager,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_TITLE = 0
        private const val VIEW_TYPE_CARD = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TITLE -> {
                CardGroupTitleViewHolder(
                    ItemSeperatorBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            else -> {
                CardViewHolder(
                    ItemTestsuiteBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder.itemViewType) {
            VIEW_TYPE_TITLE -> {
                val separator = holder as CardGroupTitleViewHolder
                separator.binding.root.text = when(item) {
                    is String -> item
                    is Int -> holder.itemView.context.getString(item)
                    else -> ""
                }
            }

            VIEW_TYPE_CARD -> {
                val cardHolder = holder as CardViewHolder
                if (item is AbstractDescriptor<*>) {
                    cardHolder.binding.apply {
                        title.text = item.title
                        desc.text = item.shortDescription
                        icon.setImageResource(item.getDisplayIcon(holder.itemView.context)).also {
                            if (item is InstalledDescriptor) {
                                icon.setColorFilter(item.color)
                                if (item.descriptor?.expired() == true) {
                                    expiredTag.root.visibility = View.VISIBLE
                                } else if (item.tags?.isNotEmpty() == true && item.tags?.get(0) == "updated") {
                                    updatedTag.root.visibility = View.VISIBLE
                                } else {
                                    expiredTag.root.visibility = View.GONE
                                }
                                holder.setIsRecyclable(false)
                            }
                        }
                    }
                    holder.itemView.tag = item
                    holder.itemView.setOnClickListener(onClickListener)
                }
            }
        }
        holder.setIsRecyclable(false)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is String -> VIEW_TYPE_TITLE
            is Int -> VIEW_TYPE_TITLE
            else -> VIEW_TYPE_CARD
        }
    }

    /**
     * ViewHolder for a dashboard item group.
     * @param binding
     */
    class CardGroupTitleViewHolder(var binding: ItemSeperatorBinding) :
        RecyclerView.ViewHolder(binding.root)

    /**
     * ViewHolder for dashboard item.
     * @param binding
     */
    class CardViewHolder(var binding: ItemTestsuiteBinding) : RecyclerView.ViewHolder(binding.root)
}
