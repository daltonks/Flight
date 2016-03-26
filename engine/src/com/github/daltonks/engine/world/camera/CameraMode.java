package com.github.daltonks.engine.world.camera;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.util.interfaces.Updatable;

public abstract class CameraMode implements Updatable {
    protected Camera camera;

    public CameraMode(Camera camera) {
        this.camera = camera;
    }

    public void update(EngineState engineState, double delta) {
        camera.getViewMatrix().setViewCenter(getViewCenter());
    }

    public abstract Vec3d getViewCenter();
}