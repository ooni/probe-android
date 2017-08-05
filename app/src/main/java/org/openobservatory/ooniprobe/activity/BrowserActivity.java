package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openobservatory.ooniprobe.R;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.FALSE;

public class BrowserActivity extends AppCompatActivity implements View.OnClickListener {
    private static WebView webView;
    private static ProgressBar webViewProgressBar;
    private static ImageView back, forward, refresh, close, share;
    private static Button try_mirror;
    private static TextView urlLabel;
    private static ArrayList<String> urls;
    private static int urlIndex = 0;
    private static final String TAG = "BrowserActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        urls = new ArrayList<>();
        try {
            JSONObject payload = new JSONObject(getIntent().getStringExtra("payload"));
            String href = payload.getString("href");
            urls.add(href);
            JSONArray alt_hrefs = payload.getJSONArray("alt_hrefs");
            for(int i=0; i< alt_hrefs.length(); i++)
            {
                urls.add(alt_hrefs.getString(i));
            }
            Log.d(TAG, "Message data urls: " + urls);
        }
        catch (JSONException e){
            System.out.println("JSONException "+ e);
            finish();
        }
        initViews();
        setUpWebView();
        reloadButtons();
        setListeners();
     }

    private void initViews() {
        back = (ImageView) findViewById(R.id.webviewBack);
        forward = (ImageView) findViewById(R.id.webviewForward);
        refresh = (ImageView) findViewById(R.id.webviewReload);
        close = (ImageView) findViewById(R.id.webviewClose);
        webViewProgressBar = (ProgressBar) findViewById(R.id.webViewProgressBar);
        urlLabel = (TextView) findViewById(R.id.urlLabel);
        share = (ImageView) findViewById(R.id.shareButton);
        try_mirror = (Button) findViewById(R.id.tryMirror);
        if (urls.size() == 1)
            try_mirror.setEnabled(FALSE);
    }


    private void setUpWebView() {
        webView = (WebView) findViewById(R.id.sitesWebView);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setInitialScale(1);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        Log.d(TAG, "Loading: " + urls.get(urlIndex));
        LoadWebViewUrl(urls.get(urlIndex));
    }

    private void setListeners() {
        back.setOnClickListener(this);
        forward.setOnClickListener(this);
        refresh.setOnClickListener(this);
        close.setOnClickListener(this);
        share.setOnClickListener(this);
        try_mirror.setOnClickListener(this);
    }

    void reloadButtons(){
        if (webView.canGoBack()){
            back.setAlpha(.5f);
            back.setClickable(false);
        }
        else {
            back.setAlpha(.5f);
            back.setClickable(false);
        }
        if (webView.canGoForward()){
            forward.setAlpha(.5f);
            forward.setClickable(false);
        }
        else {
            forward.setAlpha(.5f);
            forward.setClickable(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.webviewBack:
                isWebViewCanGoBack();
                break;
            case R.id.webviewForward:
                if (webView.canGoForward())
                    webView.goForward();
                break;
            case R.id.webviewReload:
                String url = webView.getUrl();
                LoadWebViewUrl(url);
                break;
            case R.id.webviewClose:
                finish();
                break;
            case R.id.shareButton:
                shareUrl();
                break;
            case R.id.tryMirror:
                tryMirror();
                break;
        }
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            refresh.setVisibility(View.GONE);
            urlLabel.setText(getString(R.string.loading));
            if (!webViewProgressBar.isShown())
                webViewProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            refresh.setVisibility(View.VISIBLE);
            if (webViewProgressBar.isShown())
                webViewProgressBar.setVisibility(View.GONE);
            System.out.println("Finished loading " + url);
            if (url.substring(0, 5).equals("https"))
                urlLabel.setText("\uD83D\uDD12 " + url);
            else
                urlLabel.setText(url);
            reloadButtons();
        }

        //TODO keep the error toasts for all messages? are they necessary?
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            refresh.setVisibility(View.VISIBLE);
            if (webViewProgressBar.isShown())
                webViewProgressBar.setVisibility(View.GONE);
            //Toast.makeText(BrowserActivity.this, "Unexpected error occurred.Reload page again.", Toast.LENGTH_SHORT).show();
        }

        //TODO this should be removed includes error in internal frames
        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            refresh.setVisibility(View.VISIBLE);
            if (webViewProgressBar.isShown())
                webViewProgressBar.setVisibility(View.GONE);
            //TODO requies api 21
            //if (errorResponse.getStatusCode())
            //System.out.println("Error: "+ errorResponse.getStatusCode());
            //Toast.makeText(BrowserActivity.this, "Unexpected HTTP error occurred.Reload page again.", Toast.LENGTH_SHORT).show();
        }

        //TODO keep this?
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            refresh.setVisibility(View.VISIBLE);
            if (webViewProgressBar.isShown())
                webViewProgressBar.setVisibility(View.GONE);
            //Toast.makeText(BrowserActivity.this, "Unexpected SSL error occurred.Reload page again.", Toast.LENGTH_SHORT).show();
        }
    }

    // To handle "Back" key press event for WebView to go back to previous screen.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            isWebViewCanGoBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void shareUrl(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void tryMirror(){
        if ((urlIndex+1) < urls.size()){
            urlIndex++;
            LoadWebViewUrl(urls.get(urlIndex));
        }
        else {
            urlIndex = 0;
            LoadWebViewUrl(urls.get(urlIndex));
        }
    }

    private void isWebViewCanGoBack() {
        if (webView.canGoBack())
            webView.goBack();
        //else
        //    finish();
    }

    private void LoadWebViewUrl(String url) {
        if (isInternetConnected())
            webView.loadUrl(url);
        else {
            refresh.setVisibility(View.VISIBLE);
            //Toast.makeText(BrowserActivity.this, "Oops!! There is no internet connection. Please enable your internet connection.", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isInternetConnected() {
        // At activity startup we manually check the internet status and change
        // the text status
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;

    }

}
