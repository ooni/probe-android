package org.openobservatory.ooniprobe.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import org.openobservatory.ooniprobe.databinding.ItemRunV1Binding

/**
 * Adapter for displaying a List<String>.
 */
class StringListRecyclerViewAdapter(private val items: List<String>) :
    RecyclerView.Adapter<StringListRecyclerViewAdapter.ViewHolder>() {

    /**
     * Creates new views (invoked by the layout manager).
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRunV1Binding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager).
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.textView.apply {
            text = items[position]
            /**
             * INFO(aanorbel): The item view is set to single line in the layout file.
             * This is to prevent long URLs from displaying on multiple lines.
             * The code below will show the full URL in a toast when the user clicks on it.
             */
            setOnClickListener {
                Toast.makeText(
                    holder.binding.textView.context,
                    items[position],
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }


    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * Provides a reference to the views for each data item.
     */
    class ViewHolder(val binding: ItemRunV1Binding) : RecyclerView.ViewHolder(binding.root)
}