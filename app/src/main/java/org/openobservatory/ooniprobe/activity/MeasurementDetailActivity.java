package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.model.Measurement_Table;
import org.openobservatory.ooniprobe.test.TestSuite;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeasurementDetailActivity extends AbstractActivity {
	public static final String ID = "id";
	@BindView(R.id.toolbar) Toolbar toolbar;
	private Measurement measurement;

	public static Intent newIntent(Context context, int id) {
		return new Intent(context, MeasurementDetailActivity.class).putExtra(ID, id);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		measurement = SQLite.select().from(Measurement.class).where(Measurement_Table.id.eq(getIntent().getIntExtra(ID, 0))).querySingle();
		assert measurement != null;
		measurement.result.load();
		switch (measurement.result.test_group_name) {
			case TestSuite.WEBSITES:
				setTheme(R.style.Theme_AppCompat_Light_DarkActionBar_App_NoActionBar_Websites);
				break;
			case TestSuite.INSTANT_MESSAGING:
				setTheme(R.style.Theme_AppCompat_Light_DarkActionBar_App_NoActionBar_InstantMessaging);
				break;
			case TestSuite.MIDDLE_BOXES:
				setTheme(R.style.Theme_AppCompat_Light_DarkActionBar_App_NoActionBar_MiddleBoxes);
				break;
			case TestSuite.PERFORMANCE:
				setTheme(R.style.Theme_AppCompat_Light_DarkActionBar_App_NoActionBar_Performance);
				break;
		}
		setContentView(R.layout.activity_result_detail);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		ActionBar bar = getSupportActionBar();
		if (bar != null) {
			bar.setDisplayHomeAsUpEnabled(true);
			bar.setTitle(measurement.test_name);
		}
	}
}
