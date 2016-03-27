package com.github.daltonks.game.states;

import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.game.World.engineworlds.MapEngineWorld;

public class MapEngineState extends ReturnToGameEngineState {

    @Override
    public void beforeModelGeneration() {

    }

    @Override
    public void init() {
        MapEngineWorld engineWorld = new MapEngineWorld(this);
        setEngineWorld(engineWorld);
        engineWorld.construct();
    }

    @Override
    public boolean onBackPressed() {
        returnToGame();
        return true;
    }

    @Override
    protected Vec3d getStartingUICameraOffset() {
        return new Vec3d(0, 0, 5);
    }

}