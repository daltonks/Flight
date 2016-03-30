package com.github.daltonks.game.states.inputhandlers;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.states.inputevents.*;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.camera.CameraMode;
import com.github.daltonks.engine.world.camera.SwingingFollowCameraMode;
import com.github.daltonks.game.states.GameEngineState;

public class GameInputHandler extends EngineInputHandler<GameEngineState> {

    public GameInputHandler(GameEngineState state) {
        super(state);
    }

    @Override
    public void update(EngineState engineState, double delta) {

    }

    @Override
    public void onClickDown(ClickDownEvent event) {
        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void onUICapturedClickDown(ClickDownEvent event) {
        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void onDrag(DragEvent event) {
        for(int i = 0; i < event.getClickTrackers().size(); i++) {
            ClickTracker tracker = event.getClickTrackers().get(i);
            if (Gdx.app.getType() == Application.ApplicationType.Android) {
                //Yaw if on right side of screen
                if (tracker.startingX > Gdx.graphics.getWidth() / 2) {
                    yaw((int) tracker.getDeltaX(), (int) tracker.getDeltaY());
                } else {
                    roll((int) tracker.getDeltaX(), (int) tracker.getDeltaY());
                }
            } else {
                roll((int) tracker.getDeltaX(), (int) tracker.getDeltaY());
            }
        }
    }

    @Override
    public void onMouseMove(int deltaX, int deltaY) {
        if(Gdx.input.isCursorCatched()) {
            if(!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                yaw(deltaX, deltaY);
            } else {
                roll(deltaX, deltaY);
            }
        }
    }

    private static float turnDiv = 80;
    private void roll(int deltaX, int deltaY) {
        Vec3d throttle = Pools.getVec3d().set(0, 0, 0);
        throttle.add(-deltaY / turnDiv, deltaX / turnDiv, 0);
        getEngineState().getEngineWorld().getPlayer().getModelComponent().getRigidBody().applyLocalTorque(throttle);
        Pools.recycle(throttle);
    }

    private void yaw(int deltaX, int deltaY) {
        Vec3d throttle = Pools.getVec3d().set(0, 0, 0);
        throttle.add(-deltaY / turnDiv, 0, -deltaX / turnDiv);
        getEngineState().getEngineWorld().getPlayer().getModelComponent().getRigidBody().applyLocalTorque(throttle);
        Pools.recycle(throttle);
    }

    private static float distancePerScroll = 5;
    private static float minOffsetLength = 30;
    private static float maxOffsetLength = 100;
    @Override
    public void onScroll(int amount) {
        CameraMode cameraMode = getEngineState().getEngineWorld().getCamera().getCameraMode();
        SwingingFollowCameraMode swingingMode = (SwingingFollowCameraMode) cameraMode;
        Vec3d offset = swingingMode.getOffset();
        double length = offset.length();
        float move = distancePerScroll * Math.signum(amount);
        double newLength;
        if(length + move < minOffsetLength) {
            newLength = minOffsetLength;
        } else if(length + move > maxOffsetLength) {
            newLength = maxOffsetLength;
        } else {
            newLength = length + move;
        }
        offset.setLength(newLength);
    }

    @Override
    public void onKeyDown(int keycode){
        if(keycode == Input.Keys.ESCAPE) {
            Gdx.input.setCursorCatched(false);
        }
    }

    public static Vec3d getThrottleNew() {
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            Vec3d throttle = GameEngineState.joystick.getTransformComponent().getLocation().clone();
            throttle.sub(GameEngineState.joystickBody.getTransformComponent().getLocation());
            throttle.div(GameEngineState.joystickBody.getModelComponent().getRadius());
            return throttle;
        } else {
            Vec3d throttle = Pools.getVec3d();
            throttle.set(0, 0, 0);
            if(Gdx.input.isKeyPressed(Input.Keys.W)) {
                throttle.add(0, 1, 0);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.S)) {
                throttle.add(0, -1, 0);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.A)) {
                throttle.add(-1, 0, 0);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.D)) {
                throttle.add(1, 0, 0);
            }
            throttle.setLength(1);
            return throttle;
        }
    }

    public static void resetThrottle() {
        if(GameEngineState.joystick != null) {
            GameEngineState.joystick.getTransformComponent().setLocation(GameEngineState.joystickBody.getTransformComponent().getLocation());
        }
    }
}