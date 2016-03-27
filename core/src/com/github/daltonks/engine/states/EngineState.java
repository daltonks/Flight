package com.github.daltonks.engine.states;

import com.github.daltonks.engine.EngineShaderProgram;
import com.github.daltonks.engine.states.inputevents.*;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.SortedList;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.util.interfaces.StateListener;
import com.github.daltonks.engine.world.EngineWorld;
import com.github.daltonks.engine.world.camera.Camera;
import com.github.daltonks.engine.world.events.EventSystemContainer;
import com.github.daltonks.engine.world.ui.UIEntity;

import java.util.ArrayList;

public abstract class EngineState implements StateListener, EngineInputProcessor {
    private short id;
    private int currentEntityID = 0;
    private String name;
    private Camera uiCamera;
    private EventSystemContainer eventSystemContainer = new EventSystemContainer();
    private InputQueue inputQueue;
    private EngineWorld engineWorld;
    private SortedList<UIEntity> uiEntities = new SortedList<UIEntity>();
    private ArrayList<Integer> deletedEntityIDs = new ArrayList<Integer>();

    public EngineState() {
        uiCamera = new Camera();
        Vec3d loc = Pools.getVec3d();
        loc.set(0, 1, 0);
        uiCamera.getViewMatrix().setUp(loc);
        Pools.recycle(loc);
        uiCamera.getViewMatrix().setLocation(getStartingUICameraOffset());
        inputQueue = new InputQueue(this);
    }

    public abstract void beforeModelGeneration();
    protected abstract Vec3d getStartingUICameraOffset();

    public void update(double delta) {
        inputQueue.update();

        ArrayList<UIEntity> underlying = uiEntities.getUnderlyingList();
        for (int i = 0; i < underlying.size(); i++) {
            underlying.get(i).update(this, delta);
        }

        engineWorld.update(delta);
        uiCamera.update(this, delta);
    }

    public void draw() {
        engineWorld.draw();

        EngineShaderProgram.setLightPositionInView(0, 0, 4000);

        getUICamera().useFarProjectionMatrix();
        drawFar();
        getUICamera().useNearProjectionMatrix();
        drawNear();
    }

    protected void drawNear() {
        ArrayList<UIEntity> underlying = uiEntities.getUnderlyingList();
        for (int i = 0; i < underlying.size(); i++) {
            underlying.get(i).draw(uiCamera);
        }
    }

    protected void drawFar() {

    }

    public abstract boolean onBackPressed();

    public void onNewSurfaceDimensions(int width, int height) {
        ArrayList<UIEntity> underlying = uiEntities.getUnderlyingList();
        for (UIEntity uiEntity : underlying) {
            uiEntity.onSurfaceChanged(this, width, height);
        }
        if (engineWorld != null) {
            engineWorld.onNewSurfaceDimensions(width, height);
        }
    }

    public void onEnterState() {
        inputQueue.recycleLists();
        engineWorld.onEnterState();
    }

    public void onLeaveState() {
        engineWorld.onLeaveState();
    }

    public void onPause() {
        if(engineWorld != null) {
            engineWorld.onPause();
        }
    }

    public void addUIEntity(UIEntity ent) {
        uiEntities.add(ent);
    }

    public void removeUIEntity(UIEntity ent) {
        uiEntities.remove(ent);
        recycleEntityID(ent.getID());
    }

    public int generateEntityID() {
        int id;
        if (deletedEntityIDs.isEmpty()) {
            id = currentEntityID;
            currentEntityID++;
        } else {
            id = deletedEntityIDs.remove(deletedEntityIDs.size() - 1);
        }
        return id;
    }

    //Cross-platform
    public void onClickDown(ClickDownEvent event) {
        engineWorld.onClickDown(event);
    }
    public void onDrag(DragEvent event) {
        engineWorld.onDrag(event);
    }
    public void onClickUp(ClickUpEvent event) {
        engineWorld.onClickUp(event);
    }

    //Only computer
    public void onKeyDown(int keycode) {
        engineWorld.onKeyDown(keycode);
    }
    public void onKeyUp(int keycode) {
        engineWorld.onKeyUp(keycode);
    }
    public void onKeyTyped(char character) {
        engineWorld.onKeyTyped(character);
    }
    public void onScrolled(int amount) {
        engineWorld.onScrolled(amount);
    }

    public void recycleEntityID(int id) {
        deletedEntityIDs.add(id);
    }

    public void setEngineWorld(EngineWorld engineWorld) {
        this.engineWorld = engineWorld;
    }

    public EngineWorld getEngineWorld() {
        return engineWorld;
    }

    public void setID(short id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public short getID() {
        return id;
    }

    public Camera getUICamera() {
        return uiCamera;
    }

    public EventSystemContainer getEventSystemContainer() {
        return eventSystemContainer;
    }

    public SortedList<UIEntity> getUIEntities() {
        return uiEntities;
    }

    public InputQueue getInputQueue() {
        return inputQueue;
    }
}