package com.example.defensecommander;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

public class Interceptor {

    private MainActivity mainActivity;
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    float halfImageWidth;
    float halfImageHeight;
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


        halfImageWidth = imageView.getDrawable().getIntrinsicWidth() * 0.5f;
        halfImageHeight = imageView.getDrawable().getIntrinsicHeight() * 0.5f;

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
                makeBlast();
            }
        });

        animatorSet.playTogether(xAnimator, yAnimator);
    }

    private void makeBlast() {
        SoundPlayer.getInstance().startSound("interceptor_blast");

        Drawable explosionDrawable = ContextCompat.getDrawable(mainActivity, R.drawable.i_explode);

        ImageView explodeImageView = createExplodeImageView(explosionDrawable);
        mainActivity.runOnUiThread(() -> mainActivity.removeInterceptor(this));
        mainActivity.runOnUiThread(() -> mainActivity.getLayout().addView(explodeImageView));
        ObjectAnimator alphaAnimator = createAlphaAnimator(explodeImageView);
        alphaAnimator.start();

        float centerX = explodeImageView.getX() + (explosionDrawable.getIntrinsicWidth() / 2f);
        float centerY = explodeImageView.getY() + (explosionDrawable.getIntrinsicHeight() / 2f);

        mainActivity.applyInterceptorBlast(centerX, centerY);
    }

    private ObjectAnimator createAlphaAnimator(ImageView explodeImageView) {
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
        return alphaAnimator;
    }

    private ImageView createExplodeImageView(Drawable missileDrawable) {
        ImageView explodeImageView = new ImageView(mainActivity);
        mainActivity.runOnUiThread(() -> explodeImageView.setImageDrawable(missileDrawable));

        float offset = explodeImageView.getDrawable().getIntrinsicWidth() / 2f;
        explodeImageView.setX(imageView.getX() + (imageView.getWidth() / 2f) - offset);
        explodeImageView.setY(imageView.getY() + (imageView.getHeight() / 2f) - offset);
        explodeImageView.setZ(-15);

        return explodeImageView;
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

    public ImageView getImageView() {
        return imageView;
    }
}
