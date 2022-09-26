package org.openobservatory.ooniprobe.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.common.collect.Lists;
import com.velmurugan.inapplogger.LogType;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.AppLogger;
import org.openobservatory.ooniprobe.adapters.LogRecyclerViewAdapter;
import org.openobservatory.ooniprobe.databinding.ActivityLogBinding;

import java.io.File;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class LogActivity extends AbstractActivity {
    ActivityLogBinding binding;
    @Inject
    AppLogger logger;
    String selectedTag = LogType.ALL.name();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        binding = ActivityLogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                R.layout.item_text,
                Lists.transform(Arrays.asList(LogType.values()), Enum::name));
        binding.spinnerTag.setAdapter(arrayAdapter);
        binding.spinnerTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTag = parent.getItemAtPosition(position).toString();
                update();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void update() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        new Handler(Looper.getMainLooper()).post(() -> {
            List<String> logs = logger.getLog(selectedTag);
            binding.recyclerView.setAdapter(new LogRecyclerViewAdapter(logs));
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.log_viewer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.share) {
            File file = logger.getLogFile();
            Intent intentShareFile = new Intent(Intent.ACTION_SEND);

            intentShareFile.setType(URLConnection.guessContentTypeFromName(file.getName()));
            Uri uri = FileProvider.getUriForFile(this, "org.openobservatory.ooniprobe.provider", file);
            intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);
            intentShareFile.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent chooser = Intent.createChooser(intentShareFile, "Share File");

            List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                this.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            startActivity(chooser);
            return true;
        } else if (itemId == R.id.delete) {
            logger.deleteOldLog();
            update();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}