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

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.model.database.Url;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;

import java.io.Serializable;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import localhost.toolkit.app.fragment.ConfirmDialogFragment;

public class CustomWebsiteActivity extends AbstractActivity implements ConfirmDialogFragment.OnConfirmedListener {
    @BindView(R.id.urlContainer)
    LinearLayout urlContainer;
    @BindView(R.id.bottomBar)
    Toolbar bottomBar;
    private ArrayList<EditText> editTexts;
    private ArrayList<ImageButton> deletes;

    @Inject
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setContentView(R.layout.activity_customwebsite);
        ButterKnife.bind(this);
        editTexts = new ArrayList<>();
        deletes = new ArrayList<>();
        bottomBar.inflateMenu(R.menu.run);
        bottomBar.setOnMenuItemClickListener(item -> {
            if (!checkPrefix())
                return false;
            ArrayList<String> urls = new ArrayList<>(editTexts.size());
            for (EditText editText : editTexts) {
                String value = editText.getText().toString();
                String sanitizedUrl = value.replaceAll("\\r\\n|\\r|\\n", " ");
                //https://support.microsoft.com/en-us/help/208427/maximum-url-length-is-2-083-characters-in-internet-explorer
                if (Patterns.WEB_URL.matcher(sanitizedUrl).matches() && sanitizedUrl.length() < 2084)
                    urls.add(Url.checkExistingUrl(sanitizedUrl).toString());
            }
            WebsitesSuite suite = new WebsitesSuite();
            suite.getTestList(preferenceManager)[0].setInputs(urls);

            RunningActivity.runAsForegroundService(CustomWebsiteActivity.this, suite.asArray(), this::finish, preferenceManager);
            return true;
        });
        add();
    }

    @Override
    public void onBackPressed() {
        String base = getString(R.string.http);
        boolean edited = false;
        for (EditText editText : editTexts)
            if (!editText.getText().toString().equals(base)) {
                edited = true;
                break;
            }
        if (edited)
            new ConfirmDialogFragment.Builder()
                    .withMessage(getString(R.string.Modal_CustomURL_NotSaved))
                    .build().show(getSupportFragmentManager(), null);
        else
            super.onBackPressed();
    }

    public boolean checkPrefix(){
        boolean prefix = true;
        for (EditText editText : editTexts)
            if (!editText.getText().toString().contains("http://")
                    && !editText.getText().toString().contains("https://")) {
                prefix = false;
                editText.setError(getString(R.string.Settings_Websites_CustomURL_NoURLEntered));
            }
        return prefix;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @OnClick(R.id.add)
    void add() {
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
            bottomBar.setTitle(getString(R.string.OONIRun_URLs, Integer.toString(editTexts.size())));
            setVisibilityDelete();
        });
        setVisibilityDelete();
        bottomBar.setTitle(getString(R.string.OONIRun_URLs, Integer.toString(editTexts.size())));
    }

    private void setVisibilityDelete() {
        for (ImageButton delete : deletes)
            delete.setVisibility(deletes.size() > 1 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onConfirmation(Serializable serializable, int i) {
        if (i == DialogInterface.BUTTON_POSITIVE)
            super.onBackPressed();
    }
}
