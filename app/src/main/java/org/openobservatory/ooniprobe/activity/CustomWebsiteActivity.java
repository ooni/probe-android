package org.openobservatory.ooniprobe.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Url;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import localhost.toolkit.app.ConfirmDialogFragment;

public class CustomWebsiteActivity extends AbstractActivity implements ConfirmDialogFragment.OnConfirmedListener {
	@BindView(R.id.urlContainer) LinearLayout urlContainer;
	@BindView(R.id.bottomBar) Toolbar bottomBar;
	private ArrayList<EditText> editTexts;
	private ArrayList<ImageButton> deletes;

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customwebsite);
		ButterKnife.bind(this);
		editTexts = new ArrayList<>();
		deletes = new ArrayList<>();
		bottomBar.inflateMenu(R.menu.run);
		bottomBar.setOnMenuItemClickListener(item -> {
			ArrayList<String> urls = new ArrayList<>(editTexts.size());
			for (EditText editText : editTexts) {
				String value = editText.getText().toString();
				if (Patterns.WEB_URL.matcher(value).matches())
					urls.add(Url.checkExistingUrl(value).toString());
			}
			WebsitesSuite suite = new WebsitesSuite();
			suite.getTestList(getPreferenceManager())[0].setInputs(urls);
			Intent intent = RunningActivity.newIntent(CustomWebsiteActivity.this, suite);
			if (intent != null) {
				ActivityCompat.startActivity(CustomWebsiteActivity.this, intent, null);
				finish();
			}
			return true;
		});
		add();
	}

	@Override public void onBackPressed() {
		String base = getString(R.string.http);
		boolean edited = false;
		for (EditText editText : editTexts)
			if (!editText.getText().toString().equals(base)) {
				edited = true;
				break;
			}
		if (edited)
			ConfirmDialogFragment.newInstance(null, getString(R.string.General_AppName), getString(R.string.Modal_CustomURL_NotSaved)).show(getSupportFragmentManager(), null);
		else
			super.onBackPressed();
	}

	@Override public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}

	@OnClick(R.id.add) void add() {
		ViewGroup urlBox = (ViewGroup) getLayoutInflater().inflate(R.layout.edittext_url, urlContainer, false);
		EditText editText = urlBox.findViewById(R.id.editText);
		editTexts.add(editText);
		urlContainer.addView(urlBox);
		ImageButton delete = urlBox.findViewById(R.id.delete);
		deletes.add(delete);
		delete.setTag(editText);
		delete.setOnClickListener(v -> {
			EditText tag = (EditText) v.getTag();
			((View) v.getParent()).setVisibility(View.GONE);
			editTexts.remove(tag);
			deletes.remove(v);
			setVisibilityDelete();
		});
		setVisibilityDelete();
		bottomBar.setTitle(getString(R.string.OONIRun_URLs, Integer.toString(editTexts.size())));
	}

	private void setVisibilityDelete() {
		for (ImageButton delete : deletes)
			delete.setVisibility(deletes.size() > 1 ? View.VISIBLE : View.INVISIBLE);
	}

	@Override public void onConfirmation(Serializable serializable, int i) {
		if (i == DialogInterface.BUTTON_POSITIVE)
			super.onBackPressed();
	}
}
