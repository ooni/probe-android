package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;

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
}
