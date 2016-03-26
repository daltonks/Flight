package com.github.daltonks.engine.world.camera;

import android.opengl.Matrix;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.EngineMath;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;

public class RotateAroundCameraMode extends FocusCameraMode {

    private float rotateAroundHeight;
    private Vec3d rotateAroundAngles = new Vec3d();
    private float[] rotateAroundMatrix = new float[16];

    public RotateAroundCameraMode(Camera camera, Vec3d focus, float rotateAroundHeight) {
        super(camera, focus);
        this.rotateAroundHeight = rotateAroundHeight;
        Matrix.setIdentityM(rotateAroundMatrix, 0);
    }

    public void update(EngineState engineState, double delta) {
        Vec3d loc = Pools.getVec3d();
        loc.set(0, 0, 1);
        camera.getViewMatrix().setUp(loc);
        Pools.recycle(loc);
        super.update(engineState, delta);
    }

    public void addRotateAroundAngles(float x, float y, float z) {
        rotateAroundAngles.add(x, y, z);
        if(rotateAroundAngles.x() >= 90) {
            rotateAroundAngles.x(89);
        } else if(rotateAroundAngles.x() < -90) {
            rotateAroundAngles.x(-89);
        }
        EngineMath.setRotateEulerM(rotateAroundMatrix, 0, rotateAroundAngles.xf(), rotateAroundAngles.yf(), rotateAroundAngles.zf());

        Vec3d loc = Pools.getVec3d();
        loc.set(0, rotateAroundHeight, 0);
        loc.multMatrix(rotateAroundMatrix);
        loc.add(focus);

        camera.getViewMatrix().setLocation(loc);

        Pools.recycle(loc);
    }

    public void setRotateAroundHeight(float height) {
        this.rotateAroundHeight = height;
    }

    public float getRotateAroundHeight() {
        return rotateAroundHeight;
    }

    public float[] getRotateAroundMatrix() {
        return rotateAroundMatrix;
    }
}