package com.github.daltonks.game.subactivities;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Color;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.camera.Camera;
import com.github.daltonks.engine.world.entityComponent.components.modelComponents.ModelComponent;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;
import com.github.daltonks.engine.world.entityComponent.entities.base.ModelEntity;
import com.github.daltonks.engine.world.models.Models;

public class CursorDrawing {
    private static float cursorCircleRadius;
    private static ModelEntity pointedCursor;
    private static ModelEntity dot;

    public static void init() {
        pointedCursor = new ModelEntity(null, Models.get("pointedcursor"));
        dot = new ModelEntity(null, Models.get("fourtriangleprimitive"));
    }

    public static void drawCursor(EngineState engineState, Entity entity, Color color, Camera camera, float dotScale, float arrowScale, boolean drawDot) {
        Vec3d posInView = entity.getTransformComponent().getLocation().clone();
        posInView.sub(camera.getViewMatrix().getLocation());
        posInView.multMatrix(camera.getViewMatrix().getMatrix());

        double distanceToCamera = posInView.length();
        Vec3d posInScreen = posInView.clone().multMatrix(camera.getProjectionMatrix().getMatrix());
        posInScreen.normalize();

        if(posInScreen.z() == 0)
            posInScreen.z(.0001);

        posInScreen.mult(1 / posInScreen.z());
        if(
                posInScreen.x() >= -1 && posInScreen.x() <= 1
                        && posInScreen.y() >= -1 && posInScreen.y() <= 1
                        && posInView.z() < 0
                ) {

            if(drawDot) {
                //close to middle of screen, draw dot instead of cursor
                ModelComponent dotMCcomp = dot.getModelComponent();
                dotMCcomp.setColor(color);
                Vec3d cameraUp = engineState.getEngineWorld().getCamera().getViewMatrix().getUp().clone();
                dotScale *= (float) (.008 * distanceToCamera);
                dotMCcomp.setScale(dotScale);
                cameraUp.mult(
                        entity.getModelComponent().getRadius() + dotMCcomp.getRadius()
                );
                cameraUp.add(entity.getTransformComponent().getLocation());

                dot.getTransformComponent().setLocation(cameraUp);

                dot.draw(camera);

                Pools.recycle(cameraUp);
            }
        } else {
            pointedCursor.getModelComponent().setColor(color);
            posInView.z(0);
            posInView.normalize();
            pointedCursor.getTransformComponent().setLocation(posInView.x() * cursorCircleRadius, posInView.y() * cursorCircleRadius, 0);
            pointedCursor.getTransformComponent().setRotation(0, 0, -(float) Math.toDegrees(Math.atan2(posInView.y(), posInView.x())) + 90);
            float minScale = .12f;
            float maxScale = .4f;
            float distMult = .00036f;
            arrowScale *= (float) Math.max(minScale, maxScale - distanceToCamera * distMult);
            pointedCursor.getModelComponent().setScale(arrowScale);
            Vec3d zero = Pools.getVec3d().set(0, 0, 0);
            pointedCursor.draw(engineState.getUICamera());
            Pools.recycle(zero);
        }
        Pools.recycle(posInView);
        Pools.recycle(posInScreen);
    }

    public static void updateCircleRadius(EngineState engineState) {
        Vec3d cursorCircleWorld = engineState.getUICamera().screenToWorldZPlaneNew(0, .75f);
        cursorCircleRadius = cursorCircleWorld.yf();
        Pools.recycle(cursorCircleWorld);
    }
}