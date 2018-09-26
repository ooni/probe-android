package org.openobservatory.ooniprobe.activity;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import org.openobservatory.ooniprobe.BuildConfig;

public class OoniRunActivity extends AppCompatActivity  {
   /* private RecyclerView testUrlList;
    private UrlListAdapter mUrlListAdapter;
    private static ImageView testImage;
    private static AppCompatButton runButton;
    private static TextView title, testTitle, urls;
    private ProgressBar test_progress;
    private static String test_name;
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       /*
        setContentView(R.layout.activity_run_test);
        urls = (TextView) findViewById(R.id.urls);
        runButton = (AppCompatButton) findViewById(R.id.run_test_button);
        title = (TextView) findViewById(R.id.run_test_message);
        testTitle = (TextView) findViewById(R.id.test_title);
        testImage = (ImageView) findViewById(R.id.test_logo);
        test_progress = (ProgressBar) findViewById(R.id.progressIndicator);
        testUrlList = (RecyclerView) findViewById(R.id.urlList);
*/
        // Get the intent that started this activity
        Intent intent = getIntent();
        gotIntent(intent);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        gotIntent(intent);
    }

    public void gotIntent(Intent intent){
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
                    System.out.println("OONIRun test name "+ tn);
                    System.out.println("OONIRun test arguments "+ ta);
                    System.out.println("OONIRun test description "+ td);

                    /*
                    Tn: è il nome del test
                    if(controlla che esista quel test tra i supportati) {
                    Td: Se è presente td mostralo nel campo descrizione, altrimenti mostra OONIRun.YouAreAboutToRun

                    Ta: sono gli arguments per il test, per ora solo per web_connectivity, ed è una lista di url
                     {"urls":["http://www.google.it","http://www.google.com"]}

                    Se sono presenti mostra la lista, altrimenti mostra label OONIRun.RandomSamplingOfURLs

                    Bottone titolo OONIRun.Run
                    }
                    else {
                    Titolo :OONIRun_InvalidParameter
                    Descrizione: OONIRun_InvalidParameter_Msg
                    Nessun bottone
                    }

                     */
                }
                else {
                    /*
                Show
                Titolo: OONIRun_OONIProbeOutOfDate
                Descrizione: OONIRun.OONIProbeNewerVersion
                Bottone: OONIRun.Update con azione checkPlayStore()
                */
                }
            }
            else {
                /*
                Show
                OONIRun_InvalidParameter
                OONIRun_InvalidParameter_Msg
                Nessun bottone
                */
            }
        }
    }

/*
//OLD CODE
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
                        TestData.doNetworkMeasurements(getApplicationContext(), new NetworkMeasurement(getApplicationContext(), test_name, urlItems));
                        goToMainActivity();
                    }
                }
        );

        testUrlList.setAdapter(mUrlListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        testUrlList.setLayoutManager(layoutManager);
    }
*/
    public void goToMainActivity(){
        Intent MainActivityIntent = new Intent(OoniRunActivity.this, MainActivity.class);
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

    public void checkPlayStore(){
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            openPlayStore("market://details?id=" + appPackageName);
        } catch (android.content.ActivityNotFoundException anfe) {
            openPlayStore("https://play.google.com/store/apps/details?id=" + appPackageName);
        }
    }

    public void openPlayStore(String appPackageName) {
        Intent playStore = new Intent(Intent.ACTION_VIEW, Uri.parse(appPackageName));
        playStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(playStore);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO Add button close
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.close_screen, menu);
        return true;
    }

    /*
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.close:
                finish();
                return true;
            default:
                this.onBackPressed();
                return super.onOptionsItemSelected(item);
        }
    }*/

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
