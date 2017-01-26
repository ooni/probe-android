package org.openobservatory.ooniprobe.adapter;

import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.ResultActivity;
import org.openobservatory.ooniprobe.data.TestData;
import org.openobservatory.ooniprobe.data.TestStorage;
import org.openobservatory.ooniprobe.model.NetworkMeasurement;
import org.openobservatory.ooniprobe.utils.Alert;
import android.support.v7.widget.PopupMenu;

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

    /*
    @Override
    public int getItemViewType(int position) {
        NetworkMeasurement i = values.get(position);
        if (i.completed) return 1;
        else return 0;
    }
*/

    @Override
    public void onBindViewHolder(PastTestsListAdapter.ViewHolder holder, int position) {
        final NetworkMeasurement i = values.get(position);
        holder.txtTitle.setText(NetworkMeasurement.getTestName(mActivity, i.testName));

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
        if (i.completed) {
            holder.txtTimestamp.setText(getDate(i.test_id));
        }
        else {
            holder.txtTimestamp.setText("");
        }
        final String[] parts = LogUtils.getLogParts(mActivity, i.json_file);
        if (parts.length > 1)
                holder.testImage.setImageResource(NetworkMeasurement.getTestImage(NetworkMeasurement.getTestName(mActivity, i.testName), true));
        else if (parts.length == 1 && parts[0].length() == 0)
                holder.testImage.setImageResource(NetworkMeasurement.getTestImage(NetworkMeasurement.getTestName(mActivity, i.testName), false));
        else
                holder.testImage.setImageResource(NetworkMeasurement.getTestImage(NetworkMeasurement.getTestName(mActivity, i.testName), true));


        //final JSONObject i = values.get(position);

    /*
        try {
            if (!i.getJSONObject("test_keys").getBoolean("blocking"))
                holder.testImage.setImageResource(NetworkMeasurement.getTestImage(i.getString("test_name"), true));
            else
                holder.testImage.setImageResource(NetworkMeasurement.getTestImage(i.getString("test_name"), false));
        } catch (JSONException e) {
            holder.testImage.setImageResource(0);
        }
        */
        holder.itemView.setOnClickListener(
                new ImageButton.OnClickListener() {
                    public void onClick(View v) {
                        if (parts.length > 0){
                            Intent intent = new Intent(v.getContext(), ResultActivity.class);
                            intent.putExtra("json_file", i.json_file);
                            v.getContext().startActivity(intent);
                        }
                        else {
                            Alert.alertDialog(mActivity, mActivity.getString(R.string.no_result), mActivity.getString(R.string.no_result_msg));
                        }
                    }
                }
        );

    }

    /*
    public ArrayList getJson(String json_file){
        String json_file = getActivity().getIntent().getExtras().getString("json_file");
        final String[] parts = LogUtils.getLogParts(getActivity(), json_file);
        ArrayList<JSONObject> listItems = new ArrayList<JSONObject>();
        try {
            for(String str:parts) {
                JSONObject jsonObj = new JSONObject(str);
                listItems.add(jsonObj);
            }
        } catch (JSONException e) {
        }
        return listItems;
    }
    */

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
        public TextView txtTimestamp;
        public ListImageButton popupButton;
        public ImageView testImage;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txtTitle = (TextView) itemView.findViewById(R.id.test_title);
            txtTimestamp = (TextView) itemView.findViewById(R.id.test_timestamp);
            popupButton = (ListImageButton) itemView.findViewById(R.id.test_popupmenu);
            testImage = (ImageView) itemView.findViewById(R.id.test_logo);
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
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("yyyy-MM-dd HH:mm:ss", cal).toString();
        return date;
    }
}

