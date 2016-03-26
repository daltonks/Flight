package com.github.daltonks.engine.world.camera;

import com.github.daltonks.engine.util.Vec3d;

public class FocusCameraMode extends CameraMode {

    protected Vec3d focus;

    public FocusCameraMode(Camera camera, Vec3d focus) {
        super(camera);
        this.focus = focus;
    }

    public void setFocus(Vec3d focus) {
        this.focus = focus;
    }

    public Vec3d getViewCenter() {
        return focus;
    }
}