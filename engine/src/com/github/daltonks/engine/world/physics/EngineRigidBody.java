//my implementation of Bullet's RigidBody that contains my physics masks
//onCollision is automatically called

package com.github.daltonks.engine.world.physics;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.util.interfaces.Positionable;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;

import javax.vecmath.Vector3f;

public class EngineRigidBody extends RigidBody {
    private byte collidesWithMask;
    private byte personalCollisionMask;
    private CollisionHandler collisionHandler;
    private Entity entity;

    public EngineRigidBody(Entity entity, RigidBodyConstructionInfo constructionInfo, byte personalCollisionMask, byte collidesWithMask, CollisionHandler collisionHandler) {
        super(constructionInfo);
        this.entity = entity;
        this.personalCollisionMask = personalCollisionMask;
        this.collidesWithMask = collidesWithMask;
        this.collisionHandler = collisionHandler;
    }

    public void rotateToward(Positionable positionable, float maxRotVelocity) {
        Vec3d toTarget = positionable.getLocation().clone().sub(getEntity().getTransformComponent().getLocation());
        if(toTarget.lengthSquared() < .001) {
            Pools.recycle(toTarget);
            return;
        }

        Vec3d forward = getEntity().getTransformComponent().getForwardNew();
        toTarget.normalize();

        Vec3d cross = forward.clone().cross(toTarget);
        double angleToRotate = forward.angleTo(toTarget) * 120;

        if(angleToRotate < -maxRotVelocity) {
            angleToRotate = -maxRotVelocity;
        } else if(angleToRotate > maxRotVelocity) {
            angleToRotate = maxRotVelocity;
        }

        Vec3d angularVelocity = Pools.getVec3d().set(cross.x(), cross.y(), cross.z());

        angularVelocity.setLength(angleToRotate);
        Vector3f angularVelocityVec = Pools.getVector3f();
        angularVelocity.get(angularVelocityVec);
        setAngularVelocity(angularVelocityVec);

        Pools.recycle(toTarget);
        Pools.recycle(cross);
        Pools.recycle(angularVelocity);
        Pools.recycle(angularVelocityVec);
        Pools.recycle(forward);

        /*
        Vec3d toTarget = positionable.getLocation().clone().sub(getEntity().getTransformComponent().getLocation());
        if(toTarget.length() < .001f) {
            Pools.recycle(toTarget);
            return;
        }

        Vec3d forward = getEntity().getTransformComponent().getForwardNew();
        toTarget.normalize();

        Vec3d cross = forward.clone().cross(toTarget);
        double angleToRotate = forward.angleTo(toTarget) * 120;

        Quat4f faceQuat = Pools.getQuat4f();
        AxisAngle4d rotationAngle = Pools.getAxisAngle();
        rotationAngle.set(cross.x(), cross.y(), cross.z(), angleToRotate);
        faceQuat.set(rotationAngle);

        Vec3d right = getEntity().getTransformComponent().getRightNew();
        Vec3d zero = Pools.getVec3d().set(0, 0, 0);
        Vec3d planeNormal = forward.clone().normal(zero, right);
        Vec3d toRollTo = rollTo.getLocation().clone().sub(getEntity().getTransformComponent().getLocation());
        double rollAngle = toRollTo.angleComplementTo(planeNormal);
        System.out.println(Math.toDegrees(rollAngle));

        float[] afterFirstRot = Pools.getFloat16();
        Matrix.setRotateM(afterFirstRot, 0, (float) rotationAngle.angle, (float) rotationAngle.x, (float) rotationAngle.y, (float) rotationAngle.z);
        forward.multMatrix(afterFirstRot);
        rotationAngle.set(forward.x(), forward.y(), forward.z(), rollAngle);
        Quat4f rollQuat = Pools.getQuat4f();
        rollQuat.set(rotationAngle);
        rollQuat.mul(faceQuat);
        rotationAngle.set(rollQuat);

        Vec3d angularVelocity = Pools.getVec3d().set(rotationAngle.x, rotationAngle.y, rotationAngle.z);
        /*
        if(rotationAngle.angle < -maxRotVelocity) {
            rotationAngle.angle = -maxRotVelocity;
        } else if(rotationAngle.angle > maxRotVelocity) {
            rotationAngle.angle = maxRotVelocity;
        }
        angularVelocity.setLength(rotationAngle.angle);
        Vector3f angularVelocityVec = Pools.getVector3f();
        angularVelocity.get(angularVelocityVec);
        setAngularVelocity(angularVelocityVec);

        Pools.recycleFloat16(afterFirstRot);
        Pools.recycle(zero);
        Pools.recycle(rollQuat);
        Pools.recycle(planeNormal);
        Pools.recycle(faceQuat);
        Pools.recycle(rotationAngle);
        Pools.recycle(right);
        Pools.recycle(toTarget);
        Pools.recycle(cross);
        Pools.recycle(toRollTo);
        Pools.recycle(angularVelocity);
        Pools.recycle(angularVelocityVec);
        Pools.recycle(forward);*/
    }

    private Vector3f getLocalizedVectorNew(Vec3d vec) {
        float[] rotationMatrix = entity.getTransformComponent().getRotationMatrix();
        Vec3d vec3d = vec.clone().multMatrix(rotationMatrix);
        Pools.recycle(vec3d);
        Vector3f vec3f = Pools.getVector3f();
        vec3d.get(vec3f);
        return vec3f;
    }

    public void applyLocalTorque(Vec3d vec) {
        Vector3f vec3f = getLocalizedVectorNew(vec);
        applyTorque(vec3f);
        Pools.recycle(vec3f);
    }

    public void applyLocalTorqueImpulse(Vec3d vec) {
        Vector3f vec3f = getLocalizedVectorNew(vec);
        applyTorqueImpulse(vec3f);
        Pools.recycle(vec3f);
    }

    public void applyLocalForce(Vec3d vec) {
        Vector3f vec3f = getLocalizedVectorNew(vec);
        applyCentralForce(vec3f);
        Pools.recycle(vec3f);
    }

    public void applyForce(Vec3d vec) {
        Vector3f vec3f = Pools.getVector3f();
        vec.get(vec3f);
        applyCentralForce(vec3f);
        Pools.recycle(vec3f);
    }

    public void onCollision(EngineRigidBody otherBody) {
        if(collisionHandler != null) {
            collisionHandler.onCollision(getEntity(), otherBody.getEntity());
        }
    }

    public void setCollisionHandler(CollisionHandler handler) {
        this.collisionHandler = handler;
    }

    public void setCollisionMaskInfo(byte personalCollisionType, byte collisidesWithMask) {
        this.personalCollisionMask = personalCollisionType;
        this.collidesWithMask = collisidesWithMask;
    }

    public Entity getUserPointer() {
        return (Entity) super.getUserPointer();
    }

    public byte getCollidesWithMask() {
        return collidesWithMask;
    }

    public byte getPersonalCollisionMask() {
        return personalCollisionMask;
    }

    public Entity getEntity() {
        return (Entity) userObjectPointer;
    }
}