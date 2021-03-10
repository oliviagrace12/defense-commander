package com.example.defensecommander;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class Missile {

    private long screenTime;
    private final int screenWidth;
    private final int screenHeight;
    private final MainActivity mainActivity;
    private ImageView missileImageView;
    private final AnimatorSet animatorSet = new AnimatorSet();

    public Missile(long screenTime, MainActivity mainActivity) {
        this.screenTime = screenTime;
        this.screenWidth = mainActivity.getScreenWidth();
        this.screenHeight = mainActivity.getScreenHeight();
        this.mainActivity = mainActivity;

        missileImageView = new ImageView(mainActivity);
        missileImageView.setY(-500);

        mainActivity.runOnUiThread(() -> mainActivity.getLayout().addView(missileImageView));
    }

    public static float calculateAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        // Keep angle between 0 and 360
        angle = angle + Math.ceil(-angle / 360) * 360;
        return (float) (190.0f - angle);
    }

    public AnimatorSet createAnimatorSet() {
        mainActivity.runOnUiThread(() -> missileImageView.setImageResource(R.drawable.missile));

        int startX = (int) (Math.random() * screenWidth);
        int endX = (startX + (Math.random() < 0.5 ? 500 : -500));
        missileImageView.setRotation(calculateAngle(startX, 0, endX, screenHeight));
        missileImageView.setZ(-1);

        ObjectAnimator yAnimator = ObjectAnimator
                .ofFloat(missileImageView, "y", -200, screenHeight + 200);
        yAnimator.setInterpolator(new LinearInterpolator());
        yAnimator.setDuration(screenTime);
        yAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.runOnUiThread(() -> {
                    mainActivity.getLayout().removeView(missileImageView);
                    mainActivity.getMissileMaker().removeMissile(Missile.this);
                });
            }
        });

        ObjectAnimator xAnimator = ObjectAnimator
                .ofFloat(missileImageView, "x", startX, endX);
        xAnimator.setInterpolator(new LinearInterpolator());
        xAnimator.setDuration(screenTime);

        animatorSet.playTogether(yAnimator, xAnimator);

        return animatorSet;
    }

}
