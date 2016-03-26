package com.github.daltonks.game.World.livingentities;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.world.entityComponent.components.Component;
import com.github.daltonks.engine.world.models.Model;
import com.github.daltonks.engine.world.physics.CollisionHandler;

public class AIEntity extends LivingEntity {

    private Component aiComponent;

    public AIEntity(EngineState engineState,
                    double x, double y, double z,
                    float qx, float qy, float qz, float qw,
                    Model model,
                    byte team,
                    CollisionHandler collisionHandler) {

        super(engineState, x, y, z, qx, qy, qz, qw, model, team, collisionHandler);
        aiComponent = new FlyingAIComponent(this);
    }

    public void update(EngineState engineState, double delta) {
        super.update(engineState, delta);
        aiComponent.update(engineState, delta);
    }

    public void setAIComponent(Component component) {
        this.aiComponent = component;
    }
}