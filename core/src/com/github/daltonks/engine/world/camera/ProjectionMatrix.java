package com.github.daltonks.engine.world.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;

public class ProjectionMatrix {
    private boolean dirty;
    private int lastScreenWidth, lastScreenHeight;
    private float near, far;
    private Camera camera;
    private Matrix4 projectionMatrix = new Matrix4();

    public ProjectionMatrix(Camera camera, float near, float far) {
        this.camera = camera;
        this.near = near;
        this.far = far;
        updateMatrix();
    }

    private void updateMatrix() {
        lastScreenWidth = Gdx.graphics.getWidth();
        lastScreenHeight = Gdx.graphics.getHeight();

        double mult = near / 2;

        float screenRatio = (float) ((double) lastScreenWidth / lastScreenHeight * mult);
        projectionMatrix.setToProjection(
            -screenRatio, screenRatio,
            (float) -mult, (float) mult,
            near, far
        );
        camera.setDirty();
        dirty = false;
    }

    public Matrix4 getMatrix() {
        if(isDirty())
            updateMatrix();
        return projectionMatrix;
    }

    public boolean isDirty() {
        return dirty || Gdx.graphics.getWidth() != lastScreenWidth || Gdx.graphics.getHeight() != lastScreenHeight;
    }

    public void setNear(float near) {
        if(near != this.near) {
            this.near = near;
            dirty = true;
        }
    }

    public float getNear() {
        return near;
    }

    public void setFar(float far) {
        if(far != this.far) {
            this.far = far;
            dirty = true;
        }
    }

    public float getFar() {
        return far;
    }
}