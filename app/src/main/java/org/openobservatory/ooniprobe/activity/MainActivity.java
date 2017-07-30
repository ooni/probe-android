// Part of measurement-kit <https://measurement-kit.github.io/>.
// Measurement-kit is free software. See AUTHORS and LICENSE for more
// information on the copying conditions.

package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import org.openobservatory.ooniprobe.adapter.LeftMenuListAdapter;
import org.openobservatory.ooniprobe.data.TestData;
import org.openobservatory.ooniprobe.data.TestStorage;
import org.openobservatory.ooniprobe.fragment.AboutFragment;
import org.openobservatory.ooniprobe.fragment.PastTestsFragment;
import org.openobservatory.ooniprobe.fragment.RunTestsFragment;
import org.openobservatory.ooniprobe.fragment.SettingsFragment;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.fragment.TestInfoFragment;
import org.openobservatory.ooniprobe.model.NetworkMeasurement;
import org.openobservatory.ooniprobe.utils.IntentCallback;
import org.openobservatory.ooniprobe.utils.IntentRouter;
import org.openobservatory.ooniprobe.utils.NotificationService;

public class MainActivity extends AppCompatActivity  implements Observer {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mTitle;
    private String[] mMenuItemsTitles;
    private LeftMenuListAdapter mleftMenuListAdapter;
//private CustomTabsIntent customTabsIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkResources();
        TestData.getInstance(this, this).addObserver(this);

        mTitle = getTitle();
        mMenuItemsTitles = new String[]{getString(R.string.run_tests), getString(R.string.past_tests), getString(R.string.settings), getString(R.string.about)};
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(mMenuItemsTitles));
        mleftMenuListAdapter = new LeftMenuListAdapter(this, R.layout.row_left_menu, stringList);
        mDrawerList.setAdapter(mleftMenuListAdapter);

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        ImageView _imgView = new ImageView(this);
        _imgView.setEnabled(false);
        _imgView.setImageResource(R.drawable.ooni_logo);
        mDrawerList.addFooterView(_imgView);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        updateActionBar();

        // Only used with v4.app.ActionBarDrawerToggle
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_white);

        mDrawerToggle = new ActionBarDrawerToggle(
            this,
            mDrawerLayout,
            //R.drawable.menu_white,  /* Only used with v4.app.ActionBarDrawerToggle */
            R.string.drawer_open,
            R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                mleftMenuListAdapter.notifyDataSetChanged();
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                mleftMenuListAdapter.notifyDataSetChanged();
                invalidateOptionsMenu();
            }

            public void onDrawerStateChanged(int newState) {

            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        if (savedInstanceState == null) {
            selectItem(0);
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferences.getBoolean("cleanup_unused_files", false)) {
            TestStorage.removeUnusedFiles(this);
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("cleanup_unused_files", true).apply();
        }

        //checkInformedConsent();

        //NotificationService.getInstance(this).setDevice_token("LORENZO");
        //NotificationService.getInstance(this).sendRegistrationToServer();
        System.out.println("AAAAA+ "+ FirebaseInstanceId.getInstance().getToken());
        ArrayList<String> urls = new ArrayList<>();
        urls.add("https://paul.kinlan.me/");
        urls.add("http://lorenzo.primiterra.it");
        urls.add("http://www.google.it");

        //Browser.getInstance(this).setUrls(urls);

        Intent BrowserIntent = new Intent(MainActivity.this, BrowserActivity.class);
        startActivity(BrowserIntent);

        // XXX: This is probably not correct: we would like to send
        // info to the orchestrator only when the network or any other
        // orchestrator parameter like country code changed.
        String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null) {
            NotificationService ns = NotificationService.getInstance(
                getApplicationContext());
            ns.setDevice_token(token);
            ns.sendRegistrationToServer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final MainActivity activity = this;
        IntentRouter.getInstance(getApplicationContext())
            .register_handler("main_activity", "orchestrate/notification",
                new IntentCallback() {
                    @Override
                    public void callback(Intent intent) {
                        Log.d(TAG, intent.getStringExtra("message"));
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                            activity
                        );
                        builder
                            .setMessage(intent.getStringExtra("message"))
                            .setTitle("ORCHESTRATION");
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        IntentRouter.getInstance(getApplicationContext())
            .unregister_handler("main_activity", "orchestrate/notification");
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public void selectItem(int position) {
        Fragment f = null;
        switch (position){
            case 0:
                f = new RunTestsFragment();
                break;
            case 1:
                f = new PastTestsFragment();
                break;
            case 2:
                f = new SettingsFragment();
                break;
            case 3:
                f = new AboutFragment();
                break;
        }
        if (f != null){
            replaceFragment(f);
            mDrawerList.setItemChecked(position, true);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    public void replaceFragment(Fragment f) {
        String backStateName = f.getClass().getName();
        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
        Fragment currentFragment = manager.findFragmentById(R.id.content_frame);
        if (currentFragment != null){
            if (!f.getClass().equals(PastTestsFragment.class) && currentFragment.getClass().equals(PastTestsFragment.class)){
                TestStorage.setAllViewed(this);
            }
        }
        if (!fragmentPopped && manager.findFragmentByTag(backStateName) == null) {
            // fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.content_frame, f, backStateName);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void checkInformedConsent() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("first_run", true)) {
            startInformedConsentActivity();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.menu_remove_all_tests:
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.clear_all_tests_alert))
                        .setPositiveButton(getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        TestStorage.removeAllTests(MainActivity.this, MainActivity.this);
                                        PastTestsFragment pastTestsFragment = (PastTestsFragment)getSupportFragmentManager().findFragmentByTag("org.openobservatory.ooniprobe.fragment.PastTestsFragment");
                                        if (pastTestsFragment != null && pastTestsFragment.isVisible()) {
                                            pastTestsFragment.updateList();
                                        }
                                    }
                                })
                        .setNegativeButton(getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                        .show();
                return true;
            default:
                return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        updateActionBar();
        //update the fragments
        RunTestsFragment runTestsFragment = (RunTestsFragment)getSupportFragmentManager().findFragmentByTag("org.openobservatory.ooniprobe.fragment.RunTestsFragment");
        if (runTestsFragment != null && runTestsFragment.isVisible()) {
            runTestsFragment.updateList();
        }
        PastTestsFragment pastTestsFragment = (PastTestsFragment)getSupportFragmentManager().findFragmentByTag("org.openobservatory.ooniprobe.fragment.PastTestsFragment");
        if (pastTestsFragment != null && pastTestsFragment.isVisible()) {
            pastTestsFragment.updateList();
        }
        TestInfoFragment testInfoFragment = (TestInfoFragment)getSupportFragmentManager().findFragmentByTag("org.openobservatory.ooniprobe.fragment.TestInfoFragment");
        if (testInfoFragment != null && testInfoFragment.isVisible()) {
            testInfoFragment.updateButtons();
        }
        if (data != null && data instanceof String){
            String string = NetworkMeasurement.getTestName(this, (String)data) + " " + getString(R.string.test_name_finished);
            Toast toast = Toast.makeText(this, string, Toast.LENGTH_SHORT);
            View view = toast.getView();
            TextView text = (TextView) view.findViewById(android.R.id.message);
            text.setGravity(Gravity.CENTER);;
            toast.show();
        }
        System.out.println("update "+ observable);
    }

    public void checkResources() {
        copyResources(R.raw.hosts, "hosts.txt");
        copyResources(R.raw.geoipasnum, "GeoIPASNum.dat");
        copyResources(R.raw.geoip, "GeoIP.dat");
        copyResources(R.raw.global, "global.txt");
    }

    private void copyResources(int id, String filename) {
        boolean exists = false;
        try {
            openFileInput(filename);
            exists = true;
        } catch (FileNotFoundException exc) {
            /* FALLTHROUGH */
        }
        if (exists) {
            return;
        }
        Log.v(TAG, "copyResources: " + filename + " ...");
        try {
            InputStream in = getResources().openRawResource(id);
            FileOutputStream out = openFileOutput(filename, 0);
            byte[] buff = new byte[1024];
            int read;
            while ((read = in.read(buff)) > 0) out.write(buff, 0, read);
        } catch (java.io.IOException err) {
            // XXX suppress exception
            // XXX not closing in and out
            Log.e(TAG, "copyResources: error: " + err + " for: " + filename);
        }
        Log.v(TAG, "copyResources: " + filename + " ... done");
    }

    public void startInformedConsentActivity() {
        Intent InformedConsentIntent = new Intent(MainActivity.this, InformedConsentActivity.class);
        startActivityForResult(InformedConsentIntent, InformedConsentActivity.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == InformedConsentActivity.REQUEST_CODE){
            if (resultCode != InformedConsentActivity.RESULT_CODE_COMPLETED) {
                finish();
            }
            else {
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("first_run", false).apply();
                showToast(R.string.ooniprobe_configured, true);
            }
        }
    }

    public void showToast(int string, boolean success){
        Toast toast = Toast.makeText(this, string, Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setBackgroundResource(success ? R.drawable.success_toast_bg : R.drawable.error_toast_bg);
        TextView text = (TextView) view.findViewById(android.R.id.message);
        text.setGravity(Gravity.CENTER);;
        text.setTextColor(getResources().getColor(R.color.color_off_white));
        toast.show();
    }

    public void updateActionBar(){
        if (TestStorage.newTests(this)) {
            if (Locale.getDefault().getLanguage().equals("ar"))
                getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_layout_ar));
            else
                getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_layout));
        }
        else
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.color_ooni_blue)));
    }

    private static final String TAG = "main-activity";

}