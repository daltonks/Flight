package com.github.daltonks.game.states;

import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.game.World.engineworlds.MenuEngineWorld;

public abstract class MenuEngineState extends ReturnToGameEngineState {

    @Override
    public void init() {
        MenuEngineWorld engineWorld = new MenuEngineWorld(this);
        setEngineWorld(engineWorld);
        engineWorld.construct();
    }

    @Override
    protected Vec3d getStartingUICameraOffset() {
        return new Vec3d(0, -5, 10);
    }
}