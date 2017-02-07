package org.openobservatory.ooniprobe.adapter;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.fragment.ResultFragment;
import org.openobservatory.ooniprobe.model.TestResult;

import com.lb.auto_fit_textview.AutoResizeTextView;

import junit.framework.Test;

import java.util.ArrayList;

public class TestResultListAdapter extends RecyclerView.Adapter<TestResultListAdapter.ViewHolder> {


    private static final String TAG = TestResultListAdapter.class.toString();

    private FragmentActivity mActivity;
    private ArrayList<TestResult> values;
    private int context;
    TestResultListAdapter.OnItemClickListener mItemClickListener;

    public TestResultListAdapter(FragmentActivity context, ArrayList<TestResult> values) {
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
        final TestResult i = values.get(position);
        holder.txtTitle.setText(i.input);
        if(i.anomaly == 2)
            holder.txtTitle.setTextColor(getColor(mActivity, R.color.color_bad_red));
        else if(i.anomaly == 0)
            holder.txtTitle.setTextColor(getColor(mActivity, R.color.color_ok_green));
        else
            holder.txtTitle.setTextColor(getColor(mActivity, R.color.color_warning_orange));

        holder.itemView.setOnClickListener(
                new ImageButton.OnClickListener() {
                    public void onClick(View v) {
                        showResult(position);
                    }
                }
        );

        holder.viewResult.setOnClickListener(
                new ImageButton.OnClickListener() {
                    public void onClick(View v) {
                        showResult(position);
                    }
                }
        );

    }

    private void showResult(int position){
        Fragment fragment = new ResultFragment();
        Bundle bundle = new Bundle();
        final TestResult i = values.get(position);
        bundle.putInt("position", position);
        fragment.setArguments(bundle);
        FragmentManager fm = mActivity.getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        ft.replace(R.id.fragment,fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public void setData(ArrayList<TestResult> data) {
        values = data;
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public AutoResizeTextView txtTitle;
        public Button viewResult;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txtTitle = (AutoResizeTextView) itemView.findViewById(R.id.test_title);
            viewResult = (Button) itemView.findViewById(R.id.view_button);
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

    public static final int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }

}

