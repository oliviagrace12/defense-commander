package com.example.defensecommander;

import android.widget.ImageView;

public class Base {

    private final ImageView baseImageView;

    public Base(ImageView baseImageView) {
        this.baseImageView = baseImageView;
    }

    public double getX() {
        return baseImageView.getX() + (0.5 * baseImageView.getWidth());
    }

    public double getY() {
        return baseImageView.getY() + (0.5 * baseImageView.getHeight());
    }
}
