package org.openobservatory.ooniprobe.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class OoniWebViewClient  extends WebViewClient {
    private ProgressBar mPbar = null;

    public OoniWebViewClient (ProgressBar bar){
        mPbar = bar;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        mPbar.setVisibility(View.GONE);
        super.onPageFinished(view, url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        mPbar.setVisibility(View.VISIBLE);
        super.onPageStarted(view, url, favicon);
    }

}