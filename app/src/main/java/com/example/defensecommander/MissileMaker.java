package com.example.defensecommander;

import android.animation.AnimatorSet;

import java.util.ArrayList;
import java.util.List;

public class MissileMaker implements Runnable {

    private long delay = 5000;
    private boolean isRunning = true;
    private final MainActivity mainActivity;
    private int missileCount = 0;
    private int screenWidth;
    private int screenHeight;
    private List<Missile> activeMissiles = new ArrayList<>();


    public MissileMaker(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.screenHeight = mainActivity.getScreenHeight();
        this.screenWidth = mainActivity.getScreenWidth();
    }

    @Override
    public void run() {
        while (isRunning) {
            makeMissile();

            try {
                Thread.sleep((long) (0.5 * delay));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void makeMissile() {
        missileCount++;
        long missileTime = (long) ((delay * 0.5) + (Math.random() * delay));
        final Missile missile = new Missile(missileTime, mainActivity);
        activeMissiles.add(missile);
        final AnimatorSet animatorSet = missile.createAnimatorSet();

        mainActivity.runOnUiThread(animatorSet::start);
    }

    public void removeMissile(Missile missile) {
        activeMissiles.remove(missile);
    }
}
