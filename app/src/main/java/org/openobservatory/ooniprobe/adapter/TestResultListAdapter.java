package org.openobservatory.ooniprobe.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.fragment.ResultFragment;
import org.openobservatory.ooniprobe.model.NetworkMeasurement;

import java.util.ArrayList;

public class TestResultListAdapter extends RecyclerView.Adapter<TestResultListAdapter.ViewHolder> {


    private static final String TAG = TestResultListAdapter.class.toString();

    private FragmentActivity mActivity;
    private ArrayList<JSONObject> values;
    private int context;
    TestResultListAdapter.OnItemClickListener mItemClickListener;

    public TestResultListAdapter(FragmentActivity context, ArrayList<JSONObject> values) {
        this.mActivity = context;
        this.values = values;
    }

    @Override
    public TestResultListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_result_test, parent, false);
        TestResultListAdapter.ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(TestResultListAdapter.ViewHolder holder, final int position) {
        final JSONObject i = values.get(position);
        System.out.println(i);
        try {
            holder.txtTitle.setText(i.getString("input"));
        } catch (JSONException e) {
            holder.txtTitle.setText(position);
        }

        try {
            if (!i.getJSONObject("test_keys").getBoolean("blocking"))
                holder.testImage.setImageResource(NetworkMeasurement.getTestImage(i.getString("test_name"), true));
            else
                holder.testImage.setImageResource(NetworkMeasurement.getTestImage(i.getString("test_name"), false));
        } catch (JSONException e) {
            holder.testImage.setImageResource(0);
        }

        holder.itemView.setOnClickListener(
                new ImageButton.OnClickListener() {
                    public void onClick(View v) {
                        Fragment fragment = new ResultFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("position", position);
                        fragment.setArguments(bundle);
                        FragmentManager fm = mActivity.getSupportFragmentManager();
                        FragmentTransaction ft=fm.beginTransaction();
                        ft.replace(R.id.fragment,fragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public void setData(ArrayList<JSONObject> data) {
        values = data;
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtTitle;
        public ImageView testImage;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txtTitle = (TextView) itemView.findViewById(R.id.test_title);
            testImage = (ImageView) itemView.findViewById(R.id.test_logo);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public void setOnItemClickListener(final TestResultListAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}

