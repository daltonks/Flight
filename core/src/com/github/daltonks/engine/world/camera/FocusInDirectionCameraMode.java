package com.github.daltonks.engine.world.camera;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Vec3d;

public class FocusInDirectionCameraMode extends FocusCameraMode {

    private Vec3d focusDirection;

    public FocusInDirectionCameraMode(Camera camera, Vec3d focusDirection, Vec3d up) {
        super(camera, new Vec3d());
        this.focusDirection = focusDirection;
        camera.getViewMatrix().setUp(up);
    }

    public void update(EngineState engineState, double delta) {
        focus.set(camera.getViewMatrix().getLocation());
        focus.add(focusDirection);
        super.update(engineState, delta);
    }
}