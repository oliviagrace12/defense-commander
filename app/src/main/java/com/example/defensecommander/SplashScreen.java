package com.example.defensecommander;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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

        titleImageView = findViewById(R.id.splashTitle);
        fadeInTitle();

        new Handler().postDelayed(
                () -> SoundPlayer.getInstance().startSound("background"),1000);

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
        ObjectAnimator aAnim =
                ObjectAnimator.ofFloat(titleImageView, "alpha", 0, 1);
        aAnim.setDuration(5500);
        aAnim.start();
    }

    private void openMainActivity() {
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(intent);
//        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}