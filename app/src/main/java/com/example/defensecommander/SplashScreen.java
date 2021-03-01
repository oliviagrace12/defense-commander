package com.example.defensecommander;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    private static final long SPLASH_TIME_OUT = 3000;
    ImageView titleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        titleImageView = findViewById(R.id.splashTitle);
        // todo fade in title

        new Handler().postDelayed(this::openMainActivity, SPLASH_TIME_OUT);
    }

    private void openMainActivity() {
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(intent);
//        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }
}