package com.example.defensecommander;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int screenWidth;
    private int screenHeight;
    private CloudScroller cloudScroller;
    private List<Base> bases = new ArrayList<>();
    private ViewGroup layout;
    private MissileMaker missileMaker;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullscreen();
        setContentView(R.layout.activity_main);
        getScreenDimensions();

        layout = findViewById(R.id.constraintLayout);
        cloudScroller = new CloudScroller(
                this, layout, 60000, screenWidth, screenHeight);
        layout.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                handleTouch(motionEvent.getX(), motionEvent.getY());
            }
            return false;
        });

        createBases();

        missileMaker = new MissileMaker(this);
        new Thread(missileMaker).start();
    }

    public ViewGroup getLayout() {
        return layout;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public MissileMaker getMissileMaker() {
        return missileMaker;
    }

    private void createBases() {
        bases.add(new Base(findViewById(R.id.baseLeft)));
        bases.add(new Base(findViewById(R.id.baseCenter)));
        bases.add(new Base(findViewById(R.id.baseRight)));
    }

    private void handleTouch(float x, float y) {
        // todo
    }

    private void getScreenDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
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

    @Override
    protected void onStop() {
        super.onStop();
        cloudScroller.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SoundPlayer.getInstance().stopSound(getString(R.string.background_sound));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SoundPlayer.getInstance().startSound(getString(R.string.background_sound));
    }
}