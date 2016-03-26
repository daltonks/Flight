package com.github.daltonks.engine.world.camera;

import android.opengl.Matrix;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.EngineMath;
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
        float[] entRotMatrix = focus.getRotationMatrix();

        Vec3d offsetPlusLoc = offset.clone();
        offsetPlusLoc.multMatrix(entRotMatrix).add(focus.getLocation());
        camera.getViewMatrix().setLocation(offsetPlusLoc);

        float[] entityRot = Pools.getFloat16();
        Matrix.multiplyMM(entityRot, 0, entRotMatrix, 0, rightAngleRotation, 0);
        Vec3d upVec = offset.clone();
        upVec.multMatrix(entityRot);
        upVec.normalize();
        camera.getViewMatrix().setUp(upVec);

        Pools.recycle(upVec);
        Pools.recycle(offsetPlusLoc);
        Pools.recycleFloat16(entityRot);

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

    private static float[] rightAngleRotation = new float[16];
    static {
        EngineMath.setRotateEulerM(rightAngleRotation, 0, 90, 0, 0);
    }
}