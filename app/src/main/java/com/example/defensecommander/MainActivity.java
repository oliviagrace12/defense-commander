package com.example.defensecommander;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MISSILE_BLAST_RANGE = 120;
    private static final int INTERCEPTOR_BLAST_RANGE = 250;

    private int screenWidth;
    private int screenHeight;
    private CloudScroller cloudScroller;
    private List<Base> bases = new ArrayList<>();
    private ViewGroup layout;
    private MissileMaker missileMaker;
    private TextView scoreTextView;
    private int score = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullscreen();
        setContentView(R.layout.activity_main);
        getScreenDimensions();

        layout = findViewById(R.id.constraintLayout);
        scoreTextView = findViewById(R.id.score);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!bases.isEmpty()) {
                    continue;
                }
                MainActivity.this.runOnUiThread(() -> endGame());
            }
        }).start();
    }

    public double getDistance(float x1, float y1, float x2, float y2) {
        return Math.sqrt(Math.pow((x2-x1), 2) + Math.pow((y2-y1), 2));
    }

    public void applyInterceptorBlast(float interceptorX, float interceptorY) {
        List<Missile> hitMissiles = new ArrayList<>();
        for (Missile missile : missileMaker.getActiveMissiles()) {
            double distance = 
                    getDistance(missile.getX(), missile.getY(), interceptorX, interceptorY);
            if (distance < MISSILE_BLAST_RANGE) {
                hitMissiles.add(missile);
                score++;
                scoreTextView.setText(String.valueOf(score));
            }
        }
        hitMissiles.forEach(Missile::playInterceptorBlast);
    }

    public void removeMissile(Missile missile) {
        layout.removeView(missile.getMissileImageView());
        missileMaker.removeMissile(missile);
    }

    public void applyMissileBlast(Missile missile) {
        float missileX = missile.getX();
        float missileY = missile.getY();

        Base hitBase = null;
        for (Base base : bases) {
            double distance = getDistance(missileX, missileY, base.getX(), base.getY());
            if (distance < INTERCEPTOR_BLAST_RANGE) {
                missile.playMissileMissBlast();
                missileMaker.removeMissile(missile);

                base.showHitByMissile();
                hitBase = base;
            } else {
                missile.playMissileMissBlast();
                missileMaker.removeMissile(missile);
            }
        }
        if (hitBase != null) {
            bases.remove(hitBase);
        }
    }

    private void endGame() {
        missileMaker.stop();
        for (Missile activeMissile : missileMaker.getActiveMissiles()) {
            layout.removeView(activeMissile.getMissileImageView());
        }
        missileMaker.removeAllMissiles();

        ImageView endGameImageView = findViewById(R.id.gameOver);
        ObjectAnimator aAnim =
                ObjectAnimator.ofFloat(endGameImageView, "alpha", 0, 1);
        aAnim.setDuration(5500);
        aAnim.start();
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
        bases.add(new Base(findViewById(R.id.baseLeft), this));
        bases.add(new Base(findViewById(R.id.baseCenter), this));
        bases.add(new Base(findViewById(R.id.baseRight), this));
    }

    private void handleTouch(float x, float y) {
        if (bases.isEmpty()) {
            return;
        }
        Base closestBase = findClosestBaseToTouch(x, y);
        launchInterceptor(x, y, closestBase);
    }

    private void launchInterceptor(float x, float y, Base closestBase) {
        Interceptor interceptor = new Interceptor(
                this, closestBase.getX(), closestBase.getY(), x, y);
        interceptor.launch();
    }

    private Base findClosestBaseToTouch(float touchX, float touchY) {
        Base closestBase = null;
        double closestBaseDistance = Double.MAX_VALUE;
        for (Base base : bases) {
            double distance = getDistance(touchX, touchY, base.getX(), base.getY());
            if (distance < closestBaseDistance) {
                closestBaseDistance = distance;
                closestBase = base;
            }
        }
        return closestBase;
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