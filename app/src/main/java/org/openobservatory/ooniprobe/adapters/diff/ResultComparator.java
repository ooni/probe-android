package org.openobservatory.ooniprobe.adapters.diff;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import org.openobservatory.ooniprobe.model.database.Result;

public class ResultComparator extends DiffUtil.ItemCallback<Result> {
    @Override
    public boolean areItemsTheSame(@NonNull Result oldItem, @NonNull Result newItem) {
        return oldItem.id == newItem.id;
    }

    @Override
    public boolean areContentsTheSame(@NonNull Result oldItem, @NonNull Result newItem) {
        return oldItem.id == newItem.id;
    }
}
