package com.github.daltonks.game.World.models;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.EngineMath;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.entityComponent.entities.base.ModelEntity;
import com.github.daltonks.engine.world.models.Models;

public class SpaceDust extends ModelEntity {

    public SpaceDust(EngineState engineState) {
        super(engineState, Models.get("spacedust"));
        moveToRandomLoc(engineState);
        this.getTransformComponent().setRotation((float) Math.random() * 360, (float) Math.random() * 360, (float) Math.random() * 360);
    }

    public void update(EngineState engineState, double delta) {
        Vec3d camLoc = engineState.getEngineWorld().getCamera().getViewMatrix().getLocation();
        if(getTransformComponent().getLocation().distanceSquaredTo(camLoc) > 750 * 750) {
            moveToRandomLoc(engineState);
        }
    }

    private void moveToRandomLoc(EngineState engineState) {
        Vec3d loc = EngineMath.getRandomPointOnSphereSurfaceNew(600);
        loc.add(engineState.getEngineWorld().getCamera().getViewMatrix().getLocation());
        getTransformComponent().setLocation(loc);
        Pools.recycle(loc);
    }
}