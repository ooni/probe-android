package org.openobservatory.ooniprobe.adapters.diff

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil
import org.openobservatory.ooniprobe.adapters.ResultListAdapter.UiModel

class ResultComparator : DiffUtil.ItemCallback<UiModel>() {
    /**
     * This diff callback informs the PagedListAdapter how to compute list differences when new
     * PagedLists arrive.
     *
     * When you add a Cheese with the 'Add' button, the PagedListAdapter uses diffCallback to
     * detect there's only a single item difference from before, so it only needs to animate and
     * rebind a single view.
     *
     * @see DiffUtil
     */
    override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
        return if (oldItem is UiModel.ResultModel && newItem is UiModel.ResultModel) {
            oldItem.item.id == newItem.item.id
        } else if (oldItem is UiModel.SeparatorModel && newItem is UiModel.SeparatorModel) {
            oldItem.description == newItem.description
        } else {
            oldItem == newItem
        }
    }

    /**
     * Note that in kotlin, == checking on data classes compares all contents, but in Java,
     * typically you'll implement Object#equals, and use it to compare object contents.
     */
    override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
        return oldItem.equals(newItem)
    }
}
