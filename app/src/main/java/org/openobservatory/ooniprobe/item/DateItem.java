package org.openobservatory.ooniprobe.item;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import org.openobservatory.ooniprobe.databinding.ItemDateBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerItem;

public class DateItem extends HeterogeneousRecyclerItem<Date, DateItem.ViewHolder> {
	private final SimpleDateFormat SDF;

	public DateItem(Date extra) {
		super(extra);
		SDF = new SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(), "MMMMyyyy"), Locale.getDefault());
	}

	@Override public ViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
		return new ViewHolder(ItemDateBinding.inflate(layoutInflater, viewGroup, false));
	}

	@Override public void onBindViewHolder(ViewHolder viewHolder) {
		viewHolder.binding.textView.setText(SDF.format(extra));
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		ItemDateBinding binding;

		ViewHolder(ItemDateBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}
	}
}
