package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.fragment.ResultFragment;
import org.openobservatory.ooniprobe.fragment.ResultListFragment;
import org.openobservatory.ooniprobe.fragment.TestLogFragment;
import org.openobservatory.ooniprobe.model.NetworkMeasurement;
import org.openobservatory.ooniprobe.model.OONITests;
import org.openobservatory.ooniprobe.model.TestResult;
import org.openobservatory.ooniprobe.utils.JSONUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intent = getIntent();
        if(intent.getExtras() != null) {
            if (intent.getStringExtra("test_name").equals(OONITests.WEB_CONNECTIVITY)){
                if (hasMultipleResult())
                    goToResultList();
                else
                    goToResult();
            }
            else {
                goToResult();
            }
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            setTitle(NetworkMeasurement.getTestName(this, intent.getStringExtra("test_name")));
        }
    }


    public void goToResult(){
        Fragment fragment = new ResultFragment();
        FragmentManager fm= getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt("position", 0);
        fragment.setArguments(bundle);
        ft.add(R.id.fragment,fragment);
        ft.commit();
    }

    public void goToResultList(){
        Fragment fragment = new ResultListFragment();
        FragmentManager fm= getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        ft.add(R.id.fragment,fragment);
        ft.commit();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.view_log:
                showTestLog();
                return true;
            default:
                this.onBackPressed();
                return super.onOptionsItemSelected(item);
        }
    }

    private void showTestLog(){
        Fragment fragment = new TestLogFragment();
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        ft.replace(R.id.fragment,fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    public Boolean hasMultipleResult(){
        int results = 0;
        String jsonFilename = getIntent().getExtras().getString("json_file");
        try {
            File jsonFile = new File(getFilesDir(), jsonFilename);
            JSONUtils.JSONL jsonl = new JSONUtils.JSONL(jsonFile);
            for (JSONObject jsonObj:jsonl){
                results++;
                if (results > 1)
                    return true;
            }
        } catch (IOException e) {
            //TODO handle Exception when file can't be opened.
            // Probably go back to previous screen showing an alert.
            return true;
        }
        return false;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
