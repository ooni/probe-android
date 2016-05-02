package org.openobservatory.netprobe.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import org.openobservatory.netprobe.activity.MainActivity;
import org.openobservatory.netprobe.R;
import org.openobservatory.netprobe.data.TestData;
import org.openobservatory.netprobe.model.NetworkMeasurement;
import org.openobservatory.netprobe.utils.Alert;

import java.util.ArrayList;

/**
 * Created by lorenzo on 26/04/16.
 */
public class TestsListAdapter extends RecyclerView.Adapter<TestsListAdapter.ViewHolder> {


    private static final String TAG = TestsListAdapter.class.toString();

    private MainActivity mActivity;
    private ArrayList<NetworkMeasurement> values;
    private int context;
    OnItemClickListener mItemClickListener;

    public TestsListAdapter(MainActivity context, ArrayList<NetworkMeasurement> values) {
        this.mActivity = context;
        this.values = values;
    }

    @Override
    public TestsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case 0:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pending_test, parent, false);
                break;
            case 1:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_finished_test, parent, false);
                break;
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pending_test, parent, false);
                break;
        }
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public int getItemViewType(int position) {
        NetworkMeasurement i = values.get(position);
        if (i.finished) return 1;
        else return 0;
    }

    @Override
    public void onBindViewHolder(TestsListAdapter.ViewHolder holder, int position) {
        final NetworkMeasurement i = values.get(position);
        Typeface font = Typeface.createFromAsset(mActivity.getAssets(), "fonts/Inconsolata.otf");
        holder.txtTitle.setTypeface(font);
        holder.txtTitle.setText(i.testName);
        if (i.finished){
            holder.logButton.setOnClickListener(
                    new ImageButton.OnClickListener() {
                        public void onClick(View v) {
                            Alert.alertScrollView(mActivity, i.fileName);
                        }
                    }
            );
        }
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public void setData(ArrayList<NetworkMeasurement> data) {
        values.clear();
        this.addData(data);
    }

    public void addData(ArrayList<NetworkMeasurement> data) {
        for (NetworkMeasurement i : data) {
            values.add(i);
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtTitle;
        public ProgressBar progressBar;
        public ImageButton logButton;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txtTitle = (TextView) itemView.findViewById(R.id.test_title);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            logButton = (ImageButton) itemView.findViewById(R.id.log_button);
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

