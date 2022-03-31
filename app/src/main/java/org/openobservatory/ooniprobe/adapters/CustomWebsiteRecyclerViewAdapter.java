package org.openobservatory.ooniprobe.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import org.openobservatory.ooniprobe.databinding.EdittextUrlBinding;

import java.util.List;

public class CustomWebsiteRecyclerViewAdapter extends RecyclerView.Adapter<CustomWebsiteRecyclerViewAdapter.ViewHolder> {

    private final List<String> mItems;

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final EdittextUrlBinding binding;
//        private final TextView textView;

        public ViewHolder(EdittextUrlBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
//            textView = (TextView) v.findViewById(R.id.textView);
        }

//        public TextView getTextView() {
//            return textView;
//        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet List<String> containing the data to populate views to be used by RecyclerView.
     */
    public CustomWebsiteRecyclerViewAdapter(List<String> dataSet) {
        mItems = dataSet;
    }

    public void addAll(List<String> dataSet) {
        mItems.addAll(dataSet);
    }

    public List<String> getItems() {
        return mItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        EdittextUrlBinding binding = EdittextUrlBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.binding.editText.setText(mItems.get(position));
        viewHolder.binding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mItems.set(viewHolder.getAdapterPosition(), String.valueOf(charSequence));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
