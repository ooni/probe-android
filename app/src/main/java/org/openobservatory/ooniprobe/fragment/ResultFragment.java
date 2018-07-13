package org.openobservatory.ooniprobe.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.Result;
import org.openobservatory.ooniprobe.model.Result_Table;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultFragment extends Fragment {
	@BindView(R.id.toolbar) Toolbar toolbar;
	@BindView(R.id.tests) TextView tests;
	@BindView(R.id.networks) TextView networks;
	@BindView(R.id.dataUsage) TextView dataUsage;

	@Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_result, container, false);
		ButterKnife.bind(this, v);
		((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
		getActivity().setTitle(R.string.TestResults_Overview_Title);
		tests.setText(String.format(Locale.getDefault(), "%d", SQLite.selectCountOf().from(Result.class).count()));
		networks.setText(String.format(Locale.getDefault(), "%d", SQLite.selectCountOf(Result_Table.asn.distinct()).from(Result.class).count()));
		dataUsage.setText(String.format(Locale.getDefault(), "%d", SQLite.select(Method.sum(Result_Table.dataUsageDown)).from(Result.class).count()));
		return v;
	}
}
