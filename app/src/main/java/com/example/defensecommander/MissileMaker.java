package com.example.defensecommander;

import android.animation.AnimatorSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MissileMaker implements Runnable {

    private static final String TAG = "MissileMaker";

    private long delayBetweenMissiles = 5000;
    private boolean isRunning = true;
    private final MainActivity mainActivity;
    private int missileCount = 0;
    private int screenWidth;
    private int screenHeight;
    private List<Missile> activeMissiles = new ArrayList<>();
    private int level = 1;

    public MissileMaker(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.screenHeight = mainActivity.getScreenHeight();
        this.screenWidth = mainActivity.getScreenWidth();
    }

    @Override
    public void run() {
        try {
            Thread.sleep((long) (0.5 * delayBetweenMissiles));
        } catch (InterruptedException e) {
            Log.w(TAG, "run: Error with thread sleep " + e.getLocalizedMessage());
        }

        while (isRunning) {
            makeMissile();
            if (missileCount > 5) {
                missileCount = 0;
                increaseLevel();
            }
            try {
                Thread.sleep(getSleepTime());
            } catch (InterruptedException e) {
                Log.w(TAG, "run: Error with thread sleep " + e.getLocalizedMessage());
            }
        }
    }

    private long getSleepTime() {
        double random = Math.random();
        if (random < 0.1) {
            return 1;
        } else if (random < 0.2) {
            return delayBetweenMissiles / 2L;
        } else {
            return delayBetweenMissiles;
        }
    }

    private void increaseLevel() {
        level++;
        mainActivity.runOnUiThread(() -> mainActivity.setLevel(level));
        if (delayBetweenMissiles > 500) {
            delayBetweenMissiles -= 500;
        } else {
            delayBetweenMissiles = 1;
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Log.w(TAG, "run: Error with thread sleep " + e.getLocalizedMessage());
        }
    }

    private void makeMissile() {
        missileCount++;
        long missileTime = (long) ((delayBetweenMissiles * 1.5));
        final Missile missile = new Missile(missileTime, mainActivity);
        activeMissiles.add(missile);
        final AnimatorSet animatorSet = missile.createAnimatorSet();
        SoundPlayer.getInstance().startSound("launch_missile");

        mainActivity.runOnUiThread(animatorSet::start);
    }

    public void removeMissile(Missile missile) {
        activeMissiles.remove(missile);
    }

    public List<Missile> getActiveMissiles() {
        return activeMissiles;
    }

    public void removeAllMissiles() {
        for (Missile missile : activeMissiles) {
            missile.getAnimatorSet().cancel();
        }
        activeMissiles.clear();
        missileCount = 0;
    }

    public void stop() {
        isRunning = false;
    }


}
