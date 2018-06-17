package org.openobservatory.ooniprobe.item;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.Test;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.widget.HeterogeneousRecyclerItem;

public class TestItem extends HeterogeneousRecyclerItem<Test, TestItem.ViewHolderImpl> {
	private View.OnClickListener onClickListener;

	public TestItem(Test extra, View.OnClickListener onClickListener) {
		super(extra);
		this.onClickListener = onClickListener;
	}

	@Override public ViewHolderImpl onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
		return new ViewHolderImpl(layoutInflater.inflate(R.layout.item_test, viewGroup, false));
	}

	@Override public void onBindViewHolder(ViewHolderImpl holder) {
		holder.title.setText(extra.getTitle());
		holder.desc.setText(extra.getCardDesc());
		holder.icon.setImageResource(extra.getIcon());
		holder.card.setCardBackgroundColor(ContextCompat.getColor(holder.card.getContext(), extra.getColor()));
		holder.configure.setOnClickListener(onClickListener);
		holder.run.setOnClickListener(onClickListener);
		holder.itemView.setOnClickListener(onClickListener);
		holder.configure.setTag(extra);
		holder.run.setTag(extra);
		holder.itemView.setTag(extra);

	}

	class ViewHolderImpl extends RecyclerView.ViewHolder {
		@BindView(R.id.title) TextView title;
		@BindView(R.id.desc) TextView desc;
		@BindView(R.id.icon) ImageView icon;
		@BindView(R.id.card) CardView card;
		@BindView(R.id.configure) Button configure;
		@BindView(R.id.run) Button run;

		ViewHolderImpl(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
