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

public class CustomWebsiteActivity extends AbstractActivity {
	@BindView(R.id.urlContainer) LinearLayout urlContainer;
	private ArrayList<EditText> editTexts;

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customwebsite);
		ButterKnife.bind(this);
		editTexts = new ArrayList<>();
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
		ArrayList<String> urls = new ArrayList<>(editTexts.size());
		for (EditText editText : editTexts) {
			String value = editText.getText().toString();
			if (Patterns.WEB_URL.matcher(value).matches())
				urls.add(Url.checkExistingUrl(value).toString());
		}
		WebsitesSuite suite = new WebsitesSuite();
		suite.getTestList(getPreferenceManager())[0].setInputs(urls);
		startActivity(RunningActivity.newIntent(this, suite));
		finish();
	}

	@OnClick(R.id.add) void add() {
		LinearLayout urlBox = (LinearLayout) getLayoutInflater().inflate(R.layout.edittext_url, urlContainer, false);
		EditText editText = urlBox.findViewById(R.id.editText);
		ImageButton delete = urlBox.findViewById(R.id.delete);
		editTexts.add(editText);
		urlContainer.addView(urlBox);
		delete.setTag(editText);
		delete.setOnClickListener(v -> {
			EditText tag = (EditText) v.getTag();
			((View) v.getParent()).setVisibility(View.GONE);
			editTexts.remove(tag);
		});
	}
}
