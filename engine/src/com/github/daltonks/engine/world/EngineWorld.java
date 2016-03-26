//Contains the entities that are always in cache, skybox entities, and the Bullet physics world
//further branches out the update and draw calls

package com.github.daltonks.engine.world;

import android.opengl.GLES20;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.datacompressor.converters.StaticBodyConverter;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.SortedList;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.util.interfaces.ActivityIsh;
import com.github.daltonks.engine.world.camera.Camera;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;
import com.github.daltonks.engine.world.entityComponent.entities.base.ModelEntity;
import com.github.daltonks.engine.world.models.Model;
import com.github.daltonks.engine.world.models.Models;
import com.github.daltonks.engine.world.physics.PhysicsWorld;

import java.util.ArrayList;
import java.util.LinkedList;

public abstract class EngineWorld implements ActivityIsh {
    private boolean initialized = false;
    private EngineState engineState;
    protected Camera camera;
    private PhysicsWorld physicsWorld;
    private StaticBodyAttributeHandler staticBodyAttributeHandler;
    private LinkedList<Runnable> runAfterPhysicsStep = new LinkedList<>();
    protected SortedList<Entity> staticEntities = new SortedList<>(100);
    private SortedList<Entity> entities = new SortedList<>(50);
    protected SortedList<Entity> skyboxEntities = new SortedList<>(1);

    public EngineWorld(EngineState engineState, float worldWidth, StaticBodyAttributeHandler staticBodyAttributeHandler) {
        this.engineState = engineState;
        camera = new Camera();
        physicsWorld = new PhysicsWorld(worldWidth);
        this.staticBodyAttributeHandler = staticBodyAttributeHandler;
    }

    protected abstract boolean shouldCreateRigidBodies();

    public void construct() {
        if(initialized) return;
        loadStaticBodies(engineState.getName());
        init();
        initialized = true;
    }

    protected void loadStaticBodies(String subActivityName) {
        StaticBodyConverter.StaticEntityInfo[] staticEntityInfos = WorldStaticEntityInfoImporter.getInfo(subActivityName);
        if(staticEntityInfos == null)
            return;
        for(StaticBodyConverter.StaticEntityInfo info : staticEntityInfos) {
            Entity entityToAdd;
            Model model = Models.get(info.model);

            entityToAdd = new ModelEntity(
                    getEngineState(),
                    info.x, info.y, info.z,
                    info.qx, info.qy, info.qz, info.qw,
                    info.scale, model,
                    info.personalCollisionMask, info.collidesWithMask,
                    false, null, true,
                    shouldCreateRigidBodies());

            if(info.attributes != null) {
                for(int i = 0; i < info.attributes.length; i += 2) {
                    String key = info.attributes[i];
                    String value = info.attributes[i + 1];
                    staticBodyAttributeHandler.handle(entityToAdd, info, key, value);
                }
            }
            staticEntities.addWithoutSorting(entityToAdd);
        }
        staticEntities.sort();
    }

    public void update(double delta) {
        /*
        ArrayList<Entity> staticList = staticEntities.getUnderlyingList();
        for(int i = 0; i < staticList.size(); i++) {
            staticList.get(i).update(engineState, delta);
        }*/

        stepSimulation();
        updateListsAndCamera(delta);
    }

    protected void stepSimulation() {
        physicsWorld.stepSimulation();
        while(!runAfterPhysicsStep.isEmpty()) {
            runAfterPhysicsStep.pop().run();
        }
    }

    protected void updateListsAndCamera(double delta) {
        ArrayList<Entity> entityList = entities.getUnderlyingList();
        for(int i = 0; i < entityList.size(); i++) {
            entityList.get(i).update(getEngineState(), delta);
        }

        camera.update(engineState, delta);
        ArrayList<Entity> skyboxList = skyboxEntities.getUnderlyingList();
        for(int i = 0; i < skyboxList.size(); i++) {
            skyboxList.get(i).getTransformComponent().setLocation(camera.getViewMatrix().getLocation());
        }
    }

    public void draw() {
        updateSunAndDrawSkybox();

        camera.useFarProjectionMatrix();
        drawFar();
        Gdx.gl.glClear(Gdx.gl.GL_DEPTH_BUFFER_BIT);

        camera.useNearProjectionMatrix();
        drawNear();
    }

    protected void updateSunAndDrawSkybox() {
        Vec3d sunShaderLocInView = camera.getViewMatrix().getLocation().clone().mult(-1).multMatrix(camera.getViewMatrix().getMatrix());
        Gdx.gl.glUniform3f(
                EngineGLScene.LIGHT_POSITION_IN_VIEW_3F_UNIFORM,
                (float) sunShaderLocInView.x(),
                (float) sunShaderLocInView.y(),
                (float) sunShaderLocInView.z()
        );
        Pools.recycle(sunShaderLocInView);

        camera.useNearProjectionMatrix();
        Gdx.gl.glDisable(Gdx.gl.GL_DEPTH_TEST);
        ArrayList<Entity> skyboxList = skyboxEntities.getUnderlyingList();
        for(int i = 0; i < skyboxList.size(); i++) {
            skyboxList.get(i).draw(camera);
        }
        Gdx.gl.glEnable(Gdx.gl.GL_DEPTH_TEST);
    }

    protected void drawFar() {
        drawLists();
    }

    protected void drawNear() {
        drawLists();
    }

    protected void drawLists() {
        ArrayList<Entity> staticList = staticEntities.getUnderlyingList();
        for(int i = 0; i < staticList.size(); i++) {
            staticList.get(i).draw(camera);
        }

        ArrayList<Entity> entityList = entities.getUnderlyingList();
        for(int i = 0; i < entityList.size(); i++) {
            entityList.get(i).draw(camera);
        }
    }

    public void onNewSurfaceDimensions(int width, int height) {}

    public PhysicsWorld getPhysicsWorld() {
        return physicsWorld;
    }

    public Entity addStaticEntity(Entity entity) {
        staticEntities.add(entity);
        return entity;
    }

    public Entity addEntity(Entity entity) {
        entities.add(entity);
        return entity;
    }

    public Entity addSkyboxEntity(Entity entity) {
        skyboxEntities.add(entity);
        return entity;
    }

    public void removeEntity(Entity entity) {
        if(!entities.remove(entity)) {
            if(!staticEntities.remove(entity)) {
                skyboxEntities.remove(entity);
            }
        }

        entity.onDestroy(engineState);
    }

    public void addAfterPhysicsStepRunnable(Runnable run) {
        this.runAfterPhysicsStep.add(run);
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public SortedList<Entity> getStaticEntities() {
        return staticEntities;
    }

    public Camera getCamera() {
        return camera;
    }

    public EngineState getEngineState() {
        return engineState;
    }
}