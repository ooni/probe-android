package org.openobservatory.ooniprobe.item;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import org.openobservatory.ooniprobe.databinding.ItemSeperatorBinding;

import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerItem;

@Deprecated
public class SeperatorItem extends HeterogeneousRecyclerItem<String, SeperatorItem.ViewHolderImpl> {

    public SeperatorItem() {
        super("");
    }

    @Override public ViewHolderImpl onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return new ViewHolderImpl(ItemSeperatorBinding.inflate(layoutInflater, viewGroup, false));
    }

    @Override public void onBindViewHolder(ViewHolderImpl holder) {}

    static class ViewHolderImpl extends RecyclerView.ViewHolder {
        ViewHolderImpl(ItemSeperatorBinding binding) {
            super(binding.getRoot());
        }
    }
}

