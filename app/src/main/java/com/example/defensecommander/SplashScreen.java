package com.example.defensecommander;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    private static final long SPLASH_TIME_OUT = 6000;
    ImageView titleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullscreen();
        setContentView(R.layout.activity_splash_screen);

        SoundPlayer.getInstance().setupLoopingSound(this, "background", R.raw.background);
        SoundPlayer.getInstance().setupSound(this, "interceptor_blast", R.raw.interceptor_blast);
        SoundPlayer.getInstance().setupSound(this, "interceptor_hit_missile", R.raw.interceptor_hit_missile);
        SoundPlayer.getInstance().setupSound(this, "launch_interceptor", R.raw.launch_interceptor);
        SoundPlayer.getInstance().setupSound(this, "launch_missile", R.raw.launch_missile);
        SoundPlayer.getInstance().setupSound(this, "missile_miss", R.raw.missile_miss);
        SoundPlayer.getInstance().setupSound(this, "base_blast", R.raw.base_blast);

        titleImageView = findViewById(R.id.splashTitle);
        fadeInTitle();

        new Handler().postDelayed(
                () -> SoundPlayer.getInstance().startSound("background"),2000);

        new Handler().postDelayed(this::openMainActivity, SPLASH_TIME_OUT);
    }

    private void makeFullscreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void fadeInTitle() {
        ObjectAnimator alphaAnimator =
                ObjectAnimator.ofFloat(titleImageView, "alpha", 0, 1);
        alphaAnimator.setInterpolator(new LinearInterpolator());
        alphaAnimator.setDuration(5500);
        alphaAnimator.start();
    }

    private void openMainActivity() {
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}