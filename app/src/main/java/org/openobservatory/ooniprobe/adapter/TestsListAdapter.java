package org.openobservatory.ooniprobe.adapter;

import android.graphics.Typeface;
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
import org.openobservatory.ooniprobe.utils.Alert;
import android.support.v7.widget.PopupMenu;
import org.openobservatory.ooniprobe.view.ListImageButton;

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
        if (i.completed) return 1;
        else return 0;
    }

    @Override
    public void onBindViewHolder(TestsListAdapter.ViewHolder holder, int position) {
        final NetworkMeasurement i = values.get(position);
        Typeface font = Typeface.createFromAsset(mActivity.getAssets(), "fonts/HelveticaNeue-Roman.otf");
        holder.txtTitle.setTypeface(font);
        holder.txtTitle.setText(i.testName);
        if (i.completed){
            // Set the item as the button's tag so it can be retrieved later
            holder.popupButton.setTag(values.get(position));
            // Set the fragment instance as the OnClickListener
            holder.popupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    // TODO Auto-generated method stub
                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            showPopupMenu(v);
                        }

                    });
                }
            });
            holder.logButton.setOnClickListener(
                    new ImageButton.OnClickListener() {
                        public void onClick(View v) {
                            Alert.resultWebView(mActivity, i.json_file);
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
        public ListImageButton popupButton;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txtTitle = (TextView) itemView.findViewById(R.id.test_title);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            logButton = (ImageButton) itemView.findViewById(R.id.log_button);
            popupButton = (ListImageButton) itemView.findViewById(R.id.test_popupmenu);
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
                        TestStorage ts = new TestStorage();;
                        ts.removeTest(mActivity, item);
                        TestData.getInstance().notifyObservers();
                        return true;
                }
                return false;
            }
        });
        // Finally show the PopupMenu
        popup.show();
    }
}

