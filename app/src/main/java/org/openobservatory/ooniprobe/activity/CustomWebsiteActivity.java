package org.openobservatory.ooniprobe.activity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.common.collect.Lists;

import org.openobservatory.engine.OONIURLInfo;
import org.openobservatory.engine.OONIURLListResult;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.adapters.CustomWebsiteRecyclerViewAdapter;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.databinding.ActivityCustomwebsiteBinding;
import org.openobservatory.ooniprobe.domain.UrlsManager;
import org.openobservatory.ooniprobe.model.database.Url;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import localhost.toolkit.app.fragment.ConfirmDialogFragment;

public class CustomWebsiteActivity extends AbstractActivity implements ConfirmDialogFragment.OnConfirmedListener {

    private ActivityCustomwebsiteBinding binding;
    private ArrayList<EditText> editTexts;
    private ArrayList<ImageButton> deletes;

    @Inject
    PreferenceManager preferenceManager;

    @Inject
    UrlsManager urlsManager;

    private CustomWebsiteRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
//        setContentView(R.layout.activity_customwebsite);
        binding = ActivityCustomwebsiteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        ButterKnife.bind(this);
        editTexts = new ArrayList<>();
        deletes = new ArrayList<>();
        binding.bottomBar.inflateMenu(R.menu.run);
        binding.bottomBar.setOnMenuItemClickListener(item -> {
            int itemSize = adapter.getItemCount();
            List<String> items = adapter.getItems();
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
            suite.getTestList(preferenceManager)[0].setInputs(items);

            RunningActivity.runAsForegroundService(CustomWebsiteActivity.this, suite.asArray(), this::finish);
            return true;
        });
        binding.add.setOnClickListener(v -> add());
        binding.loadFromWebConnectivity.setOnClickListener(v -> loadFromWebConnectivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.urlContainer.setLayoutManager(layoutManager);
//        items = new ArrayList<>();
        adapter = new CustomWebsiteRecyclerViewAdapter(new ArrayList<>());
        binding.urlContainer.setAdapter(adapter);
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

    public boolean checkPrefix() {
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

    void add() {
        /*ViewGroup urlBox = (ViewGroup) getLayoutInflater().inflate(R.layout.edittext_url, binding.urlContainer, false);
        EditText editText = urlBox.findViewById(R.id.editText);
        editTexts.add(editText);
        binding.urlContainer.addView(urlBox);
        ImageButton delete = urlBox.findViewById(R.id.delete);
        deletes.add(delete);
        delete.setTag(editText);
        delete.setOnClickListener(v -> {
            EditText tag = (EditText) v.getTag();
            ((View) v.getParent()).setVisibility(View.GONE);
            editTexts.remove(tag);
            deletes.remove(v);
            binding.bottomBar.setTitle(getString(R.string.OONIRun_URLs, Integer.toString(editTexts.size())));
            setVisibilityDelete();
        });
        setVisibilityDelete();*/
        adapter.addAll(Collections.singletonList("http://google.com"));
        binding.bottomBar.setTitle(getString(R.string.OONIRun_URLs, Integer.toString(adapter.getItemCount())));
    }

    void loadFromWebConnectivity() {
        new FetchUrlsAndAddToList().execute();
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

    private final class FetchUrlsAndAddToList extends AsyncTask<Void, Void, OONIURLListResult> {

        @Override
        protected OONIURLListResult doInBackground(Void... params) {
            OONIURLListResult result = null;
            try {
                result = urlsManager.downloadUrls();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(OONIURLListResult result) {
            if (result != null) {
                adapter.addAll(Lists.transform(result.getUrls(), OONIURLInfo::getUrl));
                adapter.notifyDataSetChanged();
                binding.bottomBar.setTitle(getString(R.string.OONIRun_URLs, Integer.toString(adapter.getItemCount())));

                /*for (OONIURLInfo ooniurlInfo : result.getUrls()) {
                    ViewGroup urlBox = (ViewGroup) getLayoutInflater().inflate(R.layout.edittext_url, binding.urlContainer, false);
                    EditText editText = urlBox.findViewById(R.id.editText);
                    editText.setText(ooniurlInfo.getUrl());
                    editTexts.add(editText);
                    binding.urlContainer.addView(urlBox);
                    ImageButton delete = urlBox.findViewById(R.id.delete);
                    deletes.add(delete);
                    delete.setTag(editText);
                    delete.setOnClickListener(v -> {
                        EditText tag = (EditText) v.getTag();
                        ((View) v.getParent()).setVisibility(View.GONE);
                        editTexts.remove(tag);
                        deletes.remove(v);
                        binding.bottomBar.setTitle(getString(R.string.OONIRun_URLs, Integer.toString(editTexts.size())));
                        setVisibilityDelete();
                    });
                    setVisibilityDelete();
                    binding.bottomBar.setTitle(getString(R.string.OONIRun_URLs, Integer.toString(editTexts.size())));
                }*/
            }
        }
    }
}
