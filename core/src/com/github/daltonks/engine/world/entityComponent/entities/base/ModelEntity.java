//Utility class for creating an Entity with a simple, static Model

package com.github.daltonks.engine.world.entityComponent.entities.base;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.world.entityComponent.components.modelComponents.ModelComponent;
import com.github.daltonks.engine.world.entityComponent.components.modelComponents.ModelLevelOfDetailComponent;
import com.github.daltonks.engine.world.entityComponent.components.transformComponents.RigidBodyDynamicTransformComponent;
import com.github.daltonks.engine.world.entityComponent.components.transformComponents.RigidBodyStaticTransformComponent;
import com.github.daltonks.engine.world.entityComponent.components.transformComponents.TransformComponent;
import com.github.daltonks.engine.world.models.Model;
import com.github.daltonks.engine.world.physics.CollisionHandler;

public class ModelEntity extends Entity {
    public ModelEntity(EngineState engineState,
                       double x, double y, double z,
                       float qx, float qy, float qz, float qw,
                       float scale, Model model,
                       byte personalCollisionMask, byte collidesWithMask,
                       boolean disableDeactivation, CollisionHandler collisionHandler, boolean staticModel,
                       boolean createRigidBody) {

        super(engineState);

        if(scale == -1) scale = model.getModelInfo().defaultScale;

        ModelComponent modelComponent;
        if(model.getModelInfo().levelOfDetail > 0) {
            modelComponent = new ModelLevelOfDetailComponent(this, model, scale);
        } else {
            modelComponent = new ModelComponent(this, model, scale);
        }
        setDrawableComponent(modelComponent);

        TransformComponent transformComponent;
        if(createRigidBody && model.getModelInfo().hasRigidBodyInfo()) {
            modelComponent.createRigidBody(engineState, x, y, z, qx, qy, qz, qw, personalCollisionMask, collidesWithMask, collisionHandler, disableDeactivation);
            if(staticModel) {
                transformComponent = new RigidBodyStaticTransformComponent(this, x, y, z);
            } else {
                transformComponent = new RigidBodyDynamicTransformComponent(this, x, y, z);
            }
        } else {
            transformComponent = new TransformComponent(this, x, y, z, qx, qy, qz, qw);
        }
        setTransformComponent(transformComponent);
    }

    public ModelEntity(EngineState engineState, Model model) {
        this(engineState, 0, 0, 0, 0, 0, 0, 0, -1, model);
    }

    //No rigid body
    public ModelEntity(EngineState engineState,
                       double x, double y, double z,
                       float qx, float qy, float qz, float qw,
                       float scale,
                       Model model) {

        this(engineState, x, y, z, qx, qy, qz, qw, scale, model,
                (byte) 0, (byte) 0, false, null, false,
                false);
    }

    //Rigid body
    public ModelEntity(EngineState engineState,
                              double x, double y, double z,
                              float qx, float qy, float qz, float qw,
                              float scale, Model model,
                              byte personalCollisionMask, byte collidesWithMask,
                              boolean disableDeactivation, CollisionHandler collisionHandler,
                              boolean staticModel) {

        this(engineState, x, y, z, qx, qy, qz, qw,
                scale, model,
                personalCollisionMask, collidesWithMask, disableDeactivation,
                collisionHandler, staticModel,
                true);
    }
}