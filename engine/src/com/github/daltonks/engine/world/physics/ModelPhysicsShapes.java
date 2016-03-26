//Generates the Bullet physics shapes for the individual models
//Supports arbitrary hulls and some primitives

package com.github.daltonks.engine.world.physics;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.linearmath.Transform;
import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.world.models.Model;
import com.github.daltonks.engine.world.models.Models;

import javax.vecmath.Vector3f;

public class ModelPhysicsShapes {
    public static CollisionShape getPhysicsShape(Model model, float scale) {
        boolean defaultScale = scale == model.getModelInfo().defaultScale;
        if(defaultScale && model.getDefaultScaleCollisionShape() != null) {
            return model.getDefaultScaleCollisionShape();
        }

        CollisionShape finalShape = null;

        short[] collisionMeshTypes = model.getModelInfo().collisionModelsShorts;
        if(collisionMeshTypes != null && collisionMeshTypes.length > 0) {
            CompoundShape compoundShape = null;
            boolean hasMultipleCollisionModels = collisionMeshTypes.length > 1;

            if(hasMultipleCollisionModels) {
                compoundShape = new CompoundShape();
                finalShape = compoundShape;
            }

            for(short collisionMeshType : collisionMeshTypes) {
                CollisionShape shape;

                //self
                if(collisionMeshType == -1) {
                    collisionMeshType = model.getID();
                }

                switch(collisionMeshType) {
                    //SPHERE
                    case -3: {
                        shape = new SphereShape(model.getModelInfo().radius * scale * Engine.ENGINE_TO_PHYSICS_WORLD_SCALE);
                        if(model.getModelInfo().mass != 0) {
                            shape.calculateLocalInertia(model.getModelInfo().mass, new Vector3f(0, 0, 0));
                        }
                        break;
                    }

                    //BOX
                    case -2: {
                        float xHalfWidth = (model.getXLength()) / 2 * scale * Engine.ENGINE_TO_PHYSICS_WORLD_SCALE;
                        float yHalfWidth = (model.getYLength()) / 2 * scale * Engine.ENGINE_TO_PHYSICS_WORLD_SCALE;
                        float zHalfWidth = (model.getZLength()) / 2 * scale * Engine.ENGINE_TO_PHYSICS_WORLD_SCALE;
                        shape = new BoxShape(new Vector3f(xHalfWidth, yHalfWidth, zHalfWidth));
                        if(model.getModelInfo().mass != 0) {
                            shape.calculateLocalInertia(model.getModelInfo().mass, new Vector3f(0, 0, 0));
                        }
                        break;
                    }

                    //non-primitive model
                    case -1:
                        collisionMeshType = model.getID();
                    default: {
                        Model collisionModel = Models.get(collisionMeshType);
                        if(model.getModelInfo().mass == 0) {
                            shape = PhysicsWorld.createStaticShape(collisionModel, scale);
                        } else {
                            shape = PhysicsWorld.createDynamicConvexShape(collisionModel, scale);
                        }
                        break;
                    }
                }

                if(hasMultipleCollisionModels) {
                    Transform transform = new Transform();
                    transform.setIdentity();
                    compoundShape.addChildShape(transform, shape);
                } else {
                    finalShape = shape;
                }
            }
        }
        if(defaultScale) {
            model.setDefaultScaleCollisionShape(finalShape);
        }
        return finalShape;
    }
}