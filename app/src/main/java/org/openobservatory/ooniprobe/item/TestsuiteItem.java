package org.openobservatory.ooniprobe.item;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerItem;

public class TestsuiteItem extends HeterogeneousRecyclerItem<AbstractSuite, TestsuiteItem.ViewHolderImpl> {
	private final View.OnClickListener onClickListener;

	public TestsuiteItem(AbstractSuite extra, View.OnClickListener onClickListener) {
		super(extra);
		this.onClickListener = onClickListener;
	}

	@Override public ViewHolderImpl onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
		return new ViewHolderImpl(layoutInflater.inflate(R.layout.item_testsuite, viewGroup, false));
	}

	@Override public void onBindViewHolder(ViewHolderImpl holder) {
		holder.title.setText(extra.getTitle());
		holder.desc.setText(extra.getCardDesc());
		holder.icon.setImageResource(extra.getIconGradient());
		holder.itemView.setOnClickListener(onClickListener);
		holder.itemView.setTag(extra);
		if(extra.isTestEmpty()) {
			((CardView)holder.itemView).setElevation(0);
			Resources resources = holder.itemView.getContext().getResources();
			((CardView)holder.itemView).setCardBackgroundColor(resources.getColor(R.color.disabled_test_background));
			holder.title.setTextColor(resources.getColor(R.color.disabled_test_text));
			holder.desc.setTextColor(resources.getColor(R.color.disabled_test_text));
			holder.icon.setColorFilter(resources.getColor(R.color.disabled_test_text), PorterDuff.Mode.SRC_IN);
			holder.setIsRecyclable(false);
		}
	}

	class ViewHolderImpl extends RecyclerView.ViewHolder {
		@BindView(R.id.title) TextView title;
		@BindView(R.id.desc) TextView desc;
		@BindView(R.id.icon) ImageView icon;

		ViewHolderImpl(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
