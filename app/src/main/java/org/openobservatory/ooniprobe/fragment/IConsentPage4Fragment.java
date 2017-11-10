package org.openobservatory.ooniprobe.fragment;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.InformedConsentActivity;

import android.os.Handler;

public class IConsentPage4Fragment extends Fragment {

    private InformedConsentActivity mActivity;
    private AppCompatButton trueButton;
    private AppCompatButton falseButton;
    private TextView questionNumber;
    private TextView questionText;
    private ImageView gifView;

    public static IConsentPage4Fragment create() {
        IConsentPage4Fragment atf = new IConsentPage4Fragment();
        Bundle args = new Bundle();
        atf.setArguments(args);
        return atf;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActivity = (InformedConsentActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onViewSelected");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ic_page_4, container, false);
        questionNumber = v.findViewById(R.id.question_number);
        questionText = v.findViewById(R.id.question_text);
        gifView = v.findViewById(R.id.gifView);
        gifView.setVisibility(View.GONE);
        loadView();
        trueButton = v.findViewById(R.id.trueButton);
        trueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                next(true);
            }
        });
        falseButton = v.findViewById(R.id.falseButton);
        falseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                next(false);
            }
        });

        return v;
    }

    private void loadView(){
        questionNumber.setText(mActivity.getString(R.string.question) + " " + mActivity.QUESTION_NUMBER + "/2");
        if (mActivity.QUESTION_NUMBER == 1)
            questionText.setText(mActivity.getString(R.string.question_1));
        else if (mActivity.QUESTION_NUMBER == 2)
            questionText.setText(mActivity.getString(R.string.question_2));
    }

    private void next(Boolean answer){
        if (mActivity.QUESTION_NUMBER == 1) {
            if (answer){

                //TODO showAnimation and reloadView
                //https://github.com/bumptech/glide/issues/1706
                gifView.setVisibility(View.VISIBLE);
                /*
                Glide REALLY SLOW
                https://stackoverflow.com/questions/29363321/picasso-v-s-imageloader-v-s-fresco-vs-glide
                https://github.com/Cutta/GifView/
                 */
                Glide.with(mActivity)
                        .load(R.drawable.correct_answer)
                        .into(new DrawableImageViewTarget(gifView) {
                            @Override
                            public void onResourceReady(Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                if (resource instanceof GifDrawable) {
                                    ((GifDrawable)resource).setLoopCount(1);
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            gifView.setVisibility(View.GONE);
                                            mActivity.QUESTION_NUMBER = 2;
                                            loadView();
                                        }
                                    }, 3000);
                                }
                                super.onResourceReady(resource, transition);
                            }
                        });

                /*
                Can't know when gif ends in glide 4
                https://github.com/bumptech/glide/issues/860
                https://github.com/bumptech/glide/issues/2524
                ALT: http://frescolib.org/docs/animations.html#playing-animations-manually
                 */

            }
            else {
                //TODO showAnimation and showActuallyView
            }
        }
        else if (mActivity.QUESTION_NUMBER == 2) {
            if (answer){
                //TODO showAnimation and
                mActivity.getWizard().navigateNext();
            }
            else {
                //TODO showAnimation and showActuallyView
            }
        }
    }
}
