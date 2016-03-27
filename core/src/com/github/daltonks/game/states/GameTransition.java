package com.github.daltonks.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.states.Transition;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.camera.Camera;
import com.github.daltonks.engine.world.entityComponent.components.modelComponents.ModelComponent;
import com.github.daltonks.engine.world.entityComponent.components.transformComponents.TransformComponent;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;
import com.github.daltonks.engine.world.models.Models;

public class GameTransition extends Transition {
    private static final int NUM_OF_ROWS_AND_COLUMNS = 7;
    private static final int NUM_OF_TRIANGLES = NUM_OF_ROWS_AND_COLUMNS * NUM_OF_ROWS_AND_COLUMNS;
    private static final float MAX_SCALE = 5;
    private static final double AGE_OF_DEATH = .7;
    private static final Entity[] triangles = new Entity[NUM_OF_TRIANGLES];

    public static void init() {
        for(int i = 0; i < NUM_OF_TRIANGLES; i++) {
            Entity triangle = triangles[i] = new Entity(0);
            triangle.setTransformComponent(new TransformComponent(triangle, 0, 0, 0));
            triangle.setDrawableComponent(new ModelComponent(triangle, Models.get("spacedust")));
            float color = (i / 5) / 10f;
            triangle.getModelComponent().setColor(color, color, color);
            if(i % NUM_OF_ROWS_AND_COLUMNS % 2 == 0) {
                triangle.getTransformComponent().addRotation(0, 0, 180);
            }
            triangles[i] = triangle;
        }
    }

    public static void onSurfaceChanged(int width, int height) {
        Camera uiCamera = Engine.INSTANCE.getCurrentSubActivity().getUICamera();
        Vec3d worldLocBottomLeft = uiCamera.screenToWorldZPlaneNew(-1, -1);
        Vec3d worldLocTopRight =  uiCamera.screenToWorldZPlaneNew(1, 1);
        float xStep = (worldLocTopRight.xf() * 2) / NUM_OF_ROWS_AND_COLUMNS;
        float yStep = (worldLocTopRight.yf() - worldLocBottomLeft.yf()) / NUM_OF_ROWS_AND_COLUMNS;
        for(int i = 0; i < NUM_OF_TRIANGLES; i++) {
            Entity triangle = triangles[i];
            triangle.getTransformComponent().setLocation(
                    -worldLocTopRight.x() + (i % NUM_OF_ROWS_AND_COLUMNS + .5) * xStep,
                    worldLocBottomLeft.y() + (i / NUM_OF_ROWS_AND_COLUMNS + .5) * yStep,
                    0);
        }
        Pools.recycle(worldLocBottomLeft);
        Pools.recycle(worldLocTopRight);
    }

    private double age;

    @Override
    public void onStartTransition(EngineState engineState) {

    }

    @Override
    public void update(EngineState engineState, double delta) {
        age += delta;
        if(age >= AGE_OF_DEATH) {
            remove();
        } else if(age >= AGE_OF_DEATH / 2) {
            if(!wentToNextSubActivity) {
                age = AGE_OF_DEATH / 2;
            }
            for(int i = 0; i < triangles.length; i++) {
                triangles[i].getModelComponent().setScale((float) (1 - (age / (AGE_OF_DEATH / 2) - 1)) * MAX_SCALE);
            }
            tryGoToNextSubActivity();
        } else {
            for(int i = 0; i < triangles.length; i++) {
                triangles[i].getModelComponent().setScale((float) (age / (AGE_OF_DEATH / 2)) * MAX_SCALE);
            }
        }
    }

    @Override
    public void draw(Camera camera) {
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        for(int i = 0; i < triangles.length; i++) {
            triangles[i].getDrawableComponent().draw(camera);
        }
    }
}