package org.openobservatory.ooniprobe.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.ReachabilityManager;
import org.openobservatory.ooniprobe.domain.MeasurementsManager;
import org.openobservatory.ooniprobe.domain.callback.DomainCallback;
import org.openobservatory.ooniprobe.model.database.Measurement;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.app.fragment.MessageDialogFragment;

public class TextActivity extends AbstractActivity {
    private Measurement measurement;
    private String text;
    public static final int TYPE_LOG = 1;
    public static final int TYPE_JSON = 2;
    public static final int TYPE_UPLOAD_LOG = 3;
    private static final String TEST = "test";
    private static final String TYPE = "type";
    private static final String TEXT = "text";
    @BindView(R.id.textView)
    TextView textView;

    @Inject
    MeasurementsManager measurementsManager;

    public static Intent newIntent(Context context, int type, Measurement measurement) {
        return new Intent(context, TextActivity.class).putExtra(TYPE, type).putExtra(TEST, measurement);
    }

    public static Intent newIntent(Context context, int type, String text) {
        return new Intent(context, TextActivity.class).putExtra(TYPE, type).putExtra(TEXT, text);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setContentView(R.layout.text);
        ButterKnife.bind(this);
        showText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.clipboard, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clipboard:
                ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(getString(R.string.General_AppName), text));
                Toast.makeText(this, R.string.Toast_CopiedToClipboard, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showText() {
        switch (getIntent().getIntExtra(TYPE, 0)) {
            case TYPE_JSON:
                measurement = (Measurement) getIntent().getSerializableExtra(TEST);
                showJson();
                break;
            case TYPE_LOG:
                measurement = (Measurement) getIntent().getSerializableExtra(TEST);
                showLog();
                break;
            case TYPE_UPLOAD_LOG:
                text = (String) getIntent().getSerializableExtra(TEXT);
                showUploadLog();
                break;
        }
    }

    private void showLog() {
        try {
            text =  measurementsManager.getReadableLog(measurement);
            textView.setText(text);
        } catch (Exception e) {
            new MessageDialogFragment.Builder()
                    .withTitle(getString(R.string.Modal_Error_LogNotFound))
                    .build().show(getSupportFragmentManager(), null);
        }
    }

    private void showJson() {
        //Try to open file, if it doesn't exist dont show Error dialog immediately but try to download the json from internet
        try {
            text = measurementsManager.getReadableEntry(measurement);
            textView.setText(text);
        } catch (Exception e) {
            e.printStackTrace();
            if (ReachabilityManager.getNetworkType(this).equals(ReachabilityManager.NO_INTERNET)) {
                new MessageDialogFragment.Builder()
                        .withTitle(getString(R.string.Modal_Error))
                        .withMessage(getString(R.string.Modal_Error_RawDataNoInternet))
                        .build().show(getSupportFragmentManager(), null);
                return;
            }
            measurementsManager.downloadReport(measurement, new DomainCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    runOnUiThread(() -> {
                        text = result;
                        textView.setText(result);
                    });
                }

                @Override
                public void onError(String msg) {
                    runOnUiThread(() -> {
                        showError(msg);
                    });
                }
            });
        }
    }

    private void showUploadLog() {
        textView.setText(text);
    }

    private void showError(String msg) {
        new MessageDialogFragment.Builder()
                .withTitle(getString(R.string.Modal_Error))
                .withMessage(msg)
                .build().show(getSupportFragmentManager(), null);
    }
}
