package org.openobservatory.netprobe.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import org.openobservatory.netprobe.R;

/**
 * Created by lorenzo on 27/04/16.
 */
public class Alert {

    public static void alertScrollView(Context c, String filename) {
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myScrollView = inflater.inflate(R.layout.scroll_text, null, false);

        TextView tv = (TextView) myScrollView
                .findViewById(R.id.textViewWithScroll);

        tv.setText("");
        tv.append(LogUtils.readLogFile(c, filename));

        new AlertDialog.Builder(c).setView(myScrollView)
                .setTitle("Log View")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }

                }).show();
    }

    public static void alertWebView(Context c, String htmlfile) {
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myScrollView = inflater.inflate(R.layout.alert_webview, null, false);
        WebView wv = (WebView) myScrollView.findViewById(R.id.webview);
        wv.loadUrl("file:///android_asset/html/" + htmlfile + ".html");
        new AlertDialog.Builder(c).setView(myScrollView)
                .setTitle("Log View")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }

                }).show();
    }
}
