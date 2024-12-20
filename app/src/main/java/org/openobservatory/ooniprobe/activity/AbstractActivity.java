package org.openobservatory.ooniprobe.activity;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.client.OONIAPIClient;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.LocaleUtils;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.di.ActivityComponent;
import org.openobservatory.ooniprobe.di.AppComponent;
import org.openobservatory.ooniprobe.fragment.ProgressFragment;

import okhttp3.OkHttpClient;

public abstract class AbstractActivity extends AppCompatActivity {
    public AbstractActivity() {
        LocaleUtils.updateConfig(this);
    }
    public Application getApp() {
        return ((Application) getApplication());
    }

    public AppComponent getComponent() {
        return getApp().getComponent();
    }

    public ActivityComponent getActivityComponent() {
        return getApp().getActivityComponent();
    }

    public boolean isTestRunning() {
        return ((Application) getApplication()).isTestRunning();
    }

    public void bindTestService() {
        ProgressFragment fragment = (ProgressFragment) getSupportFragmentManager().findFragmentById(R.id.progress_fragment);
        if (fragment != null) {
            fragment.bindTestService();
        }
    }


    /**
     * Remove the progress fragment.
     * <p>
     * This method is called when the task is completed.
     */
    protected void removeProgressFragment(@IdRes int id) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(id);
        if (fragment != null && fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        findViewById(id).setVisibility(View.GONE);
    }
}
