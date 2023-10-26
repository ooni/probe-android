package org.openobservatory.ooniprobe.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.openobservatory.ooniprobe.databinding.EdittextUrlBinding
import java.lang.Boolean.TRUE


class CustomWebsiteRecyclerViewAdapter(private val onItemRemovedListener: ItemRemovedListener) :
    RecyclerView.Adapter<CustomWebsiteRecyclerViewAdapter.ViewHolder>() {
    private val mItems: MutableList<String>
    private val mVisibility: MutableList<Boolean>

    /**
     * Initialize the dataset of the Adapter.
     */
    init {
        mItems = ArrayList()
        mVisibility = ArrayList()
    }

    fun addAll(items: List<String>?) {
        mItems.addAll(items ?: listOf())
        mVisibility.addAll(mItems.map { TRUE })
        mVisibility[0] = mItems.size > 1
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
        holder.binding.editText.setText(mItems[position])
        holder.binding.delete.visibility =
            if (mVisibility[position]) View.VISIBLE else View.INVISIBLE
        holder.binding.delete.setOnClickListener {
            mItems.removeAt(holder.adapterPosition)
            mVisibility.removeAt(holder.adapterPosition)
            mVisibility[0] = mItems.size > 1
            notifyDataSetChanged()
            onItemRemovedListener.onItemRemoved(holder.adapterPosition)
        }
        holder.binding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                mItems[holder.adapterPosition] = charSequence.toString()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    override fun getItemCount(): Int = mItems.size
    fun getItems(): List<String> = mItems

    class ViewHolder(val binding: EdittextUrlBinding) : RecyclerView.ViewHolder(binding.root)
}

interface ItemRemovedListener {
    fun onItemRemoved(position: Int)
}