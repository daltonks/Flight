package com.github.daltonks.engine.world.camera;

import com.badlogic.gdx.math.Matrix4;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.entityComponent.components.transformComponents.TransformComponent;

public class FollowCameraMode extends CameraMode {

    private TransformComponent focus;
    private Vec3d offset;

    public FollowCameraMode(Camera camera, TransformComponent focus, Vec3d offset) {
        super(camera);
        this.focus = focus;
        this.offset = offset;
    }

    @Override
    public void update(EngineState engineState, double delta) {
        if(focus == null) {
            return;
        }
        Matrix4 entRotMatrix = focus.getRotationMatrix();

        Vec3d offsetPlusLoc = offset.clone();
        offsetPlusLoc.multMatrix(entRotMatrix).add(focus.getLocation());
        camera.getViewMatrix().setLocation(offsetPlusLoc);

        Matrix4 rotMatrix = Pools.getMatrix4();
        rotMatrix.set(entRotMatrix);
        rotMatrix.mul(rightAngleRotation);
        Vec3d upVec = offset.clone();
        upVec.multMatrix(rotMatrix);
        upVec.normalize();
        camera.getViewMatrix().setUp(upVec);

        Pools.recycle(upVec);
        Pools.recycle(offsetPlusLoc);
        Pools.recycle(rotMatrix);

        super.update(engineState, delta);
    }

    public void setOffset(Vec3d offset) {
        this.offset = offset;
    }

    public Vec3d getOffset() {
        return offset;
    }

    public Vec3d getViewCenter() {
        return focus.getLocation();
    }

    public void setFocus(TransformComponent component) {
        this.focus = component;
    }

    private static Matrix4 rightAngleRotation = new Matrix4();
    static {
        rightAngleRotation.setFromEulerAngles(90, 0, 0);
    }
}