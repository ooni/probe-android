package org.openobservatory.ooniprobe.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.CountlyManager;
import org.openobservatory.ooniprobe.fragment.PreferenceFragment;
import org.openobservatory.ooniprobe.model.database.Result;

import androidx.annotation.Nullable;
import androidx.annotation.XmlRes;

import java.io.Serializable;

import localhost.toolkit.app.fragment.ConfirmDialogFragment;

public class PreferenceActivity extends AbstractActivity implements ConfirmDialogFragment.OnConfirmedListener {
	PreferenceFragment fragment;

	public static Intent newIntent(Context context, @XmlRes int preferenceResId, String rootKey) {
		return new Intent(context, PreferenceActivity.class).putExtra(PreferenceFragment.ARG_PREFERENCES_RES_ID, preferenceResId).putExtra(PreferenceFragment.ARG_PREFERENCE_ROOT, rootKey);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragment = PreferenceFragment.newInstance(getIntent().getIntExtra(PreferenceFragment.ARG_PREFERENCES_RES_ID, 0), android.R.id.content, getIntent().getStringExtra(PreferenceFragment.ARG_PREFERENCE_ROOT));
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	public void removeAllTests(View view){
		new ConfirmDialogFragment.Builder()
				.withExtra(R.id.delete)
				.withMessage(getString(R.string.Modal_DoYouWantToDeleteAllTests))
				.withPositiveButton(getString(R.string.Modal_Delete))
				.build().show(getSupportFragmentManager(), null);
	}

	@Override
	public void onConfirmation(Serializable serializable, int i) {
		if (i == DialogInterface.BUTTON_POSITIVE) {
			if (serializable.equals(R.id.delete)) {
				CountlyManager.recordEvent("ClearStorage");
				//From https://guides.codepath.com/android/using-dialogfragment
				ProgressDialog pd = new ProgressDialog(this);
				pd.setCancelable(false);
				pd.show();
				Result.deleteAll(this);
				pd.dismiss();
				fragment.setStorage();
			}
		}
	}
}
