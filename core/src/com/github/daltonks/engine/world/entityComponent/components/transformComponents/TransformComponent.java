//Contains location and rotation data for an Entity

package com.github.daltonks.engine.world.entityComponent.components.transformComponents;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.EngineMath;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.util.interfaces.Positionable;
import com.github.daltonks.engine.world.entityComponent.components.Component;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;

import javax.vecmath.Quat4f;

public class TransformComponent extends Component implements Positionable {
    //doubles are used for location due to the massive scale some games can have
    protected Vec3d loc = new Vec3d();
    protected Matrix4 rotationMatrix = new Matrix4();

    public TransformComponent(Entity entity, double x, double y, double z) {
        super(entity);
        loc.set(x, y, z);
    }

    public TransformComponent(Entity entity, double x, double y, double z, float qx, float qy, float qz, float qw) {
        super(entity);
        loc.set(x, y, z);
        setRotation(qx, qy, qz, qw);
    }

    public void update(EngineState engineState, double delta) {

    }

    public void setLocation(double[] loc) {
        setLocation(loc[0], loc[1], loc[2]);
    }

    public void setLocation(double x, double y, double z) {
        loc.set(x, y, z);
    }

    public void setLocation(Vec3d location) {
        setLocation(location.x(), location.y(), location.z());
    }

    public void setLocation(TransformComponent component, double xOffset, double yOffset, double zOffset) {
        Vec3d cLoc = component.getLocation();
        setLocation(cLoc.x() + xOffset, cLoc.y() + yOffset, cLoc.z() + zOffset);
    }

    public Vec3d getLocation() {
        return loc;
    }

    public Matrix4 getRotationMatrix() {
        return rotationMatrix;
    }

    public Quaternion getRotationQuatNew() {
        Quaternion quat = Pools.getQuaternion();
        rotationMatrix.getRotation(quat);
        return quat;
    }

    public Vec3d getForwardNew() {
        Vec3d forward = Pools.getVec3d();
        forward.set(0, 1, 0).multMatrix(getRotationMatrix());
        return forward;
    }

    public Vec3d getRightNew() {
        Vec3d right = Pools.getVec3d();
        right.set(1, 0, 0).multMatrix(getRotationMatrix());
        return right;
    }

    public void addRotation(float x, float y, float z) {
        if(x != 0)
            rotationMatrix.rotate(1, 0, 0, x);
        if(y != 0)
            rotationMatrix.rotate(0, 1, 0, y);
        if(z != 0)
            rotationMatrix.rotate(0, 0, 1, z);
    }

    public void setRotation(float eulerX, float eulerY, float eulerZ) {
        rotationMatrix.setFromEulerAngles(eulerX, eulerY, eulerZ);
    }

    public void setRotation(float qx, float qy, float qz, float qw) {
        Quat4f quat = Pools.getQuat4f();
        quat.set(qx, qy, qz, qw);
        float[] euler = Pools.getFloat4();
        EngineMath.quaternionToEulerXYZ(quat, euler);
        setRotation(euler[0], euler[1], euler[2]);
        Pools.recycle(quat);
        Pools.recycleFloat4(euler);
    }

    public void set(TransformComponent other) {
        loc.set(other.loc);
        System.arraycopy(other.rotationMatrix, 0, rotationMatrix, 0, 16);
    }
}