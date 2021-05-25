package org.openobservatory.ooniprobe.activity;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.openobservatory.ooniprobe.client.OONIAPIClient;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.di.ActivityComponent;
import org.openobservatory.ooniprobe.di.AppComponent;

import okhttp3.OkHttpClient;

public abstract class AbstractActivity extends AppCompatActivity {

    public Application getApp() {
        return ((Application) getApplication());
    }

    public AppComponent getComponent() {
        return getApp().getComponent();
    }

    public ActivityComponent getActivityComponent() {
        return getApp().getActivityComponent();
    }

    boolean isTestRunning() {
        return ((Application) getApplication()).isTestRunning();
    }

    @Deprecated
    /*
     * @deprecated migration to Dagger2 dependency injection in progress
     *
     * Use: Dagger 2 to inject the dependency
     * // Example with Activity
     *
     * // Declare variable
     * @Inject PreferenceManager preferenceManager;
     *
     * // Inject in the flow
     * @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
     *	 super.onCreate(savedInstanceState);
     *	 getActivityComponent().inject(this);
     *  ...
     * }
     */
    public PreferenceManager getPreferenceManager() {
        return ((Application) getApplication()).getPreferenceManager();
    }

    @Deprecated
    /*
     * @deprecated migration to Dagger2 dependency injection in progress
     *
     * Use: Dagger 2 to inject the dependency
     * // Example with Activity
     *
     * // Declare variable
     * @Inject Gson gson;
     *
     * // Inject in the flow
     * @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
     *	 super.onCreate(savedInstanceState);
     *	 getActivityComponent().inject(this);
     *  ...
     * }
     */
    public Gson getGson() {
        return ((Application) getApplication()).getGson();
    }

    @Deprecated
    /*
     * @deprecated migration to Dagger2 dependency injection in progress
     *
     * Use: Dagger 2 to inject the dependency
     * // Example with Activity
     *
     * // Declare variable
     * @Inject OONIAPIClient apiClient;
     *
     * // Inject in the flow
     * @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
     *	 super.onCreate(savedInstanceState);
     *	 getActivityComponent().inject(this);
     *  ...
     * }
     */
    public OONIAPIClient getApiClient() {
        return ((Application) getApplication()).getApiClient();
    }

    @Deprecated
    /*
     * @deprecated migration to Dagger2 dependency injection in progress
     *
     * Use: Dagger 2 to inject the dependency
     * // Example with Activity
     *
     * // Declare variable
     * @Inject OkHttpClient httpClient;
     *
     * // Inject in the flow
     * @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
     *	 super.onCreate(savedInstanceState);
     *	 getActivityComponent().inject(this);
     *  ...
     * }
     */
    public OkHttpClient getOkHttpClient() {
        return ((Application) getApplication()).getOkHttpClient();
    }
}
