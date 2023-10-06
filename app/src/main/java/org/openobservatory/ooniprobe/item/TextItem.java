package org.openobservatory.ooniprobe.item;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import org.openobservatory.ooniprobe.databinding.ItemTextBinding;

import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerItem;

public class TextItem extends HeterogeneousRecyclerItem<String, TextItem.ViewHolder> {
	public TextItem(String extra) {
		super(extra);
	}

	@Override public ViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
		return new ViewHolder(ItemTextBinding.inflate(layoutInflater, viewGroup, false));
	}

	@Override public void onBindViewHolder(ViewHolder viewHolder) {
		viewHolder.binding.textView.setText(extra);
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		ItemTextBinding binding;

		ViewHolder(ItemTextBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}
	}
}
