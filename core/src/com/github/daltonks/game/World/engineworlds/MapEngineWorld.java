package com.github.daltonks.game.World.engineworlds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Color;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.camera.FocusInDirectionCameraMode;
import com.github.daltonks.engine.world.entityComponent.components.transformComponents.TransformComponent;
import com.github.daltonks.engine.world.entityComponent.entities.base.ModelEntity;
import com.github.daltonks.engine.world.models.Models;
import com.github.daltonks.game.states.CursorDrawing;

public class MapEngineWorld extends SpaceEngineWorld {

    public static double MIN_CAMERA_Z = 1, MAX_CAMERA_Z = 1000000;

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
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        CursorDrawing.drawCursor(getEngineState(), paperPlane, Color.PLAYER_BLUE, getCamera(), 3, 1, true);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
    }

    protected void drawFar() {
        super.drawNear();
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        CursorDrawing.drawCursor(getEngineState(), paperPlane, Color.PLAYER_BLUE, getCamera(), 3, 1, true);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
    }

    @Override
    public void onEnterState() {
        TransformComponent playerTransform = GameEngineWorld.INSTANCE.getPlayer().getTransformComponent();
        Vec3d playerLoc = playerTransform.getLocation();
        paperPlane.getTransformComponent().set(playerTransform);
        Vec3d camLoc = Pools.getVec3d();
        camLoc.set(playerLoc.x(), playerLoc.y(), Math.max(MIN_CAMERA_Z, playerLoc.z() + 10000));
        getCamera().getViewMatrix().setLocation(camLoc);
        Pools.recycle(camLoc);
    }

    @Override
    public void onLeaveState() {

    }

    @Override
    public void onPause() {

    }

    @Override
    protected boolean shouldCreateRigidBodies() {
        return false;
    }

}