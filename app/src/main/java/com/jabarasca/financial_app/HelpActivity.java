package com.jabarasca.financial_app;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class HelpActivity extends AppCompatActivity {

    private final int[] IMAGES_IDS = new int[]{R.drawable.help_1, R.drawable.help_2, R.drawable.help_3,
            R.drawable.help_4};
    private final int[] STR_IMAGES_IDS = new int[]{R.string.help_title_1, R.string.help_title_2,
        R.string.help_title_3, R.string.help_title_4};

    private Animation frwdTransAnimOut;
    private Animation frwdTransAnimIn;
    private Animation bwrdTransAnimIn;
    private Animation bwrdTransAnimOut;
    private int currentPageNumber = 1;
    private int current_index = 0;
    private final String PAGE_MASK = "%d de " + IMAGES_IDS.length;
    private TextView pageNumberTextView;
    private ValueAnimator pageNumberAnimator;
    private float animationValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_layout);

        frwdTransAnimOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        frwdTransAnimIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        bwrdTransAnimIn = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        bwrdTransAnimOut = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        final ImageSwitcher imgSwitcher = (ImageSwitcher)findViewById(R.id.helpImgSwitcher);
        imgSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView img = new ImageView(getApplicationContext());
                img.setLayoutParams(new ImageSwitcher.LayoutParams(ViewGroup
                        .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                img.setImageResource(IMAGES_IDS[0]);
                return img;
            }
        });
        ((TextView)findViewById(R.id.helpTitle)).setText(STR_IMAGES_IDS[0]);
        pageNumberTextView = (TextView)findViewById(R.id.helpPageNumber);
        pageNumberTextView.setText(String.format(PAGE_MASK, currentPageNumber));

        RelativeLayout relLayout = (RelativeLayout)findViewById(R.id.helpActivRelLay);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        final float DELTA_DP_PIXELS = (float)Math.ceil(70 * metrics.density);
        relLayout.setOnTouchListener(new View.OnTouchListener() {
            float initialXValue, deltaX;
            boolean changeImage = true;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        initialXValue = event.getX();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        deltaX = changeImage ? Math.abs(event.getX() - initialXValue) : 0f;
                        if(deltaX >= DELTA_DP_PIXELS) {
                            changeImage((ViewGroup)findViewById(R.id.helpActivRelLay), initialXValue,
                                    event.getX());
                            changeImage = false;
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        changeImage = true;
                        return true;
                    default:
                        return false;
                }
            }
        });

        setupButtonListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast toastMsg = Toast.makeText(this, R.string.help_nav_instruction, Toast.LENGTH_LONG);
        toastMsg.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toastMsg.show();
        startPageNumberAnimation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pageNumberAnimator.cancel();
    }

    private void startPageNumberAnimation() {
        long animationDuration = 300;
        float startValue = 1f, endValue = 1.3f;

        pageNumberAnimator = ValueAnimator.ofFloat(startValue, endValue);
        pageNumberAnimator.setDuration(animationDuration);
        pageNumberAnimator.setRepeatCount(ValueAnimator.INFINITE);
        pageNumberAnimator.setRepeatMode(ValueAnimator.REVERSE);
        pageNumberAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pageNumberTextView.setText(String.format(PAGE_MASK, currentPageNumber));
                animationValue = (float)animation.getAnimatedValue();
                pageNumberTextView.setScaleX(animationValue);
                pageNumberTextView.setScaleY(animationValue);
            }
        });
        pageNumberAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                pageNumberTextView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }
            @Override
            public void onAnimationEnd(Animator animation) {}
            @Override
            public void onAnimationCancel(Animator animation) {
                pageNumberTextView.setLayerType(View.LAYER_TYPE_NONE, null);
            }
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        pageNumberAnimator.start();
    }

    private void setupButtonListener() {
        Button okButton = (Button)findViewById(R.id.helpOkButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void changeImage(ViewGroup relativeLayout, float x0, float x1) {
        ImageSwitcher imgSwitcher = (ImageSwitcher)relativeLayout.getChildAt(1);
        boolean changeImage = false;
        int arrayMaxIndex = IMAGES_IDS.length - 1;
        if(x1 - x0 > 0 && current_index > 0) {
            imgSwitcher.setInAnimation(bwrdTransAnimIn);
            imgSwitcher.setOutAnimation(bwrdTransAnimOut);
            current_index--;
            currentPageNumber--;
            changeImage = true;
        } else if(x1 - x0 < 0 && current_index < arrayMaxIndex) {
            imgSwitcher.setInAnimation(frwdTransAnimIn);
            imgSwitcher.setOutAnimation(frwdTransAnimOut);
            current_index++;
            currentPageNumber++;
            changeImage = true;
        }
        if(current_index <= arrayMaxIndex && current_index >= 0 && changeImage) {
            ((TextView)relativeLayout.getChildAt(0)).setText(STR_IMAGES_IDS[current_index]);
            imgSwitcher.setImageResource(IMAGES_IDS[current_index]);
        }
    }

}
