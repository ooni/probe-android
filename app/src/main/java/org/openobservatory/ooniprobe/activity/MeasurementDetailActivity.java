package org.openobservatory.ooniprobe.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.common.util.IOUtils;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.fragment.measurement.DashFragment;
import org.openobservatory.ooniprobe.fragment.measurement.FacebookMessengerFragment;
import org.openobservatory.ooniprobe.fragment.measurement.HttpHeaderFieldManipulationFragment;
import org.openobservatory.ooniprobe.fragment.measurement.HttpInvalidRequestLineFragment;
import org.openobservatory.ooniprobe.fragment.measurement.NdtFragment;
import org.openobservatory.ooniprobe.fragment.measurement.TelegramFragment;
import org.openobservatory.ooniprobe.fragment.measurement.WebConnectivityFragment;
import org.openobservatory.ooniprobe.fragment.measurement.WhatsappFragment;
import org.openobservatory.ooniprobe.fragment.resultHeader.ResultHeaderDetailFragment;
import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Measurement_Table;
import org.openobservatory.ooniprobe.model.database.Network;
import org.openobservatory.ooniprobe.test.test.Dash;
import org.openobservatory.ooniprobe.test.test.FacebookMessenger;
import org.openobservatory.ooniprobe.test.test.HttpHeaderFieldManipulation;
import org.openobservatory.ooniprobe.test.test.HttpInvalidRequestLine;
import org.openobservatory.ooniprobe.test.test.Ndt;
import org.openobservatory.ooniprobe.test.test.Telegram;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;
import org.openobservatory.ooniprobe.test.test.Whatsapp;

import java.io.FileInputStream;
import java.io.InputStreamReader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MeasurementDetailActivity extends AbstractActivity {
	public static final String ID = "id";
	public Measurement measurement;
	@BindView(R.id.toolbar) Toolbar toolbar;

	public static Intent newIntent(Context context, int id) {
		return new Intent(context, MeasurementDetailActivity.class).putExtra(ID, id);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		measurement = SQLite.select().from(Measurement.class).where(Measurement_Table.id.eq(getIntent().getIntExtra(ID, 0))).querySingle();
		assert measurement != null;
		measurement.result.load();
		setTheme(measurement.result.getTestSuite().getThemeLight());
		setContentView(R.layout.activity_measurement_detail);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		ActionBar bar = getSupportActionBar();
		if (bar != null) {
			bar.setDisplayHomeAsUpEnabled(true);
			bar.setTitle(measurement.getTest().getLabelResId());
		}
		Fragment detail;
		switch (measurement.test_name) {
			case Dash.NAME:
				detail = DashFragment.newInstance(measurement);
				break;
			case FacebookMessenger.NAME:
				detail = FacebookMessengerFragment.newInstance(measurement);
				break;
			case HttpHeaderFieldManipulation.NAME:
				detail = HttpHeaderFieldManipulationFragment.newInstance(measurement);
				break;
			case HttpInvalidRequestLine.NAME:
				detail = HttpInvalidRequestLineFragment.newInstance(measurement);
				break;
			case Ndt.NAME:
				detail = NdtFragment.newInstance(measurement);
				break;
			case Telegram.NAME:
				detail = TelegramFragment.newInstance(measurement);
				break;
			case WebConnectivity.NAME:
				detail = WebConnectivityFragment.newInstance(measurement);
				break;
			case Whatsapp.NAME:
				detail = WhatsappFragment.newInstance(measurement);
				break;
			default:
				detail = null;
				break;
		}
		measurement.result.load();
		getFragmentManager().beginTransaction()
				.replace(R.id.head, ResultHeaderDetailFragment.newInstance(null, null, measurement.start_time, measurement.runtime, false, measurement.result.network.country_code, Network.toString(this, measurement.result.network, 3)))
				.replace(R.id.body, detail)
				.commit();
	}

	@OnClick(R.id.rawData) public void onRawDataClick() {
		try {
			FileInputStream is = openFileInput(Measurement.getEntryFileName(measurement.id, measurement.test_name));
			String json = new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(new InputStreamReader(is)).getAsJsonObject());
			startActivity(TextActivity.newIntent(this, json));
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@OnClick(R.id.viewLog) public void onViewLogClick() {
		try {
			FileInputStream is = openFileInput(Measurement.getLogFileName(measurement.result.id, measurement.test_name));
			String log = new String(IOUtils.toByteArray(is));
			startActivity(TextActivity.newIntent(this, log));
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
