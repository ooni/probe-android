package org.openobservatory.ooniprobe.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.Measurement;
import org.openobservatory.ooniprobe.model.Result;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultFragment extends Fragment {
	@BindView(R.id.results) TextView results;
	@BindView(R.id.measurements) TextView measurements;

	@Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_result, container, false);
		ButterKnife.bind(this, v);
		long resultCount = SQLite.select(Method.count()).from(Result.class).count();
		long measurementCount = SQLite.select(Method.count()).from(Measurement.class).count();
		results.setText("Results: " + resultCount);
		measurements.setText("Measurements: " + measurementCount);
		return v;
	}
}
