package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.fragment.IConsentPage1Fragment;
import org.openobservatory.ooniprobe.fragment.IConsentPage2Fragment;
import org.openobservatory.ooniprobe.fragment.IConsentPage3Fragment;
import org.openobservatory.ooniprobe.fragment.IConsentPage4Fragment;
import org.openobservatory.ooniprobe.fragment.IConsentQuizFragment;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class InformedConsentActivity extends AppIntro {


    public static final int REQUEST_CODE = 1000;
    public static final int RESULT_CODE_COMPLETED = 1;
    public int QUESTION_NUMBER = 1;
    private IConsentPage1Fragment fragment1;
    private IConsentPage2Fragment fragment2;
    private IConsentPage3Fragment fragment3;
    private IConsentPage4Fragment fragment4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragment1 = new IConsentPage1Fragment();
        fragment2 = new IConsentPage2Fragment();
        fragment3 = new IConsentPage3Fragment();
        fragment4 = new IConsentPage4Fragment();

        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        //getSupportActionBar().hide();

        // Note here that we DO NOT use setContentView();
        //setContentView(R.layout.activity_informed_consent);

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        /*addSlide(firstFragment);
        addSlide(secondFragment);
        addSlide(thirdFragment);
        addSlide(fourthFragment);
*/
        addSlide(fragment1);
        addSlide(fragment2);
        addSlide(fragment3);
        addSlide(fragment4);

        /*
        addSlide(IConsentPage1Fragment.instantiate(this, "page_1"));
        addSlide(IConsentPage2Fragment.instantiate(this, "page_2"));
        addSlide(IConsentPage3Fragment.instantiate(this, "page_3"));
        addSlide(IConsentPage4Fragment.instantiate(this, "page_4"));
*/

/*
        addSlide(SampleSlide.newInstance(R.layout.fragment_ic_page_1));
        addSlide(SampleSlide.newInstance(R.layout.fragment_ic_page_2));
        addSlide(SampleSlide.newInstance(R.layout.fragment_ic_page_3));
        addSlide(SampleSlide.newInstance(R.layout.fragment_ic_page_4));
*/
        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        //addSlide(AppIntroFragment.newInstance(title, description, image, backgroundColor));

        // OPTIONAL METHODS
        // Override bar/separator color.
        //setBarColor(Color.parseColor("#3F51B5"));
        //setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(false);
        //setProgressButtonEnabled(false);
        setSkipText(getString(R.string.change));
        setDoneText(getString(R.string.lets_go));

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        //setVibrate(true);
        //setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        //next();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        //next();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    //TODO Remove: Probably deprecated
    public void showToast(int string, boolean success){
        Toast toast = Toast.makeText(this, string, Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setBackgroundResource(success ? R.drawable.success_toast_bg : R.drawable.error_toast_bg);
        TextView text = (TextView) view.findViewById(android.R.id.message);
        text.setGravity(Gravity.CENTER);;
        text.setTextColor(getResources().getColor(R.color.color_off_white));
        /*here you can do anything with text*/
        toast.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void loadQuizFragment(){
        FragmentManager fm = getSupportFragmentManager();
        IConsentQuizFragment dFragment = new IConsentQuizFragment();
        dFragment.mActivity = this;
        dFragment.show(fm, "quiz");
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 2 && QUESTION_NUMBER < 3) {
            setNextPageSwipeLock(true);
        }
        else
            setNextPageSwipeLock(false);

        if (position == 3)
            showSkipButton(true);
        else
            showSkipButton(false);
    }

    public void hideNextButton() {
        fragment3.hideNextButton();
    }

    private void next() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean("include_ip", false);
        editor.putBoolean("include_asn", true);
        editor.putBoolean("include_country", true);
        editor.putBoolean("upload_results", true);
        editor.apply();
        //mActivity.getWizard().navigateNext();
    }
}
