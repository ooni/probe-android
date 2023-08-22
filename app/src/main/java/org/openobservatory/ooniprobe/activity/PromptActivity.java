package org.openobservatory.ooniprobe.activity;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.ActivityPromptBinding;
import org.openobservatory.ooniprobe.domain.UpdatesNotificationManager;

import javax.inject.Inject;

public class PromptActivity extends AbstractActivity {
    private static final String PROMPT_ITEM = "promptItem";
    @Inject
    UpdatesNotificationManager notificationManager;
    private ActivityPromptBinding binding;
    private Prompt prompt;

    private ActivityResultLauncher<String> requestPermissionLauncher;

    public static Intent newIntent(Context context, Prompt prompt) {
        return new Intent(context, PromptActivity.class).putExtra(PROMPT_ITEM, prompt);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        binding = ActivityPromptBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prompt = (Prompt) getIntent().getExtras().get(PROMPT_ITEM);
        binding.title.setText(prompt.title);
        binding.description.setText(prompt.paragraph);

        registerPermissionRequest();
        setUpClickListeners();
    }

    private void registerPermissionRequest() {
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), (result) -> {
            if (!result) {
                Snackbar.make(binding.getRoot(), "Please grant Notification permission from App Settings", Snackbar.LENGTH_LONG).setAction(R.string.Settings_Title, view -> {
                    Intent intent = new Intent();
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    //for Android 5-7
                    intent.putExtra("app_package", getPackageName());
                    intent.putExtra("app_uid", getApplicationInfo().uid);

                    // for Android 8 and above
                    intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());

                    startActivity(intent);
                }).show();
            }
            setResult(result ? Activity.RESULT_OK : Activity.RESULT_CANCELED);
            finish();
        });
    }

    private void setUpClickListeners() {
        OnPromptAction actions;
        switch (prompt) {
            case CENSORSHIP_CONSENT:
                actions = new ConsentActions();
                break;
            case TEST_PROGRESS_CONSENT:
                actions = new ConsentActions();
                break;
            default:
                actions = new OnPromptAction() {
                    @Override
                    public void onClickPositive(View view) {
                    }

                    @Override
                    public void onClickNeutral(View view) {
                    }

                    @Override
                    public void onClickNegative(View view) {
                    }
                };
        }

        binding.soundsGreat.setOnClickListener(actions::onClickPositive);
        binding.notNow.setOnClickListener(actions::onClickNeutral);
        binding.dontAskAgain.setOnClickListener(actions::onClickNegative);
    }

    public void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    enum Prompt {
        CENSORSHIP_CONSENT(R.string.Modal_EnableNotifications_Title, R.string.Modal_EnableNotifications_Paragraph), TEST_PROGRESS_CONSENT(R.string.Modal_EnableNotifications_Title, R.string.Modal_EnableNotifications_Paragraph);

        private final int title;
        private final int paragraph;

        Prompt(@StringRes int title, @StringRes int paragraph) {
            this.title = title;
            this.paragraph = paragraph;
        }
    }

    interface OnPromptAction {
        /**
         * Callback for View#onClick of `soundsGreat` button.
         *
         * @param view
         */
        void onClickPositive(View view);

        /**
         * Callback for View#onClick of `notNow` button.
         *
         * @param view
         */
        void onClickNeutral(View view);

        /**
         * Callback for View#onClick of `dontAskAgain` button.
         *
         * @param view
         */
        void onClickNegative(View view);
    }

    public class ConsentActions implements OnPromptAction {

        @Override
        public void onClickPositive(View view) {
            notificationManager.getUpdates(true);
            PromptActivity.this.requestNotificationPermission();
        }

        @Override
        public void onClickNeutral(View view) {
            PromptActivity.this.setResult(Activity.RESULT_CANCELED);
            PromptActivity.this.finish();
        }

        @Override
        public void onClickNegative(View view) {
            notificationManager.disableAskNotificationDialog();
            onClickNeutral(view);
        }
    }

}