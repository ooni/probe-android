package org.openobservatory.ooniprobe.activity;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.fragment.IConsentPage1Fragment;
import org.openobservatory.ooniprobe.fragment.IConsentPage2Fragment;
import org.openobservatory.ooniprobe.fragment.IConsentPage3Fragment;
import org.openobservatory.ooniprobe.fragment.IConsentPage4Fragment;
import org.openobservatory.ooniprobe.fragment.IConsentPage5Fragment;

import me.panavtec.wizard.Wizard;
import me.panavtec.wizard.WizardListener;
import me.panavtec.wizard.WizardPage;
import me.panavtec.wizard.WizardPageListener;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class InformedConsentActivity extends AppCompatActivity implements WizardPageListener, WizardListener {

    private Wizard wizard;

    public static final int REQUEST_CODE = 1000;
    public static final int RESULT_CODE_COMPLETED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        //http://stackoverflow.com/questions/8500283/how-to-hide-action-bar-before-activity-is-created-and-then-show-it-again
        setContentView(R.layout.activity_informed_consent);
        switchToWizard(0);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void switchToWizard(int step) {
        WizardPage[] wizardPages = {
                new WizardStep1(),
                new WizardStep2(),
                new WizardStep3(),
                new WizardStep4(),
                new WizardStep5()};
        wizard = new Wizard.Builder(this, wizardPages)
                .containerId(R.id.container_body)
                .enterAnimation(R.anim.card_slide_right_in)
                .exitAnimation(R.anim.card_slide_left_out)
                .popEnterAnimation(R.anim.card_slide_left_in)
                .popExitAnimation(R.anim.card_slide_right_out)
                .pageListener(this)
                .wizardListener(this)
                .build();
        wizard.init();

        for (int i=1; i< step; i++)
            wizard.navigateNext();


    }

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

    @Override public void onBackPressed() {
        if (wizard.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onWizardFinished() {
            setResult(RESULT_CODE_COMPLETED);
            finish();
    }

    @Override
    public void onPageChanged(int currentPageIndex, WizardPage page) {

    }

    public Wizard getWizard(){
        return wizard;
    }

    class WizardStep1 extends WizardPage<IConsentPage1Fragment> {
        @Override public IConsentPage1Fragment createFragment() {
            return new IConsentPage1Fragment();
        }
    }

    class WizardStep2 extends WizardPage<IConsentPage2Fragment> {
        @Override public IConsentPage2Fragment createFragment() {
            return new IConsentPage2Fragment();
        }
    }

    class WizardStep3 extends WizardPage<IConsentPage3Fragment> {
        @Override public IConsentPage3Fragment createFragment() {
            return new IConsentPage3Fragment();
        }
    }

    class WizardStep4 extends WizardPage<IConsentPage4Fragment> {
        @Override public IConsentPage4Fragment createFragment() {
            return new IConsentPage4Fragment();
        }
    }

    class WizardStep5 extends WizardPage<IConsentPage5Fragment> {
        @Override public IConsentPage5Fragment createFragment() {
            return new IConsentPage5Fragment();
        }
    }
}
