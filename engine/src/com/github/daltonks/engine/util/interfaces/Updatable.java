package com.github.daltonks.engine.util.interfaces;

import com.github.daltonks.engine.states.EngineState;

public interface Updatable {
    void update(EngineState engineState, double delta);
}