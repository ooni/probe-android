package org.openobservatory.ooniprobe.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openobservatory.measurement_kit.Version;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.MainActivity;

public class AboutFragment extends Fragment {
    private MainActivity mActivity;
    private AppCompatButton ppButton;
    private AppCompatButton learn_moreButton;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActivity = (MainActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onViewSelected");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }


    @Override
    public void onResume() {
        super.onResume();
        mActivity.setTitle(mActivity.getString(R.string.about));
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        learn_moreButton = (AppCompatButton) v.findViewById(R.id.learn_more_button);
        learn_moreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ooni.torproject.org/"));
                startActivity(browserIntent);
            }
        });
        ppButton = (AppCompatButton) v.findViewById(R.id.privacy_policy_button);
        ppButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ooni.torproject.org/about/data-policy/"));
                startActivity(browserIntent);
            }
        });
        TextView version_text = (TextView) v.findViewById(R.id.ooniprobe_version);
        version_text.setText("ooniprobe: " + BuildConfig.VERSION_NAME + "\n" + "measurement-kit: " + Version.getVersion());
        return v;
    }
}
