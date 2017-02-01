package com.jabarasca.financial_app;

import android.animation.ValueAnimator;
import android.view.View;

public class AnimationUtils {

    public static void startYTranslation(final View viewToAnimate, float numberOfPixels, long time,
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
