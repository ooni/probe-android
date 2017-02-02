package org.openobservatory.ooniprobe.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.ResultActivity;
import org.openobservatory.ooniprobe.data.TestData;
import org.openobservatory.ooniprobe.data.TestStorage;
import org.openobservatory.ooniprobe.model.NetworkMeasurement;
import org.openobservatory.ooniprobe.utils.Alert;
import android.support.v7.widget.PopupMenu;

import com.lb.auto_fit_textview.AutoResizeTextView;

import org.openobservatory.ooniprobe.utils.LogUtils;
import org.openobservatory.ooniprobe.view.ListImageButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class PastTestsListAdapter extends RecyclerView.Adapter<PastTestsListAdapter.ViewHolder> {


    private static final String TAG = PastTestsListAdapter.class.toString();

    private MainActivity mActivity;
    private ArrayList<NetworkMeasurement> values;
    private int context;
    OnItemClickListener mItemClickListener;

    public PastTestsListAdapter(MainActivity context, ArrayList<NetworkMeasurement> values) {
        this.mActivity = context;
        this.values = values;
    }

    @Override
    public PastTestsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_past_test, parent, false);
                break;
        }
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(PastTestsListAdapter.ViewHolder holder, int position) {
        final NetworkMeasurement i = values.get(position);
        holder.txtTitle.setText(NetworkMeasurement.getTestName(mActivity, i.testName));
        holder.txtTimestamp.setText(getDate(i.test_id));

        // Set the item as the button's tag so it can be retrieved later
        holder.popupButton.setTag(values.get(position));
        // Set the fragment instance as the OnClickListener
        holder.popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        showPopupMenu(v);
                    }

                });
            }
        });
        if (i.entry) {
            if (i.anomaly == 0) {
                holder.testImage.setImageResource(NetworkMeasurement.getTestImage(i.testName, i.anomaly));
                holder.txtTitle.setTextColor(getColor(mActivity, R.color.color_ooni_blue));
            }
            else if (i.anomaly == 1) {
                holder.testImage.setImageResource(NetworkMeasurement.getTestImage(i.testName, i.anomaly));
                holder.txtTitle.setTextColor(getColor(mActivity, R.color.color_warning_orange));
            }
            else if (i.anomaly == 2) {
                holder.testImage.setImageResource(NetworkMeasurement.getTestImage(i.testName, i.anomaly));
                holder.txtTitle.setTextColor(getColor(mActivity, R.color.color_bad_red));
            }
        }
        else {
            holder.testImage.setImageResource(NetworkMeasurement.getTestImage(i.testName, 1));
            holder.txtTitle.setTextColor(getColor(mActivity, R.color.color_warning_orange));
        }

        holder.viewResult.setOnClickListener(
                new ImageButton.OnClickListener() {
                    public void onClick(View v) {
                        goToResults(i);
                    }
                }
        );

        holder.itemView.setOnClickListener(
                new ImageButton.OnClickListener() {
                    public void onClick(View v) {
                        goToResults(i);
                    }
                }
        );

    }

    private void goToResults (NetworkMeasurement i){
        if (!i.viewed) TestStorage.setViewed(mActivity, i.test_id);
        if (i.entry){
            Intent intent = new Intent(mActivity, ResultActivity.class);
            intent.putExtra("json_file", i.json_file);
            intent.putExtra("test_name", i.testName);
            mActivity.startActivity(intent);
        }
        else {
            Alert.alertDialog(mActivity, mActivity.getString(R.string.no_result), mActivity.getString(R.string.no_result_msg));
        }
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
        public AutoResizeTextView txtTitle;
        public TextView txtTimestamp;
        public ListImageButton popupButton;
        public ImageView testImage;
        public Button viewResult;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txtTitle = (AutoResizeTextView) itemView.findViewById(R.id.test_title);
            txtTimestamp = (TextView) itemView.findViewById(R.id.test_timestamp);
            popupButton = (ListImageButton) itemView.findViewById(R.id.test_popupmenu);
            testImage = (ImageView) itemView.findViewById(R.id.test_logo);
            viewResult = (Button) itemView.findViewById(R.id.view_button);
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

    private void showPopupMenu(View view) {
        // Retrieve the clicked item from view's tag
        final NetworkMeasurement item = (NetworkMeasurement)view.getTag();
        // Create a PopupMenu, giving it the clicked view for an anchor
        PopupMenu popup = new PopupMenu(mActivity, view);
        // Inflate our menu resource into the PopupMenu's Menu
        popup.getMenuInflater().inflate(R.menu.popup_test, popup.getMenu());
        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_remove:
                        // Remove the item from the adapter
                        TestStorage ts = new TestStorage();
                        ts.removeTest(mActivity, item);
                        TestData.getInstance(mActivity).removeTest(item);
                        TestData.getInstance(mActivity).notifyObservers();
                        return true;
                }
                return false;
            }
        });
        // Finally show the PopupMenu
        popup.show();
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time);
        String date = DateFormat.format("yyyy-MM-dd kk:mm:ss", cal).toString();
        return date;
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

