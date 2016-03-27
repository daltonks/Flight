package com.github.daltonks.game.states.inputhandlers;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.states.inputevents.*;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.game.states.GameEngineState;

public class GameInputHandler extends EngineInputHandler<GameEngineState> {

    public GameInputHandler(GameEngineState state) {
        super(state);
    }

    public void update(EngineState engineState, double delta) {

    }

    private static float turnDiv = 80;
    @Override
    public void onDrag(DragEvent event) {
        Vec3d throttle = Pools.getVec3d().set(0, 0, 0);
        for(int i = 0; i < event.getClickTrackers().size(); i++) {
            ClickTracker tracker = event.getClickTrackers().get(i);
            boolean roll;
            if(Gdx.app.getType() == Application.ApplicationType.Android) {
                //Roll if on left side of screen
                roll = tracker.startingX <= Gdx.graphics.getWidth() / 2;
            } else {
                roll = true;
            }

            if(roll) {
                throttle.add(-tracker.getDeltaY() / turnDiv, tracker.getDeltaX() / turnDiv, 0);
            } else {
                throttle.add(-tracker.getDeltaY() / turnDiv, 0, -tracker.getDeltaX() / turnDiv);
            }
        }
        getEngineState().getEngineWorld().getPlayer().getModelComponent().getRigidBody().applyLocalTorque(throttle);
        Pools.recycle(throttle);
    }

    public void onMouseMove(int deltaX, int deltaY) {
        Vec3d throttle = Pools.getVec3d();
        throttle.set(-deltaY / turnDiv, 0, -deltaX / turnDiv);
        getEngineState().getEngineWorld().getPlayer().getModelComponent().getRigidBody().applyLocalTorque(throttle);
        Pools.recycle(throttle);
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