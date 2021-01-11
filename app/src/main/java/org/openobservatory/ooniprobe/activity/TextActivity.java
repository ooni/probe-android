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

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import org.apache.commons.io.FileUtils;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.client.callback.GetMeasurementJsonCallback;
import org.openobservatory.ooniprobe.client.callback.GetMeasurementsCallback;
import org.openobservatory.ooniprobe.common.CountlyManager;
import org.openobservatory.ooniprobe.common.ReachabilityManager;
import org.openobservatory.ooniprobe.model.api.ApiMeasurement;
import org.openobservatory.ooniprobe.model.database.Measurement;

import java.io.File;
import java.nio.charset.Charset;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.app.fragment.MessageDialogFragment;
import okhttp3.Request;

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

	public static Intent newIntent(Context context, int type, Measurement measurement) {
		return new Intent(context, TextActivity.class).putExtra(TYPE, type).putExtra(TEST, measurement);
	}

	public static Intent newIntent(Context context, int type, String text) {
		return new Intent(context, TextActivity.class).putExtra(TYPE, type).putExtra(TEXT, text);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text);
		ButterKnife.bind(this);
		showText();
		CountlyManager.recordView("DataView");
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.clipboard, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.clipboard:
				((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(getString(R.string.General_AppName), text));
				Toast.makeText(this, R.string.Toast_CopiedToClipboard, Toast.LENGTH_SHORT).show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void showText(){
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

	private void showLog(){
		try {
			File logFile = Measurement.getLogFile(this, measurement.result.id, measurement.test_name);
			String log = FileUtils.readFileToString(logFile, Charset.forName("UTF-8"));
			text = log;
			textView.setText(log);
		} catch (Exception e) {
			new MessageDialogFragment.Builder()
					.withTitle(getString(R.string.Modal_Error_LogNotFound))
					.build().show(getSupportFragmentManager(), null);
		}
	}

	private void showJson(){
		//Try to open file, if it doesn't exist dont show Error dialog immediately but try to download the json from internet
		try {
			File entryFile = Measurement.getEntryFile(this, measurement.id, measurement.test_name);
			String json = FileUtils.readFileToString(entryFile, Charset.forName("UTF-8"));
			text = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(JsonParser.parseString(json));
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
			//measurement.getUrlString will return null when the measurement is not a web_connectivity
			getApiClient().getMeasurement(measurement.report_id, measurement.getUrlString()).enqueue(new GetMeasurementsCallback() {
				@Override
				public void onSuccess(ApiMeasurement.Result result) {
					//Download measurement data locally and displaying into the TextView
					getOkHttpClient().newCall(new Request.Builder().url(result.measurement_url).build()).enqueue(new GetMeasurementJsonCallback() {
						@Override
						public void onSuccess(String json) {
							text = json;
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									textView.setText(json);
								}
							});
						}
						@Override
						public void onError(String msg) {
							showError(msg);
						}
					});
				}
				@Override
				public void onError(String msg) {
					showError(msg);
				}
			});
		}
	}

	private void showUploadLog(){
		textView.setText(text);
	}

	private void showError(String msg){
		new MessageDialogFragment.Builder()
				.withTitle(getString(R.string.Modal_Error))
				.withMessage(msg)
				.build().show(getSupportFragmentManager(), null);
	}
}
