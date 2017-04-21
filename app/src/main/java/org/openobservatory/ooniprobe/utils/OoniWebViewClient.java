package org.openobservatory.ooniprobe.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import org.openobservatory.ooniprobe.R;

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

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        final Context context = view.getContext();
        final String openURL = url;
        new AlertDialog.Builder(context)
                .setMessage(context.getString(R.string.open_url_alert) + "\n\n" + url)
                .setPositiveButton(context.getString(R.string.ok)
                        , new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        context.startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri.parse(openURL)));
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
        return true;
    }
}