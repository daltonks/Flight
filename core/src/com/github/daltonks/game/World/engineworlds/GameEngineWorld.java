package com.github.daltonks.game.World.engineworlds;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.states.inputevents.ClickDownEvent;
import com.github.daltonks.engine.states.inputevents.ClickTracker;
import com.github.daltonks.engine.states.inputevents.DragEvent;
import com.github.daltonks.engine.states.inputevents.ClickUpEvent;
import com.github.daltonks.engine.util.*;
import com.github.daltonks.engine.world.camera.SwingingFollowCameraMode;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;
import com.github.daltonks.engine.world.models.Models;
import com.github.daltonks.game.World.items.base.Inventory;
import com.github.daltonks.game.World.items.base.Items;
import com.github.daltonks.game.World.livingentities.AIEntity;
import com.github.daltonks.game.World.livingentities.LivingEntity;
import com.github.daltonks.game.World.livingentities.Player;
import com.github.daltonks.game.World.physics.CollisionMasks;

import java.util.ArrayList;

public class GameEngineWorld extends SpaceEngineWorld {
    public static GameEngineWorld INSTANCE;

    private Entity planetoidTraveledToLast;
    private Player player;
    private SwingingFollowCameraMode cameraMode;
    private SortedList<LivingEntity> livingEntities = new SortedList<LivingEntity>(100);

    public GameEngineWorld(EngineState engineState) {
        super(engineState);
        INSTANCE = this;
    }

    public void initNewGame() {
        Player player = initPlayer(-167125, 279838 + 4000, 44644, 0, 0, 1, 0);
        player.setInventory(new Inventory(player, 10));
        Items.BASIC_BULLET_SHOOTER.addToInventory(player, 1);
    }

    double enemyAccum = 0;
    public void update(double delta) {
        stepSimulation();

        ArrayList<LivingEntity> livingList = livingEntities.getUnderlyingList();
        for(int i = 0; i < livingList.size(); i++) {
            livingList.get(i).update(getEngineState(), delta);
        }

        updateSpaceDust(delta);
        updateListsAndCamera(delta);

        enemyAccum += delta;
        if(enemyAccum >= 15) {
            Vec3d loc = EngineMath.getRandomPointOnSphereSurfaceNew(3500);
            loc.add(player.getTransformComponent().getLocation());
            AIEntity enemy = new AIEntity(
                    getEngineState(), loc.x(), loc.y(), loc.z(),
                    0, 1, 0, 0, Models.get("plane"), CollisionMasks.ENEMY, null);

            enemy.setInventory(new Inventory(enemy, 1));
            addEntity(enemy);
            Pools.recycle(loc);
            enemyAccum = 0;
        }
    }

    public void draw() {
        updateSunAndDrawSkybox();

        //Enable stencil for living entity outlines
        Gdx.gl.glEnable(GL20.GL_STENCIL_TEST);
        Gdx.gl.glColorMask(false, false, false, false);
        Gdx.gl.glDepthMask(false);
        Gdx.gl.glStencilFunc(GL20.GL_NEVER, 1, 0xFF);
        Gdx.gl.glStencilOp(GL20.GL_REPLACE, GL20.GL_KEEP, GL20.GL_KEEP);
        Gdx.gl.glStencilMask(0xFF);
        Gdx.gl.glClear(GL20.GL_STENCIL_BUFFER_BIT);

        ArrayList<LivingEntity> livingList = livingEntities.getUnderlyingList();

        //Create stencil
        camera.useFarProjectionMatrix();
        for(int i = 0; i < livingList.size(); i++) {
            livingList.get(i).draw(camera);
        }

        camera.useNearProjectionMatrix();
        for(int i = 0; i < livingList.size(); i++) {
            livingList.get(i).draw(camera);
        }

        Gdx.gl.glColorMask(true, true, true, true);
        Gdx.gl.glDepthMask(true);

        //Draw outline
        camera.useFarProjectionMatrix();
        Gdx.gl.glStencilMask(0x00);
        Gdx.gl.glStencilFunc(GL20.GL_EQUAL, 0, 0xFF);
        for(int i = 0; i < livingList.size(); i++) {
            livingList.get(i).drawOutline(camera);
        }
        Gdx.gl.glDisable(GL20.GL_STENCIL_TEST);
        drawFar();

        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

        camera.useNearProjectionMatrix();
        Gdx.gl.glEnable(GL20.GL_STENCIL_TEST);
        for(int i = 0; i < livingList.size(); i++) {
            livingList.get(i).drawOutline(camera);
        }
        Gdx.gl.glDisable(GL20.GL_STENCIL_TEST);
        drawNear();
    }

    protected void drawFar() {
        ArrayList<LivingEntity> livingList = livingEntities.getUnderlyingList();
        for(int i = 0; i < livingList.size(); i++) {
            livingList.get(i).draw(camera);
        }
        super.drawFar();
    }

    protected void drawNear() {
        ArrayList<LivingEntity> livingList = livingEntities.getUnderlyingList();
        for(int i = 0; i < livingList.size(); i++) {
            livingList.get(i).draw(camera);
        }
        super.drawNear();
    }

    public Player initPlayer(double x, double y, double z, float qx, float qy, float qz, float qw) {
        player = new Player(
                getEngineState(),
                x, y, z,
                qx, qy, qz, qw);
        addEntity(player);

        cameraMode = new SwingingFollowCameraMode(
                getCamera(),
                player.getTransformComponent(),
                new Vec3d(0, -30, 5), new Vec3d(0, 0, 4),
                .4, .4);
        getCamera().setCameraMode(cameraMode);

        return player;
    }

    public Entity addEntity(Entity entity) {
        if(entity instanceof LivingEntity) {
            livingEntities.add((LivingEntity) entity);
        } else {
            super.addEntity(entity);
        }
        return entity;
    }

    public void removeEntity(Entity entity) {
        if(entity instanceof LivingEntity) {
            livingEntities.remove((LivingEntity) entity);
            entity.onDestroy(getEngineState());
        } else {
            super.removeEntity(entity);
        }
    }

    @Override
    public void onEnterState() {

    }
    @Override
    public void onLeaveState() {

    }

    @Override
    public void onPause() {

    }

    public void setPlanetoidTraveledToLast(Entity planetoid) {
        this.planetoidTraveledToLast = planetoid;
    }

    public Entity getPlanetoidTraveledToLast() {
        return planetoidTraveledToLast;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    protected boolean shouldCreateRigidBodies() {
        return true;
    }

    public SortedList<LivingEntity> getLivingEntities() {
        return livingEntities;
    }
}