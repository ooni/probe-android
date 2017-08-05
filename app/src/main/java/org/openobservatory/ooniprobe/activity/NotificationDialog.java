package org.openobservatory.ooniprobe.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.utils.Alert;

import java.util.ArrayList;

public class NotificationDialog extends Activity {
    private static final String TAG = "NotificationDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //hide activity title
        final String message = getIntent().getStringExtra("message");
        displayAlert(message);
    }

    private void displayAlert(String message)
    {

        new AlertDialog.Builder(NotificationDialog.this)
                .setTitle(getString(R.string.notifications))
                .setMessage(message)
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finish();
                            }
                        })
                .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent browserIntent = new Intent(NotificationDialog.this, BrowserActivity.class);
                                browserIntent.putExtra("payload", getIntent().getStringExtra("payload"));
                                startActivity(browserIntent);
                                finish();
                            }
                        })
                .show();
    }
}