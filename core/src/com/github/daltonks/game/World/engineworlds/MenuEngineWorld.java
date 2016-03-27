package com.github.daltonks.game.World.engineworlds;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.states.inputevents.ClickDownEvent;
import com.github.daltonks.engine.states.inputevents.DragEvent;
import com.github.daltonks.engine.states.inputevents.ClickUpEvent;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.camera.RotateAroundCameraMode;

public class MenuEngineWorld extends SpaceEngineWorld {

    private RotateAroundCameraMode cameraMode;

    public MenuEngineWorld(EngineState engineState) {
        super(engineState);
        cameraMode = new RotateAroundCameraMode(getCamera(), new Vec3d(), 3000);
        getCamera().setCameraMode(cameraMode);
    }

    public void update(double delta) {
        cameraMode.addRotateAroundAngles(0, 0, (float) -delta);
        super.update(delta);
    }

    @Override
    protected boolean shouldCreateRigidBodies() {
        return false;
    }

    @Override
    public void onEnterState() {

    }

    @Override
    public void onLeaveState() {

    }

    @Override
    public void onPause() {

    }
}