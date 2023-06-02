package org.openobservatory.ooniprobe.item;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.database.TestDescriptor;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerItem;

public class TestsuiteItem extends HeterogeneousRecyclerItem<TestDescriptor, TestsuiteItem.ViewHolderImpl> {
	private final View.OnClickListener onClickListener;
	private final PreferenceManager preferenceManager;

	public TestsuiteItem(TestDescriptor extra, View.OnClickListener onClickListener, PreferenceManager preferenceManager) {
		super(extra);
		this.onClickListener = onClickListener;
		this.preferenceManager = preferenceManager;
	}

	@Override public ViewHolderImpl onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
		return new ViewHolderImpl(layoutInflater.inflate(R.layout.item_testsuite, viewGroup, false));
	}

	@Override public void onBindViewHolder(ViewHolderImpl holder) {
		Resources resources = holder.itemView.getContext().getResources();

		holder.title.setText(extra.getName());
		holder.desc.setText(extra.getShortDescription());
		holder.icon.setImageResource(resources.getIdentifier(extra.getIcon(), "drawable", holder.itemView.getContext().getPackageName()));
		holder.itemView.setTag(extra);
		if(!extra.isEnabled()) {
			((CardView)holder.itemView).setElevation(0);
			((CardView)holder.itemView).setCardBackgroundColor(resources.getColor(R.color.disabled_test_background));
			holder.title.setTextColor(resources.getColor(R.color.disabled_test_text));
			holder.desc.setTextColor(resources.getColor(R.color.disabled_test_text));
			holder.icon.setColorFilter(resources.getColor(R.color.disabled_test_text), PorterDuff.Mode.SRC_IN);
			holder.setIsRecyclable(false);
			holder.itemView.setClickable(false);
		} else {
			holder.itemView.setOnClickListener(onClickListener);
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
