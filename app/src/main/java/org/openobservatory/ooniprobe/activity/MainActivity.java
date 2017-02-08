// Part of measurement-kit <https://measurement-kit.github.io/>.
// Measurement-kit is free software. See AUTHORS and LICENSE for more
// information on the copying conditions.

package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import org.openobservatory.ooniprobe.adapter.LeftMenuListAdapter;
import org.openobservatory.ooniprobe.data.TestData;
import org.openobservatory.ooniprobe.fragment.AboutFragment;
import org.openobservatory.ooniprobe.fragment.PastTestsFragment;
import org.openobservatory.ooniprobe.fragment.RunTestsFragment;
import org.openobservatory.ooniprobe.fragment.SettingsFragment;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.fragment.TestInfoFragment;

public class MainActivity extends AppCompatActivity  implements Observer {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mMenuItemsTitles;
    private LeftMenuListAdapter mleftMenuListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkResources();
        TestData.getInstance(this, this).addObserver(this);

        mTitle = mDrawerTitle = getTitle();
        mMenuItemsTitles = new String[]{getString(R.string.run_tests), getString(R.string.past_tests), getString(R.string.settings), getString(R.string.about)};
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        //mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.menu_item, mMenuItemsTitles));
        ArrayList <String> stringList = new ArrayList<String>(Arrays.asList(mMenuItemsTitles));
        mleftMenuListAdapter = new LeftMenuListAdapter(this, R.layout.row_left_menu, stringList);
        mDrawerList.setAdapter(mleftMenuListAdapter);

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        ImageView _imgView = new ImageView(this);
        _imgView.setImageResource(R.drawable.ooni_logo);
        mDrawerList.addFooterView(_imgView);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Only used with v4.app.ActionBarDrawerToggle
        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_white);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                //R.drawable.ic_drawer,  /* Only used with v4.app.ActionBarDrawerToggle */
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                mleftMenuListAdapter.notifyDataSetChanged();
                //getSupportActionBar().setTitle(mTitle);
                //invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                mleftMenuListAdapter.notifyDataSetChanged();
                //getSupportActionBar().setTitle(mDrawerTitle);
                //invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        if (savedInstanceState == null) {
            selectItem(0);
        }

        checkInformedConsent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        switch (position){
            case 0:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new RunTestsFragment(), "run_tests").commit();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new PastTestsFragment(), "past_tests").commit();
                break;
            case 2:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment(), "settings").commit();
                break;
            case 3:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AboutFragment(), "about").commit();
                break;
        }

        mDrawerList.setItemChecked(position, true);
        setTitle(mMenuItemsTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
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

    @Override
    public void update(Observable observable, Object data) {
        //update the fragments
        RunTestsFragment runTestsFragment = (RunTestsFragment)getSupportFragmentManager().findFragmentByTag("run_tests");
        if (runTestsFragment != null && runTestsFragment.isVisible()) {
            runTestsFragment.updateList();
        }
        PastTestsFragment pastTestsFragment = (PastTestsFragment)getSupportFragmentManager().findFragmentByTag("past_tests");
        if (pastTestsFragment != null && pastTestsFragment.isVisible()) {
            pastTestsFragment.updateList();
        }
        TestInfoFragment testInfoFragment = (TestInfoFragment)getSupportFragmentManager().findFragmentByTag("test_info");
        if (testInfoFragment != null && testInfoFragment.isVisible()) {
            testInfoFragment.updateButtons();
        }
        System.out.println("update "+ observable);
    }

    public void checkResources() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("resources_copied", true)) {
            copyResources(R.raw.hosts, "hosts.txt");
            copyResources(R.raw.geoipasnum, "GeoIPASNum.dat");
            copyResources(R.raw.geoip, "GeoIP.dat");
            copyResources(R.raw.global, "global.txt");
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("resources_copied", true).apply();
        }
    }

    private void copyResources(int id, String filename) {
        Log.v(TAG, "copyResources...");
        try {
            InputStream in = getResources().openRawResource(id);
            FileOutputStream out = openFileOutput(filename, 0);
            byte[] buff = new byte[1024];
            int read;
            while ((read = in.read(buff)) > 0) out.write(buff, 0, read);
        } catch (java.io.IOException err) {
            // XXX suppress exception
            // XXX not closing in and out
            Log.e(TAG, "copyResources: error: " + err);
        }
        Log.v(TAG, "copyResources... done");
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

    private static final String TAG = "main-activity";

}