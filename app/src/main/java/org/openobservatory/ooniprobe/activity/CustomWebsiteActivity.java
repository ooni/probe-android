package org.openobservatory.ooniprobe.activity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.model.database.Url;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import localhost.toolkit.text.ErrorListenerInterface;
import localhost.toolkit.text.ErrorListenerList;
import localhost.toolkit.text.ErrorRegexListener;

public class CustomWebsiteActivity extends AbstractActivity {
	@BindView(R.id.urlContainer) LinearLayout urlContainer;
	private ErrorListenerList errorListenerList;

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customwebsite);
		ButterKnife.bind(this);
		errorListenerList = new ErrorListenerList();
		add();
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
			ArrayList<String> urls = new ArrayList<>(errorListenerList.size());
			for (ErrorListenerInterface eli : errorListenerList)
				urls.add(Url.checkExistingUrl(eli.getValue()).toString());
			WebsitesSuite suite = new WebsitesSuite();
			suite.getTestList(getPreferenceManager())[0].setInputs(urls);
			startActivity(RunningActivity.newIntent(this, suite));
			finish();
		}
	}

	@OnClick(R.id.add) void add() {
		LinearLayout urlBox = (LinearLayout) getLayoutInflater().inflate(R.layout.edittext_url, urlContainer, false);
		EditText editText = urlBox.findViewById(R.id.editText);
		ImageButton delete = urlBox.findViewById(R.id.delete);
		ErrorRegexListener errorRegexListener = new ErrorRegexListener(editText, Patterns.WEB_URL, "NEED STRING");
		errorListenerList.add(errorRegexListener);
		urlContainer.addView(urlBox);
		delete.setTag(errorRegexListener);
		delete.setOnClickListener(v -> {
			ErrorRegexListener tag = (ErrorRegexListener) v.getTag();
			((View) v.getParent()).setVisibility(View.GONE);
			errorListenerList.remove(tag);
		});
	}
}
