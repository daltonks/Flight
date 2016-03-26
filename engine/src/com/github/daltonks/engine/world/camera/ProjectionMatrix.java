package com.github.daltonks.engine.world.camera;

import android.opengl.Matrix;
import com.github.daltonks.engine.util.Util;

public class ProjectionMatrix {
    private boolean dirty;
    private int lastScreenWidth, lastScreenHeight;
    private float near, far;
    private Camera camera;
    private float[] projectionMatrix = new float[16];

    public ProjectionMatrix(Camera camera, float near, float far) {
        this.camera = camera;
        this.near = near;
        this.far = far;
        updateMatrix();
    }

    private void updateMatrix() {
        lastScreenWidth = Util.getDisplayWidth();
        lastScreenHeight = Util.getDisplayHeight();

        double mult = near / 2;

        float screenRatio = (float) ((double) lastScreenWidth / lastScreenHeight * mult);
        Matrix.frustumM(projectionMatrix, 0,
                -screenRatio, screenRatio,
                (float) -mult, (float) mult,
                near, far
        );
        camera.setDirty();
        dirty = false;
    }

    public float[] getMatrix() {
        if(isDirty())
            updateMatrix();
        return projectionMatrix;
    }

    public boolean isDirty() {
        return dirty || Util.getDisplayWidth() != lastScreenWidth || Util.getDisplayHeight() != lastScreenHeight;
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