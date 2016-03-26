package com.github.daltonks.game.World.engineworlds;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.states.touchevents.DownTouchEvent;
import com.github.daltonks.engine.states.touchevents.MoveTouchEvent;
import com.github.daltonks.engine.states.touchevents.UpTouchEvent;
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
    public void onFingerDown(DownTouchEvent event) {

    }

    @Override
    public void onFingersMove(MoveTouchEvent event) {

    }

    @Override
    public void onFingerUp(UpTouchEvent event) {

    }

    @Override
    public void onEnterSubActivity() {

    }

    @Override
    public void onLeaveSubActivity() {

    }

    @Override
    public void onActivityPause() {

    }

    @Override
    protected boolean shouldCreateRigidBodies() {
        return false;
    }
}