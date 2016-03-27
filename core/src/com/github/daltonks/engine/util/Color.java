package com.github.daltonks.engine.util;

public class Color {
    public static Color WHITE =       new Color(1, 1, 1),
                        RED =         new Color(1, 0, 0),
                        PLAYER_BLUE = new Color(.3f, .6f, 1);

    private float r, g, b;

    public Color() {}

    public Color(float r, float g, float b) {
        set(r, g, b);
    }

    public Color lerpClampNew(Color other, float position) {
        Color newColor = Pools.getColor();
        newColor.r = (float) EngineMath.lerpClamp(r, other.r, position);
        newColor.g = (float) EngineMath.lerpClamp(g, other.g, position);
        newColor.b = (float) EngineMath.lerpClamp(b, other.b, position);
        return newColor;
    }

    public boolean equals(Color other) {
        return (r == other.r) && (g == other.g) && (b == other.b);
    }

    public void set(Color other) {
        this.r = other.r;
        this.g = other.g;
        this.b = other.b;
    }

    public void set(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void setRed(float r) {
        this.r = r;
    }

    public float getRed() {
        return r;
    }

    public void setGreen(float g) {
        this.g = g;
    }

    public float getGreen() {
        return g;
    }

    public void setBlue(float b) {
        this.b = b;
    }

    public float getBlue() {
        return b;
    }
}