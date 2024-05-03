package org.openobservatory.ooniprobe.adapters

import android.content.res.Resources
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.databinding.ItemSeperatorBinding
import org.openobservatory.ooniprobe.databinding.ItemTestsuiteBinding
import org.openobservatory.ooniprobe.test.suite.AbstractSuite

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
            }

            VIEW_TYPE_CARD -> {
                val cardHolder = holder as CardViewHolder
                if (item is AbstractSuite) {
                    cardHolder.binding.apply {
                        title.setText(item.title)
                        desc.setText(item.cardDesc)
                        icon.setImageResource(item.iconGradient)
                    }
                    holder.itemView.tag = item
                    if (item.isTestEmpty(preferenceManager)) {
                        holder.setIsRecyclable(false)
                        holder.itemView.apply {
                            elevation = 0f
                            isClickable = false
                        }
                        val resources: Resources = holder.itemView.context.resources
                        (holder.itemView as CardView).setCardBackgroundColor(resources.getColor(R.color.disabled_test_background))
                        holder.binding.apply {
                            title.setTextColor(resources.getColor(R.color.disabled_test_text))
                            desc.setTextColor(resources.getColor(R.color.disabled_test_text))
                            icon.setColorFilter(resources.getColor(R.color.disabled_test_text), PorterDuff.Mode.SRC_IN)
                        }
                    } else {
                        holder.itemView.setOnClickListener(onClickListener)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is String -> VIEW_TYPE_TITLE
            else -> VIEW_TYPE_CARD
        }
    }

    /**
     * ViewHolder for dashboard item group
     * @param binding
     */
    class CardGroupTitleViewHolder(var binding: ItemSeperatorBinding) : RecyclerView.ViewHolder(binding.root)

    /**
     * ViewHolder for dashboard item
     * @param binding
     */
    class CardViewHolder(var binding: ItemTestsuiteBinding) : RecyclerView.ViewHolder(binding.root)
}