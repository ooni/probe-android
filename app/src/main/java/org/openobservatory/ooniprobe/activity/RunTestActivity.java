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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.adapter.TestResultListAdapter;
import org.openobservatory.ooniprobe.adapter.UrlListAdapter;
import org.openobservatory.ooniprobe.data.TestData;
import org.openobservatory.ooniprobe.model.NetworkMeasurement;
import org.openobservatory.ooniprobe.model.TestResult;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class RunTestActivity extends AppCompatActivity {
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

        runButton = (AppCompatButton) findViewById(R.id.run_test_button);
        runButton.setOnClickListener(
                new ImageButton.OnClickListener() {
                    public void onClick(View v) {
                        TestData.doNetworkMeasurements(getApplicationContext(), test_name);
                    }
                }
        );

        test_progress = (ProgressBar) findViewById(R.id.progressIndicator);
        ArrayList<String> listItems = new ArrayList<>();
        System.out.println(getIntent().getStringExtra("ta"));
        try {
            JSONObject ta = new JSONObject(getIntent().getStringExtra("ta"));
            JSONArray urls = ta.getJSONArray("urls");
            for (int i = 0; i < urls.length(); i++)
                listItems.add(urls.getString(i));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        testUrlList = (RecyclerView) findViewById(R.id.urlList);
        mUrlListAdapter = new UrlListAdapter(this, listItems);
        testUrlList.setAdapter(mUrlListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        testUrlList.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(testUrlList.getContext(),
                layoutManager.getOrientation());
        testUrlList.addItemDecoration(dividerItemDecoration);
        //TODO
        // - disable run if test is running (?)
        // - add generic text if there are no urls
        // - pass urls to web con test
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
}
