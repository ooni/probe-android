package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.adapter.UrlListAdapter;
import org.openobservatory.ooniprobe.data.TestData;
import org.openobservatory.ooniprobe.model.NetworkMeasurement;
import org.openobservatory.ooniprobe.utils.Alert;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static java.lang.Boolean.FALSE;

public class RunTestActivity extends AppCompatActivity implements Observer {
    private RecyclerView testUrlList;
    private UrlListAdapter mUrlListAdapter;
    private static ImageView testImage;
    private static AppCompatButton runButton;
    private static TextView title, testTitle, urls;
    private ProgressBar test_progress;
    private static String test_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_test);
        urls = (TextView) findViewById(R.id.urls);
        runButton = (AppCompatButton) findViewById(R.id.run_test_button);
        title = (TextView) findViewById(R.id.run_test_message);
        testTitle = (TextView) findViewById(R.id.test_title);
        testImage = (ImageView) findViewById(R.id.test_logo);
        test_progress = (ProgressBar) findViewById(R.id.progressIndicator);
        testUrlList = (RecyclerView) findViewById(R.id.urlList);

        // Get the intent that started this activity
        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            String mv = uri.getQueryParameter("mv");
            String[] split = BuildConfig.VERSION_NAME.split("-");
            String version_name = split[0];
            if (mv != null){
                if (versionCompare(version_name, mv) >= 0) {
                    String tn = uri.getQueryParameter("tn");
                    String ta = uri.getQueryParameter("ta");
                    String td = uri.getQueryParameter("td");
                    String test = NetworkMeasurement.getTestName(this, tn);
                    if (test.length() > 0){
                        test_name = tn;
                        configureScreen(td, ta);
                    }
                    else {
                        Alert.alertDialogWithAction(this, getString(R.string.invalid_parameter), getString(R.string.test_name) +  " : " + tn, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                goToMainActivity();
                            }
                        });
                    }
                }
                else {
                    Alert.alertDialogWithAction(this, getString(R.string.ooniprobe_outdate), getString(R.string.ooniprobe_outdate_msg),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                    try {
                                        openPlayStore("market://details?id=" + appPackageName);
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        openPlayStore("https://play.google.com/store/apps/details?id=" + appPackageName);
                                    }
                                }
                        },
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    goToMainActivity();
                                }
                            });
                }
            }
        }
    }

    public void configureScreen(String td, String ta){
        TestData.getInstance(this, this).addObserver(this);

        if (td != null)
            title.setText(td);
        else
            title.setText(getString(R.string.run_test_message));

        String test = NetworkMeasurement.getTestName(this, test_name);
        testTitle.setText(test);
        testImage.setImageResource(NetworkMeasurement.getTestImageBig(test_name));

        ArrayList<String> listItems = new ArrayList<>();
        final ArrayList<String> urlItems = new ArrayList<>();
        if (ta != null){
            try {
                JSONObject taJson = new JSONObject(ta);
                JSONArray urls = taJson.getJSONArray("urls");
                for (int i = 0; i < urls.length(); i++)
                    urlItems.add(urls.getString(i));
            } catch (JSONException e) {
                Alert.alertDialogWithAction(this, getString(R.string.invalid_parameter), getString(R.string.urls), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        goToMainActivity();
                    }
                });
                e.printStackTrace();
                return;
            }
            listItems.addAll(urlItems);
        }

        if (listItems.size() == 0 && test_name.equals("web_connectivity")){
            listItems.add(getString(R.string.random_sampling_urls));
            mUrlListAdapter = new UrlListAdapter(this, listItems, false);
        }
        else
            mUrlListAdapter = new UrlListAdapter(this, listItems, true);

        if (listItems.size() == 0)
            urls.setVisibility(View.INVISIBLE);

        runButton.setOnClickListener(
                new ImageButton.OnClickListener() {
                    public void onClick(View v) {
                        TestData.doNetworkMeasurements(getApplicationContext(), test_name, urlItems);
                        goToMainActivity();
                    }
                }
        );

        testUrlList.setAdapter(mUrlListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        testUrlList.setLayoutManager(layoutManager);
    }

    public void goToMainActivity(){
        Intent MainActivityIntent = new Intent(RunTestActivity.this, MainActivity.class);
        /*
        Here are the definitions: https://developer.android.com/reference/android/content/Intent.html
        Snippet took from: https://stackoverflow.com/questions/14332441/finish-current-activity-and-start-a-new-one
        */
        MainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        MainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MainActivityIntent.setPackage(getApplicationContext().getPackageName());
        startActivity(MainActivityIntent);
        finish();
    }

    public void openPlayStore(String appPackageName) {
        Intent playStore = new Intent(Intent.ACTION_VIEW, Uri.parse(appPackageName));
        playStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(playStore);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.close_screen, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.close:
                finish();
                return true;
            default:
                this.onBackPressed();
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void update(Observable observable, Object data) {
        System.out.println("update "+ observable);
        updateTest();
    }

    public void updateTest(){
        if (isRunning()){
            test_progress.setIndeterminate(true);
            test_progress.setVisibility(View.VISIBLE);
            runButton.setVisibility(View.INVISIBLE);
        }
        else {
            test_progress.setVisibility(View.INVISIBLE);
            runButton.setVisibility(View.VISIBLE);
        }
    }

    public Boolean isRunning(){
        ArrayList<NetworkMeasurement> runningTests = TestData.getInstance(this, this).runningTests;
        for(NetworkMeasurement test : runningTests) {
            if (test.testName.equals(test_name))
                return true;
        }
        return false;
    }

    /**
     * Compares two version strings.
     *
     * Use this instead of String.compareTo() for a non-lexicographical
     * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
     *
     * @note It does not work if "1.10" is supposed to be equal to "1.10.0".
     *
     * @param str1 a string of ordinal numbers separated by decimal points.
     * @param str2 a string of ordinal numbers separated by decimal points.
     * @return The result is a negative integer if str1 is _numerically_ less than str2.
     *         The result is a positive integer if str1 is _numerically_ greater than str2.
     *         The result is zero if the strings are _numerically_ equal.
     */
    public static int versionCompare(String str1, String str2) {
        String[] vals1 = str1.split("\\.");
        String[] vals2 = str2.split("\\.");
        int i = 0;
        // set index to first non-equal ordinal or length of shortest version string
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
            i++;
        }
        // compare first non-equal ordinal number
        if (i < vals1.length && i < vals2.length) {
            int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
            return Integer.signum(diff);
        }
        // the strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        return Integer.signum(vals1.length - vals2.length);
    }
}
