package org.openobservatory.ooniprobe.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.openobservatory.ooniprobe.fragment.MyPreferenceFragment;

public class PreferenceActivity extends AppCompatActivity {
	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
	}
}
