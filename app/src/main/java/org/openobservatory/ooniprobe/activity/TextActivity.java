package org.openobservatory.ooniprobe.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.openobservatory.ooniprobe.R;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TextActivity extends AbstractActivity {
	public static final String TEXT = "text";
	@BindView(R.id.textView) TextView textView;

	public static Intent newIntent(Context context, String text) {
		return new Intent(context, TextActivity.class).putExtra(TEXT, text);
	}

	@Override protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text);
		ButterKnife.bind(this);
		textView.setText(getIntent().getStringExtra(TEXT));
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.clipboard, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.clipboard:
				((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(getString(R.string.General_AppName), textView.getText().toString()));
				Toast.makeText(this, R.string.Toast_CopiedToClipboard, Toast.LENGTH_SHORT).show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
