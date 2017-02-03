package com.jabarasca.financial_app;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
    private float displayDensity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_amount_layout);

        String title = getIntent().getStringExtra(KEY_ADD_AMOUNT_TITLE);
        ((TextView)findViewById(R.id.amountDialogName)).setText(title);
        String amountTotal = getIntent().getStringExtra(KEY_TOTAL_AMOUNT);
        setupButtonListener(amountTotal, title);
        setupRadioButtonListener();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        displayDensity = metrics.density;

        findViewById(R.id.amountEditText).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    setKeyboardVisible(v);
                }
            }
        });

        findViewById(R.id.titleEditText).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    setKeyboardVisible(v);
                }
            }
        });
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
                            titleName = ((RadioButton)findViewById(R.id.titleNoRadioBtn))
                                    .isChecked() ? "" : titleName;
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
            final int ANIMATION_TIME = 90, MINIMIZE_DP_PIXELS = 235, MAXIMIZE_DP_PIXELS = 315,
                    TRANSLATE_DP_PIXELS = 60;

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.titleNoRadioBtn:
                        titleDesired = false;
                        ((RadioButton)findViewById(R.id.titleYesRadioBtn))
                                .setChecked(false);
                        findViewById(R.id.titleEditText).setVisibility(View.INVISIBLE);
                        startHeightResizeAnimation((ViewGroup)findViewById(R.id.amountDialogRelLay),
                                MINIMIZE_DP_PIXELS, ANIMATION_TIME);
                        startYTranslation(findViewById(R.id.amountDialogButtonCancel),
                                TRANSLATE_DP_PIXELS, ANIMATION_TIME, true);
                        startYTranslation(findViewById(R.id.amountDialogButtonOk),
                                TRANSLATE_DP_PIXELS, ANIMATION_TIME, true);
                        break;
                    case R.id.titleYesRadioBtn:
                        titleDesired = true;
                        ((RadioButton)findViewById(R.id.titleNoRadioBtn))
                                .setChecked(false);
                        startHeightResizeAnimation((ViewGroup)findViewById(R.id.amountDialogRelLay),
                                MAXIMIZE_DP_PIXELS, ANIMATION_TIME);
                        startYTranslation(findViewById(R.id.amountDialogButtonCancel),
                                TRANSLATE_DP_PIXELS, ANIMATION_TIME, false);
                        startYTranslation(findViewById(R.id.amountDialogButtonOk),
                                TRANSLATE_DP_PIXELS, ANIMATION_TIME, false);
                        break;
                }
            }
        };

        RadioButton radioNo = (RadioButton)findViewById(R.id.titleNoRadioBtn);
        RadioButton radioYes = (RadioButton)findViewById(R.id.titleYesRadioBtn);
        radioNo.setOnClickListener(radioListener);
        radioYes.setOnClickListener(radioListener);
    }

    private void setKeyboardVisible(View viewToInput) {
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(viewToInput, InputMethodManager.SHOW_IMPLICIT);
    }

    private void setTitleEditTextFocus(View amount, View title) {
        EditText amountEditText = (EditText)amount;
        if(amountEditText.getText().length() > 0) {
            title.requestFocus();
        }

    }

    // If dpPixels is inferior to actual height, the view will contract.
    private void startHeightResizeAnimation(final ViewGroup VIEW_TO_ANIMATE,
                                            int dpPixels, long time) {
        final float NUMBER_PIXELS = (float)Math.ceil(dpPixels * displayDensity);
        final ViewGroup.LayoutParams VIEW_LAY_PARAMS = VIEW_TO_ANIMATE.getLayoutParams();
        final boolean MAXIMIZE = NUMBER_PIXELS >= VIEW_LAY_PARAMS.height;
        float endValue = MAXIMIZE ? 1.7f : 0.3f;

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f, endValue);
        valueAnimator.setDuration(time);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float)animation.getAnimatedValue();
                setChildScaleValue(VIEW_TO_ANIMATE, value, 0,1,2,3,4,6,7);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                configChildHardwareLayer(VIEW_TO_ANIMATE, true, 0,1,2,3,4,6,7);
                setChildPivotYValue(VIEW_TO_ANIMATE, 0f, 0,1,2,3,4,6,7);
                if(!MAXIMIZE) {
                    findViewById(R.id.titleEditText).setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                if(MAXIMIZE) {
                    findViewById(R.id.titleEditText).setVisibility(View.VISIBLE);
                    setTitleEditTextFocus(findViewById(R.id.amountEditText),
                            findViewById(R.id.titleEditText));
                }
                VIEW_LAY_PARAMS.height = (int)NUMBER_PIXELS;
                VIEW_TO_ANIMATE.requestLayout();
                setChildScaleValue(VIEW_TO_ANIMATE, 1f, 0,1,2,3,4,6,7);
                configChildHardwareLayer(VIEW_TO_ANIMATE, false, 0,1,2,3,4,6,7);
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

    private void startYTranslation(final View VIEW_TO_ANIMATE, int numberOfDpPixels, long time,
                                   boolean reverseAnimation) {
        float numberPixels = (float)Math.ceil(numberOfDpPixels * displayDensity);
        float startValue = reverseAnimation ? numberPixels : 0f;
        float endValue = reverseAnimation ? 0f : numberPixels;
        ValueAnimator viewAnimation = ValueAnimator.ofFloat(startValue, endValue);
        viewAnimation.setDuration(time);
        viewAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float)animation.getAnimatedValue();
                VIEW_TO_ANIMATE.setTranslationY(value);
            }
        });
        viewAnimation.start();
    }
}
