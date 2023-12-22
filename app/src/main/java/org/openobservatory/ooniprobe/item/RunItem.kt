package org.openobservatory.ooniprobe.item

import android.view.View
import org.openobservatory.ooniprobe.model.database.Result


class RunItem(
    var result: Result,
    var onClickListener: View.OnClickListener,
    var onLongClickListener: View.OnLongClickListener
) : ExperimentalItem(result, onClickListener, onLongClickListener) {
    override fun onBindViewHolder(viewHolder: ViewHolder?) {
        super.onBindViewHolder(viewHolder)
        viewHolder?.itemView?.context?.let { context ->
            extra.getDescriptor(context).get().let { descriptor ->
                viewHolder.binding?.icon?.setImageResource(descriptor.getDisplayIcon(context))
                viewHolder.binding?.name?.text = descriptor.title
            }
        }
    }
}