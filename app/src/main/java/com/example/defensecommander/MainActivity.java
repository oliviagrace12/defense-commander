package com.example.defensecommander;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MISSILE_BLAST_RANGE = 250;
    private static final int INTERCEPTOR_BLAST_RANGE = 120;

    private int screenWidth;
    private int screenHeight;
    private CloudScroller cloudScroller;
    private List<Base> bases = new ArrayList<>();
    private ViewGroup layout;
    private MissileMaker missileMaker;
    private TextView scoreTextView;
    private TextView levelTextView;
    private int score = 0;
    private int level = 1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullscreen();
        setContentView(R.layout.activity_main);
        getScreenDimensions();

        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/RobotoMono-VariableFont_wght.ttf");

        scoreTextView = findViewById(R.id.score);
        levelTextView = findViewById(R.id.level);
        levelTextView.setText(getString(R.string.level, level));

        scoreTextView.setTypeface(customFont);
        levelTextView.setTypeface(customFont);

        layout = findViewById(R.id.constraintLayout);
        layout.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                handleTouch(motionEvent.getX(), motionEvent.getY());
            }
            return false;
        });

        cloudScroller = new CloudScroller(
                this, layout, 60000, screenWidth, screenHeight);

        createBases();

        missileMaker = new MissileMaker(this);
        new Thread(missileMaker).start();

        setUpGameLoop();
    }

    private void setUpGameLoop() {
        new Thread(() -> {
            while (!bases.isEmpty()) {
                continue;
            }
            MainActivity.this.runOnUiThread(this::endGame);
        }).start();
    }

    public void setLevel(int level) {
        this.level = level;
        levelTextView.setText(getString(R.string.level, level));
    }

    public double getDistance(float x1, float y1, float x2, float y2) {
        return Math.sqrt(Math.pow((x2-x1), 2) + Math.pow((y2-y1), 2));
    }

    public void applyInterceptorBlast(float interceptorX, float interceptorY) {
        List<Missile> hitMissiles = new ArrayList<>();
        for (Missile missile : missileMaker.getActiveMissiles()) {
            double distance =
                    getDistance(missile.getX(), missile.getY(), interceptorX, interceptorY);
            if (distance < INTERCEPTOR_BLAST_RANGE) {
                hitMissiles.add(missile);
                score++;
                scoreTextView.setText(String.valueOf(score));
            }
        }
        hitMissiles.forEach(missile -> {
            missile.setHit(true);
            missile.playInterceptorHitMissileBlast();
        });
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
            if (distance < MISSILE_BLAST_RANGE) {
                base.showHitByMissile();
                hitBase = base;
            } else {
                SoundPlayer.getInstance().startSound("missile_miss");
            }
            missile.playMissileMissBlast();
            missileMaker.removeMissile(missile);
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

        new Handler().postDelayed(() -> handleScores(), 8500);
    }

    private void handleScores() {
        new Thread(new TopTenScoreDatabaseHandler(this, score))
                .start();
    }

    public void openTopScoreDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        EditText userInitialsEditText = buildEnterUserInitialsEditText();
        builder.setView(userInitialsEditText);
        builder.setTitle("You are a Top-Player!");
        builder.setMessage("Please enter your initials (up to 3 characters):");
        builder.setPositiveButton("OK", (dialog, which) -> {
            enterScoreInDatabase(userInitialsEditText.getText().toString());
        });
        builder.setNegativeButton("CANCEL", (dialog, which) -> new Thread(
                new TopTenScoreDatabaseHandler(this))
                .start()
        );

        builder.create().show();
    }

    private void enterScoreInDatabase(String userInitials) {
        new Thread(new EnterScoreDatabaseHandler(this,
                new ScoreEntry(System.currentTimeMillis(), userInitials, score, level))).start();
    }

    public void openScoresActivity(ArrayList<ScoreEntry> scoreEntries) {
        Intent intent = new Intent(this, ScoreActivity.class);
        intent.putExtra("scoreEntries", scoreEntries);
        startActivity(intent);
        finish();
    }

    public EditText buildEnterUserInitialsEditText() {
        EditText userInitials = new EditText(this);
        userInitials.setInputType(InputType.TYPE_CLASS_TEXT);
        userInitials.setGravity(Gravity.CENTER_HORIZONTAL);
        userInitials.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

        return userInitials;
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