package org.openobservatory.ooniprobe.item

import android.view.View
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.model.database.Result

class ProgressItem(
    result: Result,
    onClickListener: View.OnClickListener,
    onLongClickListener: View.OnLongClickListener
) : FailedItem(result, onClickListener, onLongClickListener) {
    override fun onBindViewHolder(viewHolder: ViewHolder) {
        super.onBindViewHolder(viewHolder)
        viewHolder.binding.subtitle.text = viewHolder.itemView.context
            .getString(R.string.Dashboard_Running_Running)
            .replace(":", "");
    }
}