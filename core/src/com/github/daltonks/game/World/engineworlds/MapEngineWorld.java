package com.github.daltonks.game.World.engineworlds;

import android.opengl.GLES20;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.states.touchevents.DownTouchEvent;
import com.github.daltonks.engine.states.touchevents.FingerTracker;
import com.github.daltonks.engine.states.touchevents.MoveTouchEvent;
import com.github.daltonks.engine.states.touchevents.UpTouchEvent;
import com.github.daltonks.engine.util.Color;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.camera.FocusInDirectionCameraMode;
import com.github.daltonks.engine.world.camera.ViewMatrix;
import com.github.daltonks.engine.world.entityComponent.components.transformComponents.TransformComponent;
import com.github.daltonks.engine.world.entityComponent.entities.base.ModelEntity;
import com.github.daltonks.engine.world.models.Models;
import com.github.daltonks.game.subactivities.CursorDrawing;

import java.util.LinkedList;

public class MapEngineWorld extends SpaceEngineWorld {

    private ModelEntity paperPlane;

    public MapEngineWorld(EngineState engineState) {
        super(engineState, 0);
        getCamera().setCameraMode(new FocusInDirectionCameraMode(getCamera(), new Vec3d(0, 0, -1), new Vec3d(0, 1, 0)));
    }

    public void init() {
        super.init();
        loadStaticBodies("game");
        paperPlane = new ModelEntity(getEngineState(), Models.get("plane"));
        paperPlane.getModelComponent().setScale(50);
        addStaticEntity(paperPlane);
    }

    protected void drawNear() {
        super.drawNear();
        Gdx.gl.glDisable(Gdx.gl.GL_DEPTH_TEST);
        CursorDrawing.drawCursor(getEngineState(), paperPlane, Color.PLAYER_BLUE, getCamera(), 3, 1, true);
        Gdx.gl.glEnable(Gdx.gl.GL_DEPTH_TEST);
    }

    protected void drawFar() {
        super.drawNear();
        Gdx.gl.glDisable(Gdx.gl.GL_DEPTH_TEST);
        CursorDrawing.drawCursor(getEngineState(), paperPlane, Color.PLAYER_BLUE, getCamera(), 3, 1, true);
        Gdx.gl.glEnable(Gdx.gl.GL_DEPTH_TEST);
    }

    @Override
    public void onFingerDown(DownTouchEvent event) {

    }

    private static double mapMoveSpeed = .002;
    private static double minZ = 1, maxZ = 1000000;
    @Override
    public void onFingersMove(MoveTouchEvent event) {
        LinkedList<FingerTracker> fingerTrackers = event.getFingerTrackers();
        Vec3d cameraLocation = getCamera().getViewMatrix().getLocation();
        double speed = mapMoveSpeed * cameraLocation.z();
        ViewMatrix viewMatrix = getCamera().getViewMatrix();
        if(fingerTrackers.size() == 1) {
            FingerTracker fingerTracker = fingerTrackers.get(0);
            Vec3d loc = cameraLocation.clone();
            loc.add(-fingerTracker.getDeltaX() * speed, fingerTracker.getDeltaY() * speed, 0);
            viewMatrix.setLocation(loc);
            Pools.recycle(loc);
        } else {
            double pinchDelta = event.getPinchDelta();
            double z = cameraLocation.z() - (pinchDelta * speed);
            if(z < minZ) {
                z = minZ;
            } else if(z > maxZ) {
                z = maxZ;
            }

            Vec3d loc = cameraLocation.clone();
            loc.z(z);
            viewMatrix.setLocation(loc);
            Pools.recycle(loc);
        }
    }

    @Override
    public void onFingerUp(UpTouchEvent event) {

    }

    @Override
    public void onEnterSubActivity() {
        TransformComponent playerTransform = GameEngineWorld.INSTANCE.getPlayer().getTransformComponent();
        Vec3d playerLoc = playerTransform.getLocation();
        paperPlane.getTransformComponent().set(playerTransform);
        Vec3d camLoc = Pools.getVec3d();
        camLoc.set(playerLoc.x(), playerLoc.y(), Math.max(minZ, playerLoc.z() + 10000));
        getCamera().getViewMatrix().setLocation(camLoc);
        Pools.recycle(camLoc);
    }

    @Override
    public void onLeaveSubActivity() {

    }

    @Override
    public void onActivityPause() {

    }

    @Override
    protected boolean shouldCreateRigidBodies() {
        return false;
    }

}