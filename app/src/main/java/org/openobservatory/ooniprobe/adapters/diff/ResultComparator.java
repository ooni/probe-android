package org.openobservatory.ooniprobe.adapters.diff;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import org.jetbrains.annotations.NotNull;
import org.openobservatory.ooniprobe.adapters.ResultListAdapter.UiModel;

import java.util.Objects;


public class ResultComparator extends DiffUtil.ItemCallback<UiModel> {
    /**
     * This diff callback informs the PagedListAdapter how to compute list differences when new
     * PagedLists arrive.
     * <p>
     * When you add a Cheese with the 'Add' button, the PagedListAdapter uses diffCallback to
     * detect there's only a single item difference from before, so it only needs to animate and
     * rebind a single view.
     *
     * @see DiffUtil
     */
    public boolean areItemsTheSame(@NonNull UiModel oldItem, @NonNull UiModel newItem) {
        if (oldItem instanceof UiModel.ResultModel && newItem instanceof UiModel.ResultModel) {
            return ((UiModel.ResultModel) oldItem).getItem().id == ((UiModel.ResultModel) newItem).getItem().id;
        } else if (oldItem instanceof UiModel.SeparatorModel && newItem instanceof UiModel.SeparatorModel) {
            return Objects.equals(((UiModel.SeparatorModel) oldItem).getDescription(), ((UiModel.SeparatorModel) newItem).getDescription());
        } else {
            return Objects.equals(oldItem, newItem);
        }
    }

    /**
     * Note that in kotlin, == checking on data classes compares all contents, but in Java,
     * typically you'll implement Object#equals, and use it to compare object contents.
     */
    public boolean areContentsTheSame(@NotNull UiModel oldItem, @NotNull UiModel newItem) {
        return Objects.equals(oldItem,newItem);
    }
}
