package org.openobservatory.ooniprobe.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import org.openobservatory.ooniprobe.R;

public class Alert {
    public static void alertDialog(Context c, String title, String text) {
        new AlertDialog.Builder(c)
                .setTitle(title)
                .setMessage(text)
                .setPositiveButton(c.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
    }

    public static void alertDialogTwoButtons(Context c, String title, String text, DialogInterface.OnClickListener ocl) {
        new AlertDialog.Builder(c)
                .setTitle(title)
                .setMessage(text)
                .setNegativeButton(c.getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton(c.getString(R.string.ok), ocl).show();
    }

}
