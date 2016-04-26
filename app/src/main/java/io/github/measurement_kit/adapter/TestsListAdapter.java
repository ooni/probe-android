package io.github.measurement_kit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import io.github.measurement_kit.activity.MainActivity;
import io.github.measurement_kit.app.R;
import io.github.measurement_kit.data.TestData;
import io.github.measurement_kit.model.NetworkMeasurement;

import java.util.ArrayList;

/**
 * Created by lorenzo on 26/04/16.
 */
public class TestsListAdapter extends RecyclerView.Adapter<TestsListAdapter.ViewHolder> {
    private static final String TAG = TestsListAdapter.class.toString();

    private MainActivity mActivity;
    private ArrayList<Integer> values;
    private int context;
    OnItemClickListener mItemClickListener;

    public TestsListAdapter(MainActivity context, ArrayList<Integer> values) {
        this.mActivity = context;
        this.values = values;
    }

    @Override
    public TestsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_finished_test, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(TestsListAdapter.ViewHolder holder, int position) {

        Integer i = values.get(position);
        NetworkMeasurement e = TestData.getInstance().mNetworkMeasurementsMap.get(i);
        /*
        if (e != null){
            holder.txtCompany.setText(e.company);
            holder.txtTitle.setText(e.title);
            holder.imgLogo.setImageUrl(e.logo);
        }
    */
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public void setData(ArrayList<Integer> data) {
        values.clear();
        for (Integer i : data) {
            values.add(i);
        }
        notifyDataSetChanged();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //public RoundedImageView imgLogo;
        public TextView txtCompany;
        public TextView txtTitle;

        public ViewHolder(View itemView) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);
            itemView.setOnClickListener(this);
            //imgLogo = (RoundedImageView) itemView.findViewById(R.id.chat_logo);
            //txtCompany = (TextView) itemView.findViewById(R.id.chat_company);
            //txtTitle = (TextView) itemView.findViewById(R.id.chat_title);
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

