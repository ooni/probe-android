package org.openobservatory.ooniprobe.activity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

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

    @Inject
    PreferenceManager preferenceManager;

    @Inject
    UrlsManager urlsManager;

    private ActivityCustomwebsiteBinding binding;

    private CustomWebsiteRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        binding = ActivityCustomwebsiteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomBar.inflateMenu(R.menu.run);
        binding.bottomBar.setOnMenuItemClickListener(item -> {
            List<String> items = adapter.getItems();
            ArrayList<String> urls = new ArrayList<>(items.size());
            for (String value : items) {
                String sanitizedUrl = value.replaceAll("\\r\\n|\\r|\\n", " ");
                //https://support.microsoft.com/en-us/help/208427/maximum-url-length-is-2-083-characters-in-internet-explorer
                if (Patterns.WEB_URL.matcher(sanitizedUrl).matches() && sanitizedUrl.length() < 2084)
                    urls.add(Url.checkExistingUrl(sanitizedUrl).toString());
            }
            WebsitesSuite suite = new WebsitesSuite();
            suite.getTestList(preferenceManager)[0].setInputs(urls);

            RunningActivity.runAsForegroundService(CustomWebsiteActivity.this, suite.asArray(), this::finish);
            return true;
        });
        binding.add.setOnClickListener(v -> add());
        binding.loadFromWebConnectivity.setOnClickListener(v -> loadFromWebConnectivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.urlContainer.setLayoutManager(layoutManager);
        adapter = new CustomWebsiteRecyclerViewAdapter(input -> {
            binding.bottomBar.setTitle(getString(R.string.OONIRun_URLs, Integer.toString(adapter.getItemCount())));
        });
        binding.urlContainer.setAdapter(adapter);
        add();
    }

    @Override
    public void onBackPressed() {
        String base = getString(R.string.http);
        boolean edited = adapter.getItemCount() > 0 && !adapter.getItems().get(0).equals(base);

        if (edited)
            new ConfirmDialogFragment.Builder()
                    .withMessage(getString(R.string.Modal_CustomURL_NotSaved))
                    .build().show(getSupportFragmentManager(), null);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    void add() {
        adapter.addAll(Collections.singletonList(getString(R.string.http)));
        binding.bottomBar.setTitle(getString(R.string.OONIRun_URLs, Integer.toString(adapter.getItemCount())));
        adapter.notifyDataSetChanged();
        this.scrollToBottom();
    }

    void loadFromWebConnectivity() {
        new FetchUrlsAndAddToList().execute();
    }

    void scrollToBottom() {
        binding.urlContainer.scrollToPosition(adapter.getItemCount() - 1);
        binding.urlsList.post(() -> binding.urlsList.fullScroll(View.FOCUS_DOWN));
    }

    @Override
    public void onConfirmation(Serializable serializable, int i) {
        if (i == DialogInterface.BUTTON_POSITIVE)
            super.onBackPressed();
    }

    private final class FetchUrlsAndAddToList extends AsyncTask<Void, Void, OONIURLListResult> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.loadFromWebConnectivity.setVisibility(View.INVISIBLE);
            binding.progressIndicator.setVisibility(View.VISIBLE);
            binding.bottomBar.getMenu().findItem(R.id.runButton).setEnabled(false);
        }

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
                CustomWebsiteActivity.this.scrollToBottom();
            }

            binding.progressIndicator.setVisibility(View.INVISIBLE);
            binding.bottomBar.getMenu().findItem(R.id.runButton).setEnabled(true);
        }
    }
}
