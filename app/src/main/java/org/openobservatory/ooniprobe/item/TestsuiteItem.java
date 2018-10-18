package org.openobservatory.ooniprobe.item;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.widget.HeterogeneousRecyclerItem;

public class TestsuiteItem extends HeterogeneousRecyclerItem<AbstractSuite, TestsuiteItem.ViewHolderImpl> {
	private View.OnClickListener onClickListener;

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
		holder.icon.setImageResource(extra.getIcon());
		int color = ContextCompat.getColor(holder.card.getContext(), extra.getColor());
		holder.card.setCardBackgroundColor(color);
		holder.run.setTextColor(color);
		holder.configure.setOnClickListener(onClickListener);
		holder.run.setOnClickListener(onClickListener);
		holder.itemView.setOnClickListener(onClickListener);
		holder.configure.setTag(extra);
		holder.run.setTag(extra);
		holder.itemView.setTag(extra);
		holder.runtime.setText(DateUtils.formatElapsedTime(extra.getRuntime()));
	}

	class ViewHolderImpl extends RecyclerView.ViewHolder {
		@BindView(R.id.title) TextView title;
		@BindView(R.id.desc) TextView desc;
		@BindView(R.id.icon) ImageView icon;
		@BindView(R.id.card) CardView card;
		@BindView(R.id.configure) Button configure;
		@BindView(R.id.run) Button run;
		@BindView(R.id.runtime) TextView runtime;

		ViewHolderImpl(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
