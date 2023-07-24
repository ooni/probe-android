package org.openobservatory.ooniprobe.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openobservatory.applogger.LogType;
import org.openobservatory.applogger.LoggerManager;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.ItemTextBinding;

import java.util.List;

public class LogRecyclerViewAdapter extends RecyclerView.Adapter<LogRecyclerViewAdapter.ViewHolder> {

    private final List<String> items;

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
     * @param data List<String> containing the data to populate views to be used
     *                by RecyclerView.
     */
    public LogRecyclerViewAdapter(List<String> data) {
        items = data;
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
        viewHolder.binding.textView.setText(items.get(position).trim());
        viewHolder.binding.textView.setPadding(0, 0, 0, 0);
        viewHolder.binding.getRoot().setOnLongClickListener(v -> {
            ((ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE))
                    .setPrimaryClip(ClipData.newPlainText(context.getString(R.string.General_AppName), Html.fromHtml(items.get(position).trim())));
            Toast.makeText(context, R.string.Toast_CopiedToClipboard, Toast.LENGTH_SHORT).show();

            return false;
        });
        try {
            switch (LogType.valueOf(LoggerManager.getTag(items.get(position).trim()))){
                case ERROR:
                    viewHolder.binding.textView.setTextColor(context.getResources().getColor(R.color.color_red9));
                    break;
                case WARNING:
                    viewHolder.binding.textView.setTextColor(context.getResources().getColor(R.color.color_orange9));
                    break;
                case INFO:
                    viewHolder.binding.textView.setTextColor(context.getResources().getColor(R.color.color_green9));
                    break;
                case API:
                    viewHolder.binding.textView.setTextColor(context.getResources().getColor(R.color.color_blue9));
                    break;
                default:
                    viewHolder.binding.textView.setTextColor(context.getResources().getColor(R.color.color_black));
                    break;
            }
        }catch (Exception e){
            System.out.println(items.get(position).trim());
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
