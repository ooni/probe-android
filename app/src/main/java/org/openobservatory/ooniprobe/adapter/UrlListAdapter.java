package org.openobservatory.ooniprobe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.TestResult;

import java.util.ArrayList;

public class UrlListAdapter extends RecyclerView.Adapter<UrlListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> values;
    private Boolean images;

    public UrlListAdapter(Context context, ArrayList<String> items, Boolean images) {
        this.context = context;
        this.values = items;
        this.images = images;
    }


    @Override
    public UrlListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (images)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_url, parent, false);
        else
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_url_noimg, parent, false);

        UrlListAdapter.ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(UrlListAdapter.ViewHolder holder, final int position) {
        final String i = values.get(position);
        holder.txtTitle.setText(i);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txtTitle = (TextView) itemView.findViewById(R.id.row_title);
        }

        @Override
        public void onClick(View v) {
        }
    }

    public void setData(ArrayList<String> data) {
        values = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

}

