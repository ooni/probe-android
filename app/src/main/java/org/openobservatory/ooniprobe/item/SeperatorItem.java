package org.openobservatory.ooniprobe.item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import org.openobservatory.ooniprobe.R;

import butterknife.ButterKnife;
import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerItem;

@Deprecated
public class SeperatorItem extends HeterogeneousRecyclerItem<String, SeperatorItem.ViewHolderImpl> {

    public SeperatorItem() {
        super("");
    }

    @Override public ViewHolderImpl onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return new ViewHolderImpl(layoutInflater.inflate(R.layout.item_seperator, viewGroup, false));
    }

    @Override public void onBindViewHolder(ViewHolderImpl holder) {}

    static class ViewHolderImpl extends RecyclerView.ViewHolder {
        ViewHolderImpl(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

