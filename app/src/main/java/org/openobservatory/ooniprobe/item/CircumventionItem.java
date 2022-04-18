package org.openobservatory.ooniprobe.item;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Network;
import org.openobservatory.ooniprobe.model.database.Result;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerItem;

@Deprecated
public class CircumventionItem extends HeterogeneousRecyclerItem<Result, CircumventionItem.ViewHolder> {
    private final View.OnClickListener onClickListener;
    private final View.OnLongClickListener onLongClickListener;

    public CircumventionItem(Result extra, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        super(extra);
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
    }

    @Override public CircumventionItem.ViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return new CircumventionItem.ViewHolder(layoutInflater.inflate(R.layout.item_circumvention, viewGroup, false));
    }

    @Override public void onBindViewHolder(CircumventionItem.ViewHolder viewHolder) {
        viewHolder.itemView.setTag(extra);
        viewHolder.itemView.setOnClickListener(onClickListener);
        viewHolder.itemView.setOnLongClickListener(onLongClickListener);
        viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(), extra.is_viewed ? android.R.color.transparent : R.color.color_yellow0));
        viewHolder.asnName.setText(Network.toString(viewHolder.asnName.getContext(), extra.network));
        viewHolder.startTime.setText(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "yMdHm"), extra.start_time));
        Long blocked = extra.countAnomalousMeasurements();
        Long available = extra.countOkMeasurements();
        viewHolder.failedMeasurements.setText(viewHolder.failedMeasurements.getContext().getResources().getQuantityString(R.plurals.TestResults_Overview_Circumvention_Blocked, blocked.intValue(), blocked.toString()));
        viewHolder.okMeasurements.setText(viewHolder.failedMeasurements.getContext().getResources().getQuantityString(R.plurals.TestResults_Overview_Circumvention_Available, available.intValue(), available.toString()));
        viewHolder.failedMeasurements.setTextColor(ContextCompat.getColor(viewHolder.failedMeasurements.getContext(), blocked == 0 ? R.color.color_gray9 : R.color.color_yellow9));
        DrawableCompat.setTint(DrawableCompat.wrap(viewHolder.failedMeasurements.getCompoundDrawablesRelative()[0]).mutate(), ContextCompat.getColor(viewHolder.failedMeasurements.getContext(), blocked == 0 ? R.color.color_gray9 : R.color.color_yellow9));
        boolean allUploaded = true;
        for (Measurement m : extra.getMeasurements())
            allUploaded = allUploaded && (m.isUploaded() || m.is_failed);
        viewHolder.startTime.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, allUploaded ? 0 : R.drawable.cloudoff, 0);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.asnName)
        TextView asnName;
        @BindView(R.id.startTime) TextView startTime;
        @BindView(R.id.failedMeasurements) TextView failedMeasurements;
        @BindView(R.id.okMeasurements) TextView okMeasurements;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
