package org.openobservatory.ooniprobe.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

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
}
