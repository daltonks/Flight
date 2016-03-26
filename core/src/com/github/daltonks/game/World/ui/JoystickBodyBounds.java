package com.github.daltonks.game.World.ui;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;
import com.github.daltonks.engine.world.ui.UIBoundsComponent;
import com.github.daltonks.game.subactivities.GameEngineState;

public class JoystickBodyBounds extends UIBoundsComponent {

    public JoystickBodyBounds(Entity entity) {
        super(entity, 1.5f, 1.5f);
    }

    public boolean onCollidedDown(EngineState engineState, float openGLX, float openGLY) {
        onCollidedSwipe(engineState, 0, 0, openGLX, openGLY);
        return true;
    }

    @Override
    public boolean onCollidedSwipe(EngineState engineState, float dx, float dy, float openGLX, float openGLY) {
        Vec3d joystickBodyLoc = GameEngineState.joystickBody.getTransformComponent().getLocation();
        float joystickBodyRadius = GameEngineState.joystickBody.getModelComponent().getRadius();
        Vec3d fingerWorldLoc = engineState.getUICamera().screenToWorldZPlaneNew(openGLX, openGLY);
        if(fingerWorldLoc.distanceTo(joystickBodyLoc) > joystickBodyRadius) {
            fingerWorldLoc.sub(joystickBodyLoc).normalize().mult(joystickBodyRadius);
            fingerWorldLoc.add(joystickBodyLoc);
        }
        GameEngineState.joystick.getTransformComponent().setLocation(fingerWorldLoc);
        Pools.recycle(fingerWorldLoc);
        return true;
    }

    public boolean onNonCollidedSwipe(EngineState engineState, float dx, float dy, float openGLX, float openGLY) {
        return onCollidedSwipe(engineState, dx, dy, openGLX, openGLY);
    }
}