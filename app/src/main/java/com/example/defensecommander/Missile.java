package com.example.defensecommander;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

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
        Drawable missileDrawable = ContextCompat.getDrawable(mainActivity, R.drawable.missile);
        mainActivity.runOnUiThread(() -> missileImageView.setImageDrawable(missileDrawable));

        int startX = (int) (Math.random() * screenWidth);
        int endX = (startX + (Math.random() < 0.5 ? 500 : -500));
        missileImageView.setRotation(calculateAngle(startX, 0, endX, screenHeight));
        missileImageView.setZ(-1);

        ObjectAnimator yAnimator = ObjectAnimator.ofFloat(missileImageView, "y",
                0.0f, screenHeight - missileDrawable.getIntrinsicHeight());
        yAnimator.setInterpolator(new LinearInterpolator());
        yAnimator.setDuration(screenTime);
        yAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.runOnUiThread(() -> {
                    mainActivity.applyMissileBlast(Missile.this);
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

    public float getX() {
        return missileImageView.getX() + (missileImageView.getWidth() / 2f);
    }

    public float getY() {
        return missileImageView.getY() + (missileImageView.getHeight() /2f);
    }


    public void playMissileMissBlast() {
        ImageView blastImageView = new ImageView(mainActivity);
        blastImageView.setImageResource(R.drawable.explode);
        blastImageView.setX(missileImageView.getX());
        blastImageView.setY(missileImageView.getY());
        blastImageView.setRotation((float) (360.0 * Math.random()));

        mainActivity.getLayout().removeView(missileImageView);
        mainActivity.getLayout().addView(blastImageView);

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(blastImageView, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(blastImageView);
            }
        });
        alpha.start();
    }

    public ImageView getMissileImageView() {
        return missileImageView;
    }
}
