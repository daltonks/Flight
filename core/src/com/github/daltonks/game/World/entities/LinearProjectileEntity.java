package com.github.daltonks.game.World.entities;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.entityComponent.components.modelComponents.ModelComponent;
import com.github.daltonks.engine.world.entityComponent.components.transformComponents.RigidBodyDynamicTransformComponent;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;
import com.github.daltonks.engine.world.models.Model;
import com.github.daltonks.engine.world.physics.CollisionHandler;
import com.github.daltonks.game.World.physics.CollisionMasks;

import javax.vecmath.Vector3f;

public class LinearProjectileEntity extends Entity {
    private double age;
    private double ageOfDeath;

    public LinearProjectileEntity(EngineState engineState, Model model,
                                  Vec3d location,
                                  Vec3d force,
                                  Vec3d torque,
                                  byte team,
                                  CollisionHandler collisionHandler,
                                  double ageOfDeath) {

        super(engineState);
        this.ageOfDeath = ageOfDeath;
        setDrawableComponent(new ModelComponent(this, model));
        getModelComponent().createRigidBody(
                engineState,
                location.x(), location.y(), location.z(),
                0, 0, 0, 1,
                CollisionMasks.getAttackMask(team), CollisionMasks.getAttackCollidesWithMask(team), collisionHandler, true);
        setTransformComponent(new RigidBodyDynamicTransformComponent(this, location.x(), location.y(), location.z()));
        Vector3f vec = Pools.getVector3f();
        force.get(vec);
        getModelComponent().getRigidBody().applyCentralImpulse(vec);
        torque.get(vec);
        getModelComponent().getRigidBody().applyTorqueImpulse(vec);
        Pools.recycle(vec);
    }

    public void update(EngineState engineState, double delta) {
        super.update(engineState, delta);
        age += delta;
        double shrinkTime = Math.min(ageOfDeath, .3);
        if(ageOfDeath - age < shrinkTime) {
            double defaultScale = getModelComponent().getModel().getModelInfo().defaultScale;
            double subtract = (1 - (ageOfDeath - age) / shrinkTime) * defaultScale;
            double scale = defaultScale - subtract;
            getModelComponent().setScale((float) scale);
        }
        if(age >= ageOfDeath) {
            engineState.getEngineWorld().removeEntity(this);
        }
    }
}