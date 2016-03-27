//Bridge between Android, OpenGL, and Bullet physics
//This is the only Activity in the application
//Usually not extended because EngineState should be

package com.github.daltonks.engine;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.states.Transition;
import com.github.daltonks.engine.states.inputevents.ComputerInputProcessor;
import com.github.daltonks.engine.util.EngineSerializer;
import com.github.daltonks.engine.util.Util;
import com.github.daltonks.engine.world.entityComponent.components.modelComponents.ModelLevelOfDetailComponent;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Engine implements ApplicationListener {
    public static int PHYSICS_WORLD_TO_ENGINE_SCALE = 100;
    public static float ENGINE_TO_PHYSICS_WORLD_SCALE = 1.f / 100.f;
    private static final float CHECK_LEVEL_OF_DETAIL_SECONDS = 1f;

    public static Engine INSTANCE;
    private static DataInputStream dataPileInputStream;
    private static EngineState currentEngineState;
    private static HashMap<String, EngineState> subActivities = new HashMap<String, EngineState>();
    private static ArrayList<EngineState> orderedSubActivities = new ArrayList<EngineState>();

    private boolean isRunning = true;
    private AtomicBoolean haltUpdates = new AtomicBoolean(false);
    private Transition transition;

    public static long creationTime;
    public Engine() {
        creationTime = System.currentTimeMillis();
        INSTANCE = this;
    }

    @Override
    public void create() {
        if(Gdx.app.getType() != Application.ApplicationType.Android) {
            Gdx.input.setInputProcessor(new ComputerInputProcessor());
        }
        try {
            FileHandle handle = Gdx.files.getFileHandle("android/assets/data/data.pile", Files.FileType.Internal);
            dataPileInputStream = new DataInputStream(handle.read(16384));

            //Import SubActivities
            String[] subActivityData = EngineSerializer.getSerializer().read(dataPileInputStream, String[].class);
            for(short i = 0; i < subActivityData.length; i+=2) {
                String subActivityName = subActivityData[i];
                String subActivityClassName = subActivityData[i + 1];
                Class<? extends EngineState> subActivityClass = (Class<? extends EngineState>) Class.forName(subActivityClassName);
                EngineState engineState = subActivityClass.newInstance();
                engineState.setID((short) (i / 2));
                engineState.setName(subActivityName);
                addSubActivity(subActivityName, engineState);
                if(currentEngineState == null) {
                    currentEngineState = subActivities.get(subActivityName);
                }
            }

            EngineShaderProgram.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void beforeModelGeneration() {
        Util.startTimer();
        for(EngineState sub : subActivities.values()) {
            sub.beforeModelGeneration();
        }
        System.out.println("SubActivities' beforeModelGeneration(): " + Util.endTimer());
    }

    public void onSurfaceCreated() {
        Util.startTimer();

        for(EngineState sub : subActivities.values()) {
            sub.init();
        }
        currentEngineState.onEnterState();

        try {
            dataPileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //SoundHandler.init(this);
        System.out.println("Engine init(): " + Util.endTimer());
    }

    static double levelOfDetailAccum = 0;
    public void update(double delta) {
        if(transition != null) {
            transition.update(currentEngineState, delta);
            if(transition.shouldBeRemoved()) {
                transition = null;
            }
        }
        if(!haltUpdates.get()) {
            levelOfDetailAccum += delta;
            if(levelOfDetailAccum >= CHECK_LEVEL_OF_DETAIL_SECONDS) {
                levelOfDetailAccum -= CHECK_LEVEL_OF_DETAIL_SECONDS;
                ModelLevelOfDetailComponent.updateLevelOfDetailModelsOnNextTick = true;
            }
            currentEngineState.update(delta);
            ModelLevelOfDetailComponent.updateLevelOfDetailModelsOnNextTick = false;
        }
    }

    public void draw() {
        currentEngineState.draw();

        if(transition != null) {
            transition.draw(currentEngineState.getUICamera());
        }
    }

    public void onTouchEvent() {
        if(!isHaltingUpdates()) {

        }
    }

    public void addSubActivity(String name, EngineState activity) {
        subActivities.put(name, activity);
        orderedSubActivities.add(activity);
    }

    public void setCurrentSubActivity(String name) {
        setCurrentSubActivity(subActivities.get(name));
    }

    public void setCurrentSubActivity(EngineState engineState) {
        if(currentEngineState != null) {
            currentEngineState.onLeaveState();
        }
        currentEngineState = engineState;
        currentEngineState.onEnterState();
    }

    public void setCurrentSubActivity(String nextState, Transition transition) {
        setCurrentSubActivity(subActivities.get(nextState), transition);
    }

    public void setCurrentSubActivity(EngineState engineState, Transition transition) {
        this.transition = transition;
        transition.setNextEngineState(engineState);
        setHaltUpdates(true);
        transition.onStartTransition(currentEngineState);
    }

    public EngineState getCurrentSubActivity() {
        return currentEngineState;
    }

    public EngineState getSubActivity(String name) {
        return subActivities.get(name);
    }

    public EngineState getSubActivity(short id) {
        return orderedSubActivities.get(id);
    }

    public void setHaltUpdates(boolean haltUpdates) {
        this.haltUpdates.set(haltUpdates);
        if(haltUpdates) {
            getCurrentSubActivity().getInputQueue().recycleLists();
        }
    }

    public DataInputStream getDataPileInputStream() {
        return dataPileInputStream;
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void resize(int width, int height) {
        Util.startTimer();
        Gdx.gl.glViewport(0, 0, width, height);
        for(EngineState engineState : subActivities.values()) {
            engineState.onNewSurfaceDimensions(width, height);
        }
        System.out.println("Engine onSurfaceChanged(): " + Util.endTimer());
    }

    private static long lastTime = 0;
    @Override
    public void render() {
        if(!isRunning())
            return;

        long time = System.nanoTime();
        if(lastTime == 0) lastTime = time;
        double delta = (time - lastTime) / 1000000000.0;
        if(delta > .2) delta = .2;
        lastTime = time;

        update(delta);

        // Redraw background color
        Gdx.gl.glClearDepthf(1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        draw();
    }

    @Override
    public void pause() {
        isRunning = false;
        for(EngineState engineState : subActivities.values()) {
            engineState.onPause();
        }
    }

    @Override
    public void resume() {
        isRunning = true;
    }

    @Override
    public void dispose() {

    }

    public boolean isHaltingUpdates() {
        return haltUpdates.get();
    }
}