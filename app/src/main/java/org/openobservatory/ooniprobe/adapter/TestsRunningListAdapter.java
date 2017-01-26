package org.openobservatory.ooniprobe.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.data.TestData;
import org.openobservatory.ooniprobe.data.TestStorage;
import org.openobservatory.ooniprobe.model.NetworkMeasurement;
import org.openobservatory.ooniprobe.model.OONITests;
import org.openobservatory.ooniprobe.utils.Alert;
import android.support.v7.widget.PopupMenu;

import org.openobservatory.ooniprobe.utils.LogUtils;
import org.openobservatory.ooniprobe.view.ListImageButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
/*
public class TestsRunningListAdapter extends RecyclerView.Adapter<TestsRunningListAdapter.ViewHolder> {


    private static final String TAG = TestsRunningListAdapter.class.toString();

    private MainActivity mActivity;
    private ArrayList<NetworkMeasurement> values;
    private int context;
    OnItemClickListener mItemClickListener;

    public TestsRunningListAdapter(MainActivity context, ArrayList<NetworkMeasurement> values) {
        this.mActivity = context;
        this.values = values;
    }

    @Override
    public TestsRunningListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pending_test, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(TestsRunningListAdapter.ViewHolder holder, int position) {
        final NetworkMeasurement i = values.get(position);
        holder.txtTitle.setText(NetworkMeasurement.getTestName(mActivity, i.testName));
        holder.progressBar.setProgress(i.progress);
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public void setData(ArrayList<NetworkMeasurement> data) {
        values = data;
        notifyDataSetChanged();
    }

    public void addData(ArrayList<NetworkMeasurement> data) {
        for (NetworkMeasurement i : data) {
            values.add(i);
        }
        notifyDataSetChanged();
    }

    public void addTest(NetworkMeasurement test) {
        values.add(test);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtTitle;
        public ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txtTitle = (TextView) itemView.findViewById(R.id.test_title);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}

*/