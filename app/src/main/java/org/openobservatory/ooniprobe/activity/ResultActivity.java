package org.openobservatory.ooniprobe.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.fragment.ResultFragment;
import org.openobservatory.ooniprobe.fragment.ResultListFragment;
import org.openobservatory.ooniprobe.utils.LogUtils;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intent = getIntent();
        if(intent.getExtras() != null) {
            String json_file = intent.getStringExtra("json_file");
            final String[] parts = LogUtils.getLogParts(this, json_file);
            if (parts.length == 1){
                Fragment fragment = new ResultFragment();
                FragmentManager fm= getSupportFragmentManager();
                FragmentTransaction ft=fm.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putInt("position", 0);
                fragment.setArguments(bundle);
                //ft.remove(fragment);
                ft.add(R.id.fragment,fragment);
                //ft.replace(R.id.fragment, fragment);
                //ft.addToBackStack(null);
                ft.commit();
            }
            else {
                Fragment fragment = new ResultListFragment();
                FragmentManager fm= getSupportFragmentManager();
                FragmentTransaction ft=fm.beginTransaction();
                //ft.remove(fragment);
                ft.add(R.id.fragment,fragment);
                //ft.replace(R.id.fragment, fragment);
                //ft.addToBackStack(null);
                ft.commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    /*
        @Override
        public void onBackPressed() {
            Fragment fragment=new Fragment1();
            FragmentManager fm= getFragmentManager();

            FragmentTransaction ft=fm.beginTransaction();

            ft.replace(R.id.fragment,fragment);

            ft.commit();
        }
    */
    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Fragment fragment=new ResultListFragment();
            FragmentManager fm= getSupportFragmentManager();
            FragmentTransaction ft=fm.beginTransaction();
            //ft.remove(fragment);
            ft.add(R.id.fragment,fragment);
            //ft.replace(R.id.fragment,fragment);
            ft.addToBackStack(null);
            ft.commit();
            return true;
        }
        if(id == R.id.action_exit)
        {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }

        return super.onOptionsItemSelected(item);
    }
    */
}
