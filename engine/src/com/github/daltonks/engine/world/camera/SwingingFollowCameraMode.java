package com.github.daltonks.engine.world.camera;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.EngineMath;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.entityComponent.components.transformComponents.TransformComponent;
import com.github.daltonks.engine.world.physics.PhysicsWorld;

public class SwingingFollowCameraMode extends CameraMode {

    private double locationSpeedModifier, viewCenterSpeedModifier;
    private Vec3d offset, viewCenterOffset;
    private Vec3d lastViewCenter = new Vec3d();
    private TransformComponent focus;

    public SwingingFollowCameraMode(Camera camera, TransformComponent focus, Vec3d offset, Vec3d viewCenterOffset, double locationSpeedModifier, double viewCenterSpeedModifier) {
        super(camera);
        this.focus = focus;
        this.offset = offset;
        this.viewCenterOffset = viewCenterOffset;
        this.locationSpeedModifier = locationSpeedModifier;
        this.viewCenterSpeedModifier = viewCenterSpeedModifier;

        //Location
        Vec3d loc = offset.clone();
        loc.multMatrix(focus.getRotationMatrix()).add(focus.getLocation());
        camera.getViewMatrix().setLocation(loc);

        //View Center
        loc.set(viewCenterOffset);
        loc.multMatrix(focus.getRotationMatrix()).add(focus.getLocation());
        lastViewCenter.set(loc);

        Pools.recycle(loc);
    }

    @Override
    public void update(EngineState engineState, double delta) {
        if(focus == null || PhysicsWorld.numOfTicksProcessedLast == 0) {
            return;
        }

        //Location
        Vec3d newLoc = offset.clone();
        newLoc.multMatrix(focus.getRotationMatrix()).add(focus.getLocation());
        smooth(camera.getViewMatrix().getLocation(), newLoc, locationSpeedModifier);
        camera.getViewMatrix().setLocation(newLoc);

        //View Center
        Vec3d newViewCenter = newLoc.set(viewCenterOffset);
        newViewCenter.multMatrix(focus.getRotationMatrix()).add(focus.getLocation());
        smooth(lastViewCenter, newViewCenter, viewCenterSpeedModifier);
        lastViewCenter.set(newViewCenter);

        //Up
        Vec3d upVec = newViewCenter.set(0, 0, 1);
        upVec.multMatrix(focus.getRotationMatrix());
        camera.getViewMatrix().setUp(upVec);

        Pools.recycle(newLoc);

        super.update(engineState, delta);
    }

    private void smooth(Vec3d oldLoc, Vec3d newLoc, double modifier) {
        newLoc.sub(oldLoc);
        newLoc.mult(modifier);
        newLoc.add(oldLoc);
    }

    public void setOffset(Vec3d offset) {
        this.offset = offset;
    }

    public Vec3d getOffset() {
        return offset;
    }

    public Vec3d getViewCenter() {
        return lastViewCenter;
    }

    public void setFocus(TransformComponent component) {
        this.focus = component;
    }

    private static float[] forwardToUpRotation = new float[16];
    static {
        EngineMath.setRotateEulerM(forwardToUpRotation, 0, 90, 0, 0);
    }
}