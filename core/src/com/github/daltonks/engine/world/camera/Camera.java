//Simple state-based Camera for vision in the 3D world

package com.github.daltonks.engine.world.camera;

import com.badlogic.gdx.math.Matrix4;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.util.interfaces.Updatable;
import com.github.daltonks.engine.world.FrustumCuller;

public class Camera implements Updatable {
    private boolean dirtyPVInverse, dirtyFrustum;
    private FrustumCuller frustumCuller;
    private CameraMode cameraMode;
    private ViewMatrix viewMatrix;
    private ProjectionMatrix projectionMatrix;
    private ProjectionMatrix nearProjectionMatrix;
    private ProjectionMatrix farProjectionMatrix;
    private Matrix4 projectionAndViewMatrix = new Matrix4();
    private Matrix4 projectionAndViewInverseMatrix = new Matrix4();

    public Camera() {
        frustumCuller = new FrustumCuller(this);
        viewMatrix = new ViewMatrix(this);
        nearProjectionMatrix = new ProjectionMatrix(this, 2, 2000);
        farProjectionMatrix = new ProjectionMatrix(this, 1990, 2000000);
        projectionMatrix = nearProjectionMatrix;
    }

    @Override
    public void update(EngineState engineState, double delta) {
        if(cameraMode != null) {
            cameraMode.update(engineState, delta);
        }
    }

    public Vec3d screenToWorldZPlaneNew(float openGLX, float openGLY) {
        Matrix4 projectionAndViewInverse = getProjectionAndViewInverseMatrix();
        Vec3d point = Pools.getVec3d().set(openGLX, openGLY, -1);
        double w = point.multMatrix(projectionAndViewInverse, 1);
        point.div(w);
        point.mult(-viewMatrix.getLocation().z() / point.z());
        point.add(viewMatrix.getLocation());
        return point;
    }

    public Matrix4 getProjectionAndViewInverseMatrix() {
        if(dirtyPVInverse) {
            projectionAndViewMatrix.set(projectionAndViewMatrix);
            projectionAndViewMatrix.mul(viewMatrix.getMatrix());

            projectionAndViewInverseMatrix.set(projectionAndViewMatrix);
            projectionAndViewInverseMatrix.inv();
            dirtyPVInverse = false;
        }
        return projectionAndViewInverseMatrix;
    }

    public FrustumCuller getFrustumCuller() {
        if(dirtyFrustum || viewMatrix.isDirty() || projectionMatrix.isDirty()) {
            frustumCuller.updateFrustum();
            dirtyFrustum = false;
        }
        return frustumCuller;
    }

    protected void setDirty() {
        dirtyFrustum = true;
        dirtyPVInverse = true;
    }

    public void setCameraMode(CameraMode mode) {
        cameraMode = mode;
    }

    public CameraMode getCameraMode() {
        return cameraMode;
    }

    public void useNearProjectionMatrix() {
        projectionMatrix = nearProjectionMatrix;
        setDirty();
    }

    public void useFarProjectionMatrix() {
        projectionMatrix = farProjectionMatrix;
        setDirty();
    }

    public ProjectionMatrix getProjectionMatrix() {
        return projectionMatrix;
    }

    public ViewMatrix getViewMatrix() {
        return viewMatrix;
    }
}