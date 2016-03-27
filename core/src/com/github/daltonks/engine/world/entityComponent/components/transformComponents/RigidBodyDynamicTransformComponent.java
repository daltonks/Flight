//Transfrom Component of a rigid body that constantly moves around, such as a Player

package com.github.daltonks.engine.world.entityComponent.components.transformComponents;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;

public class RigidBodyDynamicTransformComponent extends RigidBodyStaticTransformComponent {
    public RigidBodyDynamicTransformComponent(Entity entity, double x, double y, double z) {
        super(entity, x, y, z);
        updateLocation();
    }

    public void update(EngineState engineState, double delta) {
        super.update(engineState, delta);
        updateLocation();
    }
}