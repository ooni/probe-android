package org.openobservatory.ooniprobe.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import localhost.toolkit.app.fragment.ConfirmDialogFragment;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.adapters.CustomWebsiteRecyclerViewAdapter;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.databinding.ActivityCustomwebsiteBinding;
import org.openobservatory.ooniprobe.model.database.Url;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;

import javax.inject.Inject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomWebsiteActivity extends AbstractActivity implements ConfirmDialogFragment.OnConfirmedListener {
    @Inject
    PreferenceManager preferenceManager;
    private CustomWebsiteRecyclerViewAdapter adapter;
    private ActivityCustomwebsiteBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        binding = ActivityCustomwebsiteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

            RunningActivity.runAsForegroundService(CustomWebsiteActivity.this, suite.asArray(), this::finish,preferenceManager);
            return true;
        });
        binding.add.setOnClickListener(v -> add());
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
            new ConfirmDialogFragment.Builder().withMessage(getString(R.string.Modal_CustomURL_NotSaved)).build().show(getSupportFragmentManager(), null);
        else super.onBackPressed();
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

    void scrollToBottom() {
        binding.urlContainer.scrollToPosition(adapter.getItemCount() - 1);
        binding.urlsList.post(() -> binding.urlsList.fullScroll(View.FOCUS_DOWN));
    }

    @Override
    public void onConfirmation(Serializable serializable, int i) {
        if (i == DialogInterface.BUTTON_POSITIVE) super.onBackPressed();
    }
}
