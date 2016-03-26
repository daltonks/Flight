//Implementation of EngineWorld for the paper airplane game

package com.github.daltonks.game.World.engineworlds;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.world.EngineWorld;
import com.github.daltonks.engine.world.entityComponent.entities.base.ModelEntity;
import com.github.daltonks.engine.world.models.Models;
import com.github.daltonks.game.World.models.SpaceDust;

public abstract class SpaceEngineWorld extends EngineWorld {
    public static int WORLD_WIDTH = 1000000;

    private SpaceDust[] spaceDusts;//collected in one place for faster rendering

    public SpaceEngineWorld(EngineState engineState) {
        this(engineState, 150);
    }

    public SpaceEngineWorld(EngineState engineState, int numOfSpaceDust) {
        super(engineState, WORLD_WIDTH, new PaperStaticBodyAttributeHandler());
        spaceDusts = new SpaceDust[numOfSpaceDust];
    }

    public void init() {
        ModelEntity stars = new ModelEntity(getEngineState(), Models.get("stars"));
        stars.getModelComponent().setFrustumCulling(false);
        addSkyboxEntity(stars);

        for(int i = 0; i < spaceDusts.length; i++) {
            spaceDusts[i] = new SpaceDust(getEngineState());
        }
    }

    public void update(double delta) {
        super.update(delta);
        updateSpaceDust(delta);
    }

    private static int dustUpdateIndex = 0;
    private static double dustAccum = 0;
    private static final double timeToUpdateAllDust = 1;
    protected void updateSpaceDust(double delta) {
        dustAccum += delta;
        int numToUpdate = (int) (spaceDusts.length * dustAccum / timeToUpdateAllDust);
        dustAccum -= (double) numToUpdate / spaceDusts.length;
        for(int i = 0; i < numToUpdate; i++) {
            spaceDusts[dustUpdateIndex].update(getEngineState(), delta);
            dustUpdateIndex = (dustUpdateIndex + 1) % spaceDusts.length;
        }
    }

    protected void drawNear() {
        for(int i = 0; i < spaceDusts.length; i++) {
            spaceDusts[i].draw(camera);
        }

        super.drawNear();
    }
}