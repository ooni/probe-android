package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ResubmitTask;
import org.openobservatory.ooniprobe.databinding.ActivityMeasurementDetailBinding;
import org.openobservatory.ooniprobe.domain.GetTestSuite;
import org.openobservatory.ooniprobe.domain.MeasurementsManager;
import org.openobservatory.ooniprobe.domain.callback.DomainCallback;
import org.openobservatory.ooniprobe.fragment.measurement.DashFragment;
import org.openobservatory.ooniprobe.fragment.measurement.FacebookMessengerFragment;
import org.openobservatory.ooniprobe.fragment.measurement.FailedFragment;
import org.openobservatory.ooniprobe.fragment.measurement.HeaderNdtFragment;
import org.openobservatory.ooniprobe.fragment.measurement.HeaderOutcomeFragment;
import org.openobservatory.ooniprobe.fragment.measurement.HttpHeaderFieldManipulationFragment;
import org.openobservatory.ooniprobe.fragment.measurement.HttpInvalidRequestLineFragment;
import org.openobservatory.ooniprobe.fragment.measurement.NdtFragment;
import org.openobservatory.ooniprobe.fragment.measurement.PsiphonFragment;
import org.openobservatory.ooniprobe.fragment.measurement.RiseupVPNFragment;
import org.openobservatory.ooniprobe.fragment.measurement.SignalFragment;
import org.openobservatory.ooniprobe.fragment.measurement.TelegramFragment;
import org.openobservatory.ooniprobe.fragment.measurement.TorFragment;
import org.openobservatory.ooniprobe.fragment.measurement.WebConnectivityFragment;
import org.openobservatory.ooniprobe.fragment.measurement.WhatsappFragment;
import org.openobservatory.ooniprobe.fragment.resultHeader.ResultHeaderDetailFragment;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Network;
import org.openobservatory.ooniprobe.test.suite.OONIRunSuite;
import org.openobservatory.ooniprobe.test.suite.PerformanceSuite;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.FacebookMessenger;
import org.openobservatory.ooniprobe.test.test.HttpHeaderFieldManipulation;
import org.openobservatory.ooniprobe.test.test.HttpInvalidRequestLine;
import org.openobservatory.ooniprobe.test.test.Ndt;
import org.openobservatory.ooniprobe.test.test.Psiphon;
import org.openobservatory.ooniprobe.test.test.RiseupVPN;
import org.openobservatory.ooniprobe.test.test.Signal;
import org.openobservatory.ooniprobe.test.test.Telegram;
import org.openobservatory.ooniprobe.test.test.Tor;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;
import org.openobservatory.ooniprobe.test.test.Whatsapp;

import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;

import javax.inject.Inject;

import io.noties.markwon.Markwon;
import localhost.toolkit.app.fragment.ConfirmDialogFragment;

public class MeasurementDetailActivity extends AbstractActivity implements ConfirmDialogFragment.OnConfirmedListener {
    private static final String ID = "id";
    private static final String RERUN_KEY = "rerun";


	private ActivityMeasurementDetailBinding binding;

    private Measurement measurement;

    private Snackbar snackbar;
    private Boolean isInExplorer;

    @Inject
    MeasurementsManager measurementsManager;

    @Inject
    PreferenceManager pm;

    @Inject
    GetTestSuite getTestSuite;

    @Inject
    PreferenceManager preferenceManager;


	public static Intent newIntent(Context context, int id) {
        return new Intent(context, MeasurementDetailActivity.class).putExtra(ID, id);
    }

	public void setThemeColor(int color) {
		Window window = getWindow();
		window.setStatusBarColor(color);
		binding.appbarLayout.setBackgroundColor(color);
		binding.appbarLayout.setBackgroundColor(color);
	}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        measurement = measurementsManager.get(getIntent().getIntExtra(ID, 0));
        assert measurement != null;
        measurement.result.load();
		if (measurement.is_failed) {
			setTheme(R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_Failed);
		} else {
			if (!measurement.result.test_group_name.equals(PerformanceSuite.NAME)) {
				if (measurement.is_anomaly) {
					setTheme(R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_Failure);
				} else {
					setTheme(R.style.Theme_MaterialComponents_Light_DarkActionBar_App_NoActionBar_Success);
				}
			}
		}
		binding = ActivityMeasurementDetailBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

		// TODO(aanorbel) Handle performance suite
		if (measurement.result.test_group_name.equals(OONIRunSuite.NAME)) {
			setThemeColor(((OONIRunSuite)measurement.result.getTestSuite()).getDescriptor().getParsedColor());
		}

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(measurement.getTest().getLabelResId());
        }
		setUpClickListeners();
        Fragment detail = null;
        Fragment head = null;
        if (measurement.is_failed) {
            head = HeaderOutcomeFragment.newInstance(R.drawable.error_48dp,
                    getString(R.string.bold,
                            getString(R.string.outcomeHeader,
                                    getString(R.string.TestResults_Details_Failed_Title),
                                    getString(R.string.TestResults_Details_Failed_Paragraph))));
            detail = FailedFragment.newInstance(measurement);
        } else {
            int iconRes = measurement.is_anomaly ? R.drawable.exclamation_white_48dp : R.drawable.tick_white_48dp;
            switch (measurement.test_name) {
                case Dash.NAME:
                    head = HeaderOutcomeFragment.newInstance(null,
                            getString(R.string.outcomeHeader,
                                    getString(measurement.getTestKeys().getVideoQuality(true)),
                                    getString(R.string.TestResults_Details_Performance_Dash_VideoWithoutBuffering,
                                            getString(measurement.getTestKeys().getVideoQuality(false)))));
                    detail = DashFragment.newInstance(measurement);
                    break;
                case FacebookMessenger.NAME:
                    head = HeaderOutcomeFragment.newInstance(iconRes, getString(R.string.bold, getString(measurement.is_anomaly ?
                            R.string.TestResults_Details_InstantMessaging_FacebookMessenger_LikelyBlocked_Hero_Title :
                            R.string.TestResults_Details_InstantMessaging_FacebookMessenger_Reachable_Hero_Title)));
                    detail = FacebookMessengerFragment.newInstance(measurement);
                    break;
                case HttpHeaderFieldManipulation.NAME:
                    head = HeaderOutcomeFragment.newInstance(R.drawable.test_middle_boxes_white, getString(R.string.bold, getString(measurement.is_anomaly ?
                            R.string.TestResults_Details_Middleboxes_HTTPHeaderFieldManipulation_Found_Hero_Title :
                            R.string.TestResults_Details_Middleboxes_HTTPHeaderFieldManipulation_NotFound_Hero_Title)));
                    detail = HttpHeaderFieldManipulationFragment.newInstance(measurement);
                    break;
                case HttpInvalidRequestLine.NAME:
                    head = HeaderOutcomeFragment.newInstance(R.drawable.test_middle_boxes_white, getString(R.string.bold, getString(measurement.is_anomaly ?
                            R.string.TestResults_Details_Middleboxes_HTTPInvalidRequestLine_Found_Hero_Title :
                            R.string.TestResults_Details_Middleboxes_HTTPInvalidRequestLine_NotFound_Hero_Title)));
                    detail = HttpInvalidRequestLineFragment.newInstance(measurement);
                    break;
                case Ndt.NAME:
                    head = HeaderNdtFragment.newInstance(measurement);
                    detail = NdtFragment.newInstance(measurement);
                    break;
                case Telegram.NAME:
                    head = HeaderOutcomeFragment.newInstance(iconRes, getString(R.string.bold, getString(measurement.is_anomaly ?
                            R.string.TestResults_Details_InstantMessaging_Telegram_LikelyBlocked_Hero_Title :
                            R.string.TestResults_Details_InstantMessaging_Telegram_Reachable_Hero_Title)));
                    detail = TelegramFragment.newInstance(measurement);
                    break;
                case WebConnectivity.NAME:
                    head = HeaderOutcomeFragment.newInstance(iconRes, getString(R.string.outcomeHeader, measurement.url.url,
                            getString(measurement.is_anomaly ?
                                    R.string.TestResults_Details_Websites_LikelyBlocked_Hero_Title :
                                    R.string.TestResults_Details_Websites_Reachable_Hero_Title)));
                    detail = WebConnectivityFragment.newInstance(measurement);
                    break;
                case Whatsapp.NAME:
                    head = HeaderOutcomeFragment.newInstance(iconRes, getString(R.string.bold, getString(measurement.is_anomaly ?
                            R.string.TestResults_Details_InstantMessaging_WhatsApp_LikelyBlocked_Hero_Title :
                            R.string.TestResults_Details_InstantMessaging_WhatsApp_Reachable_Hero_Title)));
                    detail = WhatsappFragment.newInstance(measurement);
                    break;
                case Signal.NAME:
                    head = HeaderOutcomeFragment.newInstance(iconRes, getString(R.string.bold, getString(measurement.is_anomaly ?
                            R.string.TestResults_Details_InstantMessaging_Signal_LikelyBlocked_Hero_Title :
                            R.string.TestResults_Details_InstantMessaging_Signal_Reachable_Hero_Title)));
                    detail = SignalFragment.newInstance(measurement);
                    break;
                case Psiphon.NAME:
                    head = HeaderOutcomeFragment.newInstance(iconRes, getString(R.string.bold, getString(measurement.is_anomaly ?
                            R.string.TestResults_Details_Circumvention_Psiphon_Blocked_Hero_Title :
                            R.string.TestResults_Details_Circumvention_Psiphon_Reachable_Hero_Title)));
                    detail = PsiphonFragment.newInstance(measurement);
                    break;
                case Tor.NAME:
                    head = HeaderOutcomeFragment.newInstance(iconRes, getString(R.string.bold, getString(measurement.is_anomaly ?
                            R.string.TestResults_Details_Circumvention_Tor_Blocked_Hero_Title :
                            R.string.TestResults_Details_Circumvention_Tor_Reachable_Hero_Title)));
                    detail = TorFragment.newInstance(measurement);
                    break;
                case RiseupVPN.NAME:
                    head = HeaderOutcomeFragment.newInstance(iconRes, getString(R.string.bold, getString(measurement.is_anomaly ?
                            R.string.TestResults_Details_Circumvention_RiseupVPN_Blocked_Hero_Title :
                            R.string.TestResults_Details_Circumvention_RiseupVPN_Reachable_Hero_Title)));
                    detail = RiseupVPNFragment.newInstance(measurement);
                    break;
            }
        }
        assert detail != null && head != null;
        Network net = measurement.result.network;
        String cc = measurement.result.network.country_code;
        if (measurement.rerun_network != null && !measurement.rerun_network.isEmpty()) {
            Network network = new Gson().fromJson(measurement.rerun_network,Network.class);
            net = network;
            cc = network.country_code;
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.footer, ResultHeaderDetailFragment.newInstance(
                       true,
                       null,
                       null,
                       measurement.start_time,
                       measurement.runtime,
                       false,
                       cc,
                       net))
                .replace(R.id.body, detail)
                .replace(R.id.head, head)
                .commit();
        snackbar = Snackbar.make(binding.coordinatorLayout, R.string.Snackbar_ResultsNotUploaded_Text, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.Snackbar_ResultsNotUploaded_Upload, v1 -> runAsyncTask());
        Context c = this;
        isInExplorer = !measurement.hasReportFile(c);

        measurementsManager.checkReportAndDeleteIt(measurement, new DomainCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean found) {
                isInExplorer = found;
            }

            @Override
            public void onError(String msg) {
                isInExplorer = false;
            }
        });

        if (!measurement.hasLogFile(this))
            binding.log.setVisibility(View.GONE);
        if (!measurementsManager.hasReportId(measurement))
            binding.explorer.setVisibility(View.GONE);
        Markwon.builder(this)
                .build()
                .setMarkdown(binding.methodology, getString(R.string.TestResults_Details_Methodology_Paragraph, getString(measurement.getTest().getUrlResId())));
        load();
        binding.log.setOnClickListener(v -> logClick());
        binding.data.setOnClickListener(v -> dataClick());
        binding.explorer.setOnClickListener(v -> explorerClick());
    }

	private void setUpClickListeners() {
		binding.log.setOnClickListener((view) -> logClick());
		binding.data.setOnClickListener((view) -> dataClick());
		binding.explorer.setOnClickListener((view) -> explorerClick());
	}

	private void runAsyncTask() {
        new ResubmitAsyncTask(this, pm.getProxyURL()).execute(null, measurement.id);
    }

    private void load() {
        if (measurementsManager.canUpload(measurement))
            snackbar.show();
        else
            snackbar.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share, menu);
        if (Objects.equals(measurement.test_name, WebConnectivity.NAME) && measurement.is_anomaly) {
            MenuItem reRunItem = menu.findItem(R.id.reRun);
            reRunItem.setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shareExplorerUrl:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_TEXT, measurementsManager.getExplorerUrl(measurement));
                share.setType("text/plain");
                Intent shareIntent = Intent. createChooser(share, null);
                startActivity(shareIntent);
                return true;
            case R.id.reRun:
                new ConfirmDialogFragment.Builder()
                        .withExtra(RERUN_KEY)
                        .withMessage(getString(R.string.Modal_ReRun_Title))
                        .withPositiveButton(getString(R.string.Modal_ReRun_Websites_Run))
                        .build().show(getSupportFragmentManager(), null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void logClick() {
        startActivity(TextActivity.newIntent(this, TextActivity.TYPE_LOG, measurement));
    }

    void dataClick() {
        startActivity(TextActivity.newIntent(this, TextActivity.TYPE_JSON, measurement));
    }

    void explorerClick() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(measurementsManager.getExplorerUrl(measurement))));
    }

    @Override
    public void onConfirmation(Serializable extra, int buttonClicked) {
        if (buttonClicked == DialogInterface.BUTTON_POSITIVE && extra.equals(RERUN_KEY))
            RunningActivity.runAsForegroundService(
                    this,
                    getTestSuite.getForWebConnectivityReRunFrom(measurement.result, Collections.singletonList(measurement.url.url)).asArray(),
                    this::finish,
                    preferenceManager);
        else if (buttonClicked == DialogInterface.BUTTON_POSITIVE)
            runAsyncTask();
        else if (buttonClicked == DialogInterface.BUTTON_NEUTRAL)
            startActivity(TextActivity.newIntent(this, TextActivity.TYPE_UPLOAD_LOG, (String) extra));
    }

    public static class ResubmitAsyncTask extends ResubmitTask<MeasurementDetailActivity> {

        ResubmitAsyncTask(MeasurementDetailActivity activity, String proxy) {
            super(activity, proxy);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            MeasurementDetailActivity activity = getActivity();
            if (activity != null) {
                activity.measurement = d.measurementsManager.get(activity.measurement.id);
                activity.load();
                if (!result)
                    new ConfirmDialogFragment.Builder()
                            .withTitle(activity.getString(R.string.Modal_UploadFailed_Title))
                            .withMessage(activity.getString(R.string.Modal_UploadFailed_Paragraph, errors.toString(), totUploads.toString()))
                            .withPositiveButton(activity.getString(R.string.Modal_Retry))
                            .withNeutralButton(getActivity().getString(R.string.Modal_DisplayFailureLog))
                            .withExtra(String.join("\n", logger.logs))
                            .build().show(activity.getSupportFragmentManager(), null);
            }
        }
    }
}
