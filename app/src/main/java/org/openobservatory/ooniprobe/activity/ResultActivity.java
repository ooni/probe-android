package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.fragment.ResultFragment;
import org.openobservatory.ooniprobe.fragment.ResultListFragment;
import org.openobservatory.ooniprobe.utils.LogUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intent = getIntent();
        if(intent.getExtras() != null) {
            String json_file = intent.getStringExtra("json_file");
            int logParts = LogUtils.getNumLogParts(this, json_file);
            if (logParts == 1){
                Fragment fragment = new ResultFragment();
                FragmentManager fm= getSupportFragmentManager();
                FragmentTransaction ft=fm.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putInt("position", 0);
                bundle.putString("title", intent.getStringExtra("test_name"));
                fragment.setArguments(bundle);
                ft.add(R.id.fragment,fragment);
                ft.commit();
            }
            else if (logParts == 2){
                Fragment fragment = new ResultListFragment();
                FragmentManager fm= getSupportFragmentManager();
                FragmentTransaction ft=fm.beginTransaction();
                ft.add(R.id.fragment,fragment);
                ft.commit();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
