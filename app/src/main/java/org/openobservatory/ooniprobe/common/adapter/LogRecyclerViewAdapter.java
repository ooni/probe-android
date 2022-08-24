package org.openobservatory.ooniprobe.common.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.ItemTextBinding;

import java.util.List;

public class LogRecyclerViewAdapter extends RecyclerView.Adapter<LogRecyclerViewAdapter.ViewHolder> {

    private List<String> localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemTextBinding binding;

        public ViewHolder(ItemTextBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet List<String> containing the data to populate views to be used
     *                by RecyclerView.
     */
    public LogRecyclerViewAdapter(List<String> dataSet) {
        localDataSet = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTextBinding binding = ItemTextBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Context context = viewHolder.binding.getRoot().getContext();
        viewHolder.binding.textView.setText(Html.fromHtml(localDataSet.get(position).trim()));
        viewHolder.binding.textView.setPadding(0, 0, 0, 0);
        viewHolder.binding.getRoot().setOnLongClickListener(v -> {
            ((ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE))
                    .setPrimaryClip(ClipData.newPlainText(context.getString(R.string.General_AppName), Html.fromHtml(localDataSet.get(position).trim())));
            Toast.makeText(context, R.string.Toast_CopiedToClipboard, Toast.LENGTH_SHORT).show();

            return false;
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}