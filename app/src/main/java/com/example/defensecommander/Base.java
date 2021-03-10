package com.example.defensecommander;

import android.widget.ImageView;

public class Base {

    private final ImageView baseImageView;

    public Base(ImageView baseImageView) {
        this.baseImageView = baseImageView;
    }

    public float getX() {
        return baseImageView.getX() + (0.5f * baseImageView.getWidth());
    }

    public float getY() {
        return baseImageView.getY() + (0.5f * baseImageView.getHeight());
    }
}
