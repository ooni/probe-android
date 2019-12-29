package org.openobservatory.ooniprobe.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.client.callback.GetMeasurementJsonCallback;
import org.openobservatory.ooniprobe.client.callback.GetMeasurementsCallback;
import org.openobservatory.ooniprobe.common.ReachabilityManager;
import org.openobservatory.ooniprobe.model.api.ApiMeasurement;
import org.openobservatory.ooniprobe.model.database.Measurement;

import androidx.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.File;
import java.nio.charset.Charset;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.app.fragment.MessageDialogFragment;
import okhttp3.Request;

public class TextActivity extends AbstractActivity {
	private Measurement measurement;
	private String text;
	private static final int TYPE_LOG = 1;
	private static final int TYPE_JSON = 2;
	private static final String TEST = "test";
	private static final String TYPE = "type";
	@BindView(R.id.webView) WebView webView;
	@BindView(R.id.progressBar) ProgressBar progressBar;

	public static Intent newIntent(Context context, int type, Measurement measurement) {
		return new Intent(context, TextActivity.class).putExtra(TYPE, type).putExtra(TEST, measurement);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text);
		ButterKnife.bind(this);
		measurement = (Measurement) getIntent().getSerializableExtra(TEST);
		webView.setWebChromeClient(new WebChromeClient());
		setProgressBarVisibility(View.VISIBLE);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				setProgressBarVisibility(View.VISIBLE);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				setProgressBarVisibility(View.GONE);
			}

			@Override
			public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
				super.onReceivedError(view, request, error);
				new MessageDialogFragment.Builder()
						.withTitle(getString(R.string.Modal_Error))
						.withMessage(error.toString())
						.build().show(getSupportFragmentManager(), null);
				setProgressBarVisibility(View.GONE);
			}
		});
		showText();
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
				//Try to open file, if it doesn't exist dont show Error dialog immediately but try to download the json from internet
				try {
					File entryFile = Measurement.getEntryFile(this, measurement.id, measurement.test_name);
					String json = FileUtils.readFileToString(entryFile, Charset.forName("UTF-8"));
					json = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(new JsonParser().parse(json));
					text = json;
					webView.loadDataWithBaseURL(null, json, "text/plain", "UTF-8", null);
					//webView.loadData(json, "text/plain", "UTF-8");
				} catch (Exception e) {
					e.printStackTrace();
					if (ReachabilityManager.getNetworkType(this).equals(ReachabilityManager.NO_INTERNET)) {
						new MessageDialogFragment.Builder()
								.withTitle(getString(R.string.Modal_Error))
								.withMessage(getString(R.string.Modal_Error_RawDataNoInternet))
								.build().show(getSupportFragmentManager(), null);
					}
					getApiClient().getMeasurement(measurement.report_id, null).enqueue(new GetMeasurementsCallback() {
						@Override
						public void onSuccess(ApiMeasurement.Result result) {
							//Download measurement data locally and displaying into the WebView
							getOkHttpClient().newCall(new Request.Builder().url(result.measurement_url).build()).enqueue(new GetMeasurementJsonCallback() {
								@Override
								public void onSuccess(String json) {
									text = json;
									webView.loadData(json, "text/plain", "UTF-8");
								}
								@Override
								public void onError(String msg) {
									new MessageDialogFragment.Builder()
											.withTitle(getString(R.string.Modal_Error))
											.withMessage(msg)
											.build().show(getSupportFragmentManager(), null);
								}
							});
						}
						@Override
						public void onError(String msg) {
							new MessageDialogFragment.Builder()
									.withTitle(getString(R.string.Modal_Error))
									.withMessage(msg)
									.build().show(getSupportFragmentManager(), null);
						}
					});
				}
				break;
			case TYPE_LOG:
				try {
					File logFile = Measurement.getLogFile(this, measurement.result.id, measurement.test_name);
					String log = FileUtils.readFileToString(logFile, Charset.forName("UTF-8"));
					text = log;
					webView.loadDataWithBaseURL(null, log, "text/plain", "UTF-8", null);
				} catch (Exception e) {
					e.printStackTrace();
					new MessageDialogFragment.Builder()
							.withTitle(getString(R.string.Modal_Error_LogNotFound))
							.build().show(getSupportFragmentManager(), null);
				}
				break;
		}
	}

	private void setProgressBarVisibility(int visibility) {
		// If a user returns back, a NPE may occur if WebView is still loading a page and then tries to hide a ProgressBar.
		if (progressBar != null) {
			progressBar.setVisibility(visibility);
		}
	}

}
