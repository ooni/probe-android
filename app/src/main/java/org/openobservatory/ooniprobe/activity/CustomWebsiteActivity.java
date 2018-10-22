package org.openobservatory.ooniprobe.activity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputLayout;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import localhost.toolkit.text.ErrorListenerList;
import localhost.toolkit.text.ErrorRegexListener;

public class CustomWebsiteActivity extends AbstractActivity {
	@BindView(R.id.urlContainer) LinearLayout urlContainer;
	private ErrorListenerList errorListenerList;

	// TODO ALE add in overview
	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customwebsite);
		ButterKnife.bind(this);
		errorListenerList = new ErrorListenerList();
		errorListenerList.add(new ErrorRegexListener(((TextInputLayout) urlContainer.getChildAt(0)).getEditText(), Patterns.WEB_URL, "is not a valid url"));
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.run, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.help:
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@OnClick(R.id.run) void runClick() {
		if (errorListenerList.matches()) {
			ArrayList<String> urls = new ArrayList<>(urlContainer.getChildCount());
			for (int i = 0; i < urlContainer.getChildCount(); i++)
				urls.add(((TextInputLayout) urlContainer.getChildAt(i)).getEditText().getText().toString());
			WebsitesSuite suite = new WebsitesSuite();
			suite.getTestList(getPreferenceManager())[0].setInputs(urls);
			startActivity(RunningActivity.newIntent(this, suite));
			finish();
		}
	}

	@OnClick(R.id.add) void add() {
		TextInputLayout textInputLayout = (TextInputLayout) getLayoutInflater().inflate(R.layout.edittext_url, urlContainer, false);
		errorListenerList.add(new ErrorRegexListener(textInputLayout.getEditText(), Patterns.WEB_URL, "is not a valid url"));
		urlContainer.addView(textInputLayout);
	}
}
