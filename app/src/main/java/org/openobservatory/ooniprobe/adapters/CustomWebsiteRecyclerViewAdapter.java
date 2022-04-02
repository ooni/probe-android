package org.openobservatory.ooniprobe.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.collect.Lists;

import org.openobservatory.ooniprobe.databinding.EdittextUrlBinding;

import java.util.ArrayList;
import java.util.List;

public class CustomWebsiteRecyclerViewAdapter extends RecyclerView.Adapter<CustomWebsiteRecyclerViewAdapter.ViewHolder> {

    private final List<String> mItems;
    private final List<Boolean> mVisibility;
    private final ItemRemovedListener onItemRemoved;

    /**
     * Initialize the dataset of the Adapter.
     */
    public CustomWebsiteRecyclerViewAdapter(ItemRemovedListener onItemRemoved) {
        mItems = new ArrayList<>();
        mVisibility = new ArrayList<>();
        this.onItemRemoved = onItemRemoved;
    }

    public void addAll(List<String> dataSet) {
        mItems.addAll(dataSet);
        mVisibility.addAll(Lists.transform(dataSet, input -> Boolean.TRUE));
        mVisibility.set(0, mItems.size() > 1);
    }

    public List<String> getItems() {
        return mItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        EdittextUrlBinding binding = EdittextUrlBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.binding.editText.setText(mItems.get(position));
        viewHolder.binding.delete.setVisibility(mVisibility.get(position) ? View.VISIBLE : View.INVISIBLE);


        viewHolder.binding.delete.setOnClickListener(v -> {
            mItems.remove(viewHolder.getAdapterPosition());
            mVisibility.remove(viewHolder.getAdapterPosition());
            mVisibility.set(0, mItems.size() > 1);
            notifyDataSetChanged();
            onItemRemoved.onItemRemoved(viewHolder.getAdapterPosition());
        });

        viewHolder.binding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mItems.set(viewHolder.getAdapterPosition(), String.valueOf(charSequence));
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public interface ItemRemovedListener {
        void onItemRemoved(int position);
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final EdittextUrlBinding binding;

        public ViewHolder(EdittextUrlBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
