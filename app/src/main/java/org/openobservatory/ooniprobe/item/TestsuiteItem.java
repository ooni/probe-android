package org.openobservatory.ooniprobe.item;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerItem;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.databinding.ItemTestsuiteBinding;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;

public class TestsuiteItem extends HeterogeneousRecyclerItem<AbstractSuite, TestsuiteItem.ViewHolderImpl> {
	private final View.OnClickListener onClickListener;
	private final PreferenceManager preferenceManager;

	public TestsuiteItem(AbstractSuite extra, View.OnClickListener onClickListener, PreferenceManager preferenceManager) {
		super(extra);
		this.onClickListener = onClickListener;
		this.preferenceManager = preferenceManager;
	}

	@Override public ViewHolderImpl onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
		return new ViewHolderImpl(ItemTestsuiteBinding.inflate(layoutInflater, viewGroup, false));
	}

	@Override public void onBindViewHolder(ViewHolderImpl holder) {
		holder.binding.title.setText(extra.getTitle());
		holder.binding.desc.setText(extra.getCardDesc());
		holder.binding.icon.setImageResource(extra.getIconGradient());
		holder.itemView.setTag(extra);
		if(extra.isTestEmpty(preferenceManager)) {
			((CardView)holder.itemView).setElevation(0);
			Resources resources = holder.itemView.getContext().getResources();
			((CardView)holder.itemView).setCardBackgroundColor(resources.getColor(R.color.disabled_test_background));
			holder.binding.title.setTextColor(resources.getColor(R.color.disabled_test_text));
			holder.binding.desc.setTextColor(resources.getColor(R.color.disabled_test_text));
			holder.binding.icon.setColorFilter(resources.getColor(R.color.disabled_test_text), PorterDuff.Mode.SRC_IN);
			holder.setIsRecyclable(false);
			holder.itemView.setClickable(false);
		} else {
			holder.itemView.setOnClickListener(onClickListener);
		}
	}

	static class ViewHolderImpl extends RecyclerView.ViewHolder {
		ItemTestsuiteBinding binding;

		ViewHolderImpl(ItemTestsuiteBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}
	}
}
