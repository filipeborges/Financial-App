package com.jabarasca.financial_app;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class AddAmountActivity extends Activity {

    public static final String KEY_ADD_AMOUNT_TITLE = "com.jabarasca.financial_app.DIALOG_TITLE";
    public static final String KEY_TOTAL_AMOUNT = "com.jabarasca.financial_app.TOTAL";
    public static final String KEY_TYPE_AMOUNT_RETURNED = "com.jabarasca.financial_app.AMOUNT_TYPE";
    public static final String KEY_AMOUNT_RETURNED = "com.jabarasca.financial_app.AMOUNT_VALUE";
    public static final String KEY_TITLE_NAME_RETURNED = "com.jabarasca.financial_app.TITLE";
    public static final int ADD_AMOUNT_ACTIVITY_CODE = 4;
    public static final int INCOME_AMOUNT = 1;
    public static final int EXPENSE_AMOUNT = 2;
    private boolean titleDesired;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_amount_layout);

        String title = getIntent().getStringExtra(KEY_ADD_AMOUNT_TITLE);
        ((TextView)findViewById(R.id.amountDialogName)).setText(title);
        String amountTotal = getIntent().getStringExtra(KEY_TOTAL_AMOUNT);
        setupButtonListener(amountTotal, title);
        setupRadioButtonListener();
    }

    private void setupButtonListener(final String amountTotal, final String title) {
        View.OnClickListener btnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText amountEditText = (EditText)findViewById(R.id.amountEditText);
                String outOfBoundsLabel = getResources().getString(R.string.out_of_bounds_label);
                String okText = getResources().getString(android.R.string.ok);

                if(((Button)v).getText().toString().equals(okText)) {
                    if (!amountEditText.getText().toString().equals("") &&
                            !amountTotal.equals(outOfBoundsLabel))
                    {
                        String titleName = ((EditText)findViewById(R.id.titleEditText))
                                .getText().toString();
                        if(!titleDesired || titleName.length() > 0) {
                            Intent intent = new Intent();
                            //If its a income amount.
                            if(title.equals(getString(R.string.income_title))) {
                                intent.putExtra(KEY_TYPE_AMOUNT_RETURNED, INCOME_AMOUNT);
                            } else {
                                intent.putExtra(KEY_TYPE_AMOUNT_RETURNED, EXPENSE_AMOUNT);
                            }
                            //Needs to be verified on result Activity.
                            intent.putExtra(KEY_TITLE_NAME_RETURNED, titleName);
                            intent.putExtra(KEY_AMOUNT_RETURNED, amountEditText.getText().toString());
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.amount_invalid_title),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.amount_invalid_value),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        };
        findViewById(R.id.amountDialogButtonOk).setOnClickListener(btnListener);
        findViewById(R.id.amountDialogButtonCancel).setOnClickListener(btnListener);
    }

    private void setupRadioButtonListener() {
        View.OnClickListener radioListener = new View.OnClickListener() {
            final float TRANSLATE_PIXELS = 83f;
            final int ANIMATION_TIME = 90;

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.titleNoRadioBtn:
                        titleDesired = false;
                        ((RadioButton)findViewById(R.id.titleYesRadioBtn))
                                .setChecked(false);
                        findViewById(R.id.titleEditText).setVisibility(View.INVISIBLE);
                        startHeightResizeAnimation((ViewGroup)findViewById(R.id.amountDialogRelLay), 235,
                                ANIMATION_TIME);
                        startYTranslation(findViewById(R.id.amountDialogButtonCancel),
                                TRANSLATE_PIXELS, ANIMATION_TIME, true);
                        startYTranslation(findViewById(R.id.amountDialogButtonOk),
                                TRANSLATE_PIXELS, ANIMATION_TIME, true);
                        break;
                    case R.id.titleYesRadioBtn:
                        titleDesired = true;
                        ((RadioButton)findViewById(R.id.titleNoRadioBtn))
                                .setChecked(false);
                        startHeightResizeAnimation((ViewGroup)findViewById(R.id.amountDialogRelLay), 315,
                                ANIMATION_TIME);
                        startYTranslation(findViewById(R.id.amountDialogButtonCancel),
                                TRANSLATE_PIXELS, ANIMATION_TIME, false);
                        startYTranslation(findViewById(R.id.amountDialogButtonOk),
                                TRANSLATE_PIXELS, ANIMATION_TIME, false);
                        break;
                }
            }
        };

        RadioButton radioNo = (RadioButton)findViewById(R.id.titleNoRadioBtn);
        RadioButton radioYes = (RadioButton)findViewById(R.id.titleYesRadioBtn);
        radioNo.setOnClickListener(radioListener);
        radioYes.setOnClickListener(radioListener);
    }

    // If dpPixels is inferior to actual height, the view will contract.
    private void startHeightResizeAnimation(final ViewGroup viewToAnimate, final int dpPixels, long time) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float logicalDensity = metrics.density;
        final float numberPixels = (float)Math.ceil(dpPixels * logicalDensity);
        final ViewGroup.LayoutParams viewLayParams = viewToAnimate.getLayoutParams();
        final boolean maximize = numberPixels >= viewLayParams.height;
        float endValue = maximize ? 1.7f : 0.3f;

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f, endValue);
        valueAnimator.setDuration(time);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float)animation.getAnimatedValue();
                setChildScaleValue(viewToAnimate, value, 0,1,2,3,4,6,7);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                configChildHardwareLayer(viewToAnimate, true, 0,1,2,3,4,6,7);
                setChildPivotYValue(viewToAnimate, 0f, 0,1,2,3,4,6,7);
                if(!maximize) {
                    findViewById(R.id.titleEditText).setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                if(maximize) {
                    findViewById(R.id.titleEditText).setVisibility(View.VISIBLE);
                }
                viewLayParams.height = (int)numberPixels;
                viewToAnimate.requestLayout();
                setChildScaleValue(viewToAnimate, 1f, 0,1,2,3,4,6,7);
                configChildHardwareLayer(viewToAnimate, false, 0,1,2,3,4,6,7);
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        valueAnimator.start();
    }

    private void setChildPivotYValue(ViewGroup viewGroup, float pivotValue, int... childIndexes) {
        for (int i : childIndexes) {
            viewGroup.getChildAt(i).setPivotY(pivotValue);
        }
    }

    private void configChildHardwareLayer(ViewGroup viewGroup, boolean useHardLayer, int... childIndexes) {
        int layerValue = useHardLayer ? View.LAYER_TYPE_HARDWARE : View.LAYER_TYPE_NONE;
        for (int i : childIndexes) {
            viewGroup.getChildAt(i).setLayerType(layerValue, null);
        }
    }

    private void setChildScaleValue(ViewGroup viewGroup, float scaleValue, int... childIndexes) {
        for (int i : childIndexes) {
            viewGroup.getChildAt(i).setScaleY(scaleValue);
        }
    }

    private void startYTranslation(final View viewToAnimate, float numberOfPixels, long time,
                                          boolean reverseAnimation) {
        float startValue = reverseAnimation ? numberOfPixels : 0f;
        float endValue = reverseAnimation ? 0f : numberOfPixels;
        ValueAnimator viewAnimation = ValueAnimator.ofFloat(startValue, endValue);
        viewAnimation.setDuration(time);
        viewAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float)animation.getAnimatedValue();
                viewToAnimate.setTranslationY(value);
            }
        });
        viewAnimation.start();
    }
}
