package org.openobservatory.ooniprobe.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.data.TestData;

import java.util.ArrayList;
import java.util.List;

import us.feras.mdv.MarkdownView;

public class Alert {

    public static void alertDialog(Context c, String title, String text) {
        new AlertDialog.Builder(c)
                .setTitle(title)
                .setMessage(text)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }

                }).show();
    }

    public static void alertScrollView(Context c, String filename) {
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myScrollView = inflater.inflate(R.layout.scroll_text, null, false);

        TextView tv = (TextView) myScrollView
                .findViewById(R.id.textViewWithScroll);

        tv.setText("");
        tv.append(formatString(LogUtils.readLogFile(c, filename)));

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

    public static void alertMdWebView(Context c, String htmlfile) {
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myScrollView = inflater.inflate(R.layout.alert_md_webview, null, false);
        MarkdownView markdownView = (MarkdownView) myScrollView.findViewById(R.id.markdownView);
        markdownView.loadMarkdownFile("file:///android_asset/md/" + htmlfile + ".md","file:///android_asset/html/setup-mobile.css");
        new AlertDialog.Builder(c).setView(myScrollView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }

                }).show();
    }

    public static void resultWebView(final Context c, final String jsonfile) {
            resultWebView(c, jsonfile, 0);
        }

    public static void resultWebView(final Context c, final String jsonfile, int idx) {
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myScrollView = inflater.inflate(R.layout.alert_webview, null, false);

        WebView wv = (WebView) myScrollView.findViewById(R.id.webview);
        wv.getSettings().setJavaScriptEnabled(true);
        final String jsonContent = LogUtils.readLogFile(c, jsonfile);
        final String[] parts = LogUtils.getLogParts(c, jsonfile);
        wv.addJavascriptInterface(new InjectedJSON(parts[idx]), "MeasurementJSON");
        wv.loadUrl("file:///android_asset/webui/index.html");

        AlertDialog.Builder alert = new AlertDialog.Builder(c);
        alert.setView(myScrollView);
        alert.setTitle(R.string.test_result);
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        if (parts.length > 1)
            alert.setNeutralButton(R.string.change, new DialogInterface.OnClickListener() {
                @TargetApi(11)
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    List<String> listItems = new ArrayList<String>();
                    try {
                        for(String str:parts) {
                            JSONObject jsonObj = new JSONObject(str);
                            listItems.add("input " + jsonObj.getString("input"));
                        }
                    } catch (JSONException e) {
                        listItems.clear();
                        for(int i = 0; i < parts.length; i++)  {
                            listItems.add("Test " + i);
                        }
                    }
                    final CharSequence[] buttons = listItems.toArray(new CharSequence[listItems.size()]);
                    AlertDialog.Builder builder = new AlertDialog.Builder(c);
                    builder.setTitle(R.string.change);
                    builder.setItems(buttons,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Alert.resultWebView(c, jsonfile, which);
                                }
                            });
                    builder.create().show();
                }
            });
        alert.show();
    }

    public static String formatString(String text){

        StringBuilder json = new StringBuilder();
        String indentString = "";

        boolean inQuotes = false;
        boolean isEscaped = false;

        for (int i = 0; i < text.length(); i++) {
            char letter = text.charAt(i);

            switch (letter) {
                case '\\':
                    isEscaped = !isEscaped;
                    break;
                case '"':
                    if (!isEscaped) {
                        inQuotes = !inQuotes;
                    }
                    break;
                default:
                    isEscaped = false;
                    break;
            }

            if (!inQuotes && !isEscaped) {
                switch (letter) {
                    case '{':
                    case '[':
                        json.append("\n" + indentString + letter + "\n");
                        indentString = indentString + "\t";
                        json.append(indentString);
                        break;
                    case '}':
                    case ']':
                        indentString = indentString.replaceFirst("\t", "");
                        json.append("\n" + indentString + letter);
                        break;
                    case ',':
                        json.append(letter + "\n" + indentString);
                        break;
                    default:
                        json.append(letter);
                        break;
                }
            } else {
                json.append(letter);
            }
        }

        return json.toString();
    }


    public static class InjectedJSON {
        private String jsonData;

        public InjectedJSON(String json) {
            jsonData = json;
        }

        @JavascriptInterface
        public String get() {
            return jsonData;
        }
    }
}
