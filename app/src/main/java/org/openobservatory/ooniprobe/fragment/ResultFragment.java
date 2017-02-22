package org.openobservatory.ooniprobe.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.ResultActivity;
import org.openobservatory.ooniprobe.utils.Alert;
import org.openobservatory.ooniprobe.utils.JSONUtils;
import org.openobservatory.ooniprobe.utils.LogUtils;
import org.openobservatory.ooniprobe.utils.OoniWebViewClient;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class ResultFragment extends Fragment {
    private ResultActivity mActivity;
    private ProgressBar mPbar = null;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActivity = (ResultActivity) activity;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.view_log_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_result, container, false);
        setHasOptionsMenu(true);

        int position = this.getArguments().getInt("position");
        String jsonFilename = getActivity().getIntent().getExtras().getString("json_file");
        mPbar = (ProgressBar) v.findViewById(R.id.web_view_progress);
        File jsonFile = new File(mActivity.getFilesDir(), jsonFilename);
        try {
            JSONUtils.JSONL jsonl = new JSONUtils.JSONL(jsonFile);
            final String jsonLine = jsonl.getLineN(position);
            WebView wv = (WebView) v.findViewById(R.id.webview);
            wv.setWebViewClient(new OoniWebViewClient(mPbar));
            wv.getSettings().setJavaScriptEnabled(true);
            //TODO bug, I send userLocale = it and it doesnt work
            wv.addJavascriptInterface(new JSONUtils.InjectedJSON(Locale.getDefault().getLanguage()), "userLocale");
            wv.addJavascriptInterface(new JSONUtils.InjectedJSON(jsonLine), "MeasurementJSON");
            wv.loadUrl("file:///android_asset/webui/index.html");
        } catch (IOException e) {
            // XXX log the IO exception in some way
        } catch (RuntimeException e) {
            // XXX log the runtime exception in some way
        }
        return v;
    }
}
