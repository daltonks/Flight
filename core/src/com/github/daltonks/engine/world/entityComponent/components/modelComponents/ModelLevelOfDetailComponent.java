//Level of Detail implementation of ModelComponent
//Currently, 5 levels of detail are used for chosen models, but I plan on making that a variable number in the future

package com.github.daltonks.engine.world.entityComponent.components.modelComponents;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;
import com.github.daltonks.engine.world.models.Model;
import com.github.daltonks.engine.world.models.Models;

public class ModelLevelOfDetailComponent extends ModelComponent {
    private static final float LEVEL_OF_DETAIL_DISTANCE_STEP = 4f;
    public static boolean updateLevelOfDetailModelsOnNextTick = false;

    private Model[] models;

    public ModelLevelOfDetailComponent(Entity entity, Model startingModel, float scale) {
        super(entity, startingModel, scale);
        String baseName = startingModel.getName().substring(0, startingModel.getName().length() - 1);
        models = new Model[getModel().getModelInfo().levelOfDetail];
        for(int i = 0; i < models.length; i++) {
            models[i] = Models.get(baseName + (i + 1));
        }
    }

    public void update(EngineState engineState, double delta) {
        super.update(engineState, delta);
        if(updateLevelOfDetailModelsOnNextTick) {
            Vec3d camLoc = engineState.getEngineWorld().getCamera().getViewMatrix().getLocation();
            double distance = getEntity().getTransformComponent().getLocation().distanceTo(camLoc);
            float boundingBoxLength = getScale() * models[0].getModelInfo().boundingBoxDiagonalLength;
            //Model model = models[levelsOfDetail - 1];

            //for(byte b = 0; b < levelsOfDetail - 1; b++) {
            //    if(distance < LEVEL_OF_DETAIL_DISTANCE_STEP * (b + 1) * boundingBoxLength) {
            //        model = models[b];
            //    }
            //}

            Model model;
            if(distance < LEVEL_OF_DETAIL_DISTANCE_STEP * boundingBoxLength) {
                model = models[0];
            } else if(distance < LEVEL_OF_DETAIL_DISTANCE_STEP * 2 * boundingBoxLength) {
                model = models[1];
            } else if(distance < LEVEL_OF_DETAIL_DISTANCE_STEP * 3 * boundingBoxLength) {
                model = models[2];
            } else if(distance < LEVEL_OF_DETAIL_DISTANCE_STEP * 10 * boundingBoxLength) {
                model = models[3];
            } else {
                model = models[4];
            }
            this.setModel(model);
        }
    }
}