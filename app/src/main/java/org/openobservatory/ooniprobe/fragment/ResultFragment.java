package org.openobservatory.ooniprobe.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.utils.Alert;
import org.openobservatory.ooniprobe.utils.LogUtils;

public class ResultFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_result, container, false);

        int position = this.getArguments().getInt("position");
        String json_file = getActivity().getIntent().getExtras().getString("json_file");
        final String[] parts = LogUtils.getLogParts(getActivity(), json_file);

        WebView wv = (WebView) v.findViewById(R.id.webview);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.addJavascriptInterface(new Alert.InjectedJSON(parts[position]), "MeasurementJSON");
        wv.loadUrl("file:///android_asset/webui/index.html");

        return v;
    }

}
