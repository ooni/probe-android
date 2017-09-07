package org.openobservatory.ooniprobe.activity;

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
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.adapter.UrlListAdapter;
import org.openobservatory.ooniprobe.data.TestData;
import org.openobservatory.ooniprobe.model.NetworkMeasurement;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class RunTestActivity extends AppCompatActivity implements Observer {
    private RecyclerView testUrlList;
    private UrlListAdapter mUrlListAdapter;
    private static ImageView testImage;
    private static AppCompatButton runButton;
    private static TextView title, testTitle;
    private ProgressBar test_progress;
    private static String test_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_test);
        TestData.getInstance(this, this).addObserver(this);

        test_name = getIntent().getStringExtra("tn");

        title = (TextView) findViewById(R.id.run_test_message);
        if (getIntent().getStringExtra("td") != null)
            title.setText(getIntent().getStringExtra("td"));
        else
            title.setText(getString(R.string.run_test_message));

        String test = NetworkMeasurement.getTestName(this, test_name);
        testTitle = (TextView) findViewById(R.id.test_title);
        testTitle.setText(test);

        testImage = (ImageView) findViewById(R.id.test_logo);
        testImage.setImageResource(NetworkMeasurement.getTestImageBig(test_name));

        ArrayList<String> listItems = new ArrayList<>();
        final ArrayList<String> urlItems = new ArrayList<>();
        try {
            JSONObject ta = new JSONObject(getIntent().getStringExtra("ta"));
            JSONArray urls = ta.getJSONArray("urls");
            for (int i = 0; i < urls.length(); i++)
                urlItems.add(urls.getString(i));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listItems.addAll(urlItems);
        if (listItems.size() == 0 && test_name.equals("web_connectivity"))
            listItems.add(getString(R.string.random_sampling_urls));

        runButton = (AppCompatButton) findViewById(R.id.run_test_button);
        runButton.setOnClickListener(
                new ImageButton.OnClickListener() {
                    public void onClick(View v) {
                        TestData.doNetworkMeasurements(getApplicationContext(), test_name, urlItems);
                        finish();
                    }
                }
        );

        test_progress = (ProgressBar) findViewById(R.id.progressIndicator);

        testUrlList = (RecyclerView) findViewById(R.id.urlList);
        mUrlListAdapter = new UrlListAdapter(this, listItems);
        testUrlList.setAdapter(mUrlListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        testUrlList.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(testUrlList.getContext(),
                layoutManager.getOrientation());
        testUrlList.addItemDecoration(dividerItemDecoration);
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
}
