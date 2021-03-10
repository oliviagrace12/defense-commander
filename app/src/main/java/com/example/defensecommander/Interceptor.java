package com.example.defensecommander;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class Interceptor {

    private MainActivity mainActivity;
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private ImageView imageView;
    private AnimatorSet animatorSet;
    private static final double DISTANCE_TIME = 0.75;


    public Interceptor(
            MainActivity mainActivity, float startX, float startY, float endX, float endY) {
        this.mainActivity = mainActivity;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        initialize();
    }

    private void initialize() {
        imageView = new ImageView(mainActivity);
        imageView.setImageResource(R.drawable.interceptor);
        imageView.setX(startX);
        imageView.setY(startY);
        imageView.setZ(-10);

        float halfImageWidth = imageView.getDrawable().getIntrinsicWidth() * 0.5f;
        float halfImageHeight = imageView.getDrawable().getIntrinsicHeight() * 0.5f;

        endX = endX - halfImageWidth;
        endY = endY - halfImageHeight;
        float angle = calculateAngle(startX, startY, endX, endY);
        imageView.setRotation(angle);

        mainActivity.getLayout().addView(imageView);

        setupAnimatorSet();
    }

    private void setupAnimatorSet() {
        double distanceToTravel = mainActivity.getDistance(startX, startY, endX, endY);
        long duration = (long) (distanceToTravel * DISTANCE_TIME);

        ObjectAnimator xAnimator = ObjectAnimator.ofFloat(imageView, "x", endX);
        xAnimator.setInterpolator(new AccelerateInterpolator());
        xAnimator.setDuration(duration);

        ObjectAnimator yAnimator = ObjectAnimator.ofFloat(imageView, "y", endY);
        yAnimator.setInterpolator(new AccelerateInterpolator());
        yAnimator.setDuration(duration);

        animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(imageView);
                makeBlast();
            }
        });
        animatorSet.playTogether(xAnimator, yAnimator);
    }

    private void makeBlast() {
        SoundPlayer.getInstance().startSound("interceptor_blast");
        ImageView explodeImageView = new ImageView(mainActivity);
        explodeImageView.setImageResource(R.drawable.i_explode);

        float offset = explodeImageView.getDrawable().getIntrinsicWidth() / 2f;
        explodeImageView.setX(endX - offset);
        explodeImageView.setY(endY - offset);
        explodeImageView.setZ(-15);
        mainActivity.getLayout().addView(explodeImageView);

        ObjectAnimator alphaAnimator = ObjectAnimator
                .ofFloat(explodeImageView, "alpha", 0.0f);
        alphaAnimator.setDuration(3000);
        alphaAnimator.setInterpolator(new LinearInterpolator());
        alphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(explodeImageView);
            }
        });
        alphaAnimator.start();
    }

    public void launch() {
        animatorSet.start();
    }

    public static float calculateAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        // Keep angle between 0 and 360
        angle = angle + Math.ceil(-angle / 360) * 360;
        return (float) (190.0f - angle);
    }
}