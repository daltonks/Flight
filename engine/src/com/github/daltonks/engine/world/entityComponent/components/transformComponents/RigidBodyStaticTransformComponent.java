//Transform Component that gets its data from a Bullet rigid body

package com.github.daltonks.engine.world.entityComponent.components.transformComponents;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.Transform;
import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;

public class RigidBodyStaticTransformComponent extends TransformComponent {
    public RigidBodyStaticTransformComponent(Entity entity, double x, double y, double z) {
        super(entity, x, y, z);
        updateLocation();
    }

    private static Transform tempTransform = new Transform();
    public void updateLocation() {
        RigidBody rigidBody = getEntity().getModelComponent().getRigidBody();
        rigidBody.getWorldTransform(tempTransform);

        loc.set(
                ((double) tempTransform.origin.x) * Engine.PHYSICS_WORLD_TO_ENGINE_SCALE,
                ((double) tempTransform.origin.y) * Engine.PHYSICS_WORLD_TO_ENGINE_SCALE,
                ((double) tempTransform.origin.z) * Engine.PHYSICS_WORLD_TO_ENGINE_SCALE);

        MatrixUtil.getOpenGLSubMatrix(tempTransform.basis, rotationMatrix);
        rotationMatrix[12] = 0;
        rotationMatrix[13] = 0;
        rotationMatrix[14] = 0;
        rotationMatrix[15] = 1;
    }

    public void setLocation(double x, double y, double z) {
        loc.set(x, y, z);
        x *= Engine.ENGINE_TO_PHYSICS_WORLD_SCALE;
        y *= Engine.ENGINE_TO_PHYSICS_WORLD_SCALE;
        z *= Engine.ENGINE_TO_PHYSICS_WORLD_SCALE;
        setRigidBodyLocation((float) x, (float) y, (float) z);
    }

    private void setRigidBodyLocation(float x, float y, float z) {
        RigidBody rigidBody = getEntity().getModelComponent().getRigidBody();
        rigidBody.getWorldTransform(tempTransform);
        tempTransform.origin.set(x, y, z);
        rigidBody.setWorldTransform(tempTransform);
    }

    public void setRotation(float eulerX, float eulerY, float eulerZ) {
        RigidBody rigidBody = getEntity().getModelComponent().getRigidBody();
        rigidBody.getWorldTransform(tempTransform);
        tempTransform.basis.setIdentity();
        tempTransform.basis.rotX(eulerX);
        tempTransform.basis.rotY(eulerY);
        tempTransform.basis.rotZ(eulerZ);
        rigidBody.setWorldTransform(tempTransform);
    }

    public void addRotation(float x, float y, float z) {
        RigidBody rigidBody = getEntity().getModelComponent().getRigidBody();
        rigidBody.getWorldTransform(tempTransform);
        tempTransform.basis.rotX(x);
        tempTransform.basis.rotY(y);
        tempTransform.basis.rotZ(z);
        rigidBody.setWorldTransform(tempTransform);
    }
}