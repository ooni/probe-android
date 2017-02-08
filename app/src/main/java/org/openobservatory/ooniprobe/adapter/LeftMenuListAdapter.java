package org.openobservatory.ooniprobe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.data.TestStorage;

import java.util.List;

public class LeftMenuListAdapter extends ArrayAdapter<String> {
    private Context context;
    public LeftMenuListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public LeftMenuListAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.row_left_menu, null);
        }

        String p = getItem(position);

        if (p != null) {
            TextView txtTitle = (TextView) v.findViewById(R.id.row_title);
            ImageView testImage = (ImageView) v.findViewById(R.id.row_icon);

            if (txtTitle != null) {
                txtTitle.setText(p);
            }

            if (testImage != null) {
                if (position == 1 && TestStorage.newTests(context))
                    testImage.setVisibility(View.VISIBLE);
                else
                    testImage.setVisibility(View.GONE);
            }
        }

        return v;
    }

}
