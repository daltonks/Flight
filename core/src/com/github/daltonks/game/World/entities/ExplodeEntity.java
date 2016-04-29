package com.github.daltonks.game.World.entities;

import com.github.daltonks.engine.EngineShaderProgram;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.world.camera.Camera;
import com.github.daltonks.engine.world.entityComponent.components.transformComponents.TransformComponent;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;

public class ExplodeEntity extends Entity {

    public ExplodeEntity(EngineState engineState, Entity entity) {
        super(engineState);
        this.setDrawableComponent(entity.getDrawableComponent());
        TransformComponent trans = new TransformComponent(this, 0, 0, 0);
        trans.getRotationMatrix().set(entity.getTransformComponent().getRotationMatrix());
        trans.getLocation().set(entity.getTransformComponent().getLocation());
    }

    double explodeAccum = 0;
    @Override
    public void update(EngineState state, double delta) {
        super.update(state, delta);
        explodeAccum += delta;
        if(explodeAccum > 1) {
            state.getEngineWorld().removeEntity(this);
        }
    }

    @Override
    public void draw(Camera camera) {
        EngineShaderProgram.setExplodeTime((float) explodeAccum);
        super.draw(camera);
        EngineShaderProgram.setExplodeTime(0);
    }
}