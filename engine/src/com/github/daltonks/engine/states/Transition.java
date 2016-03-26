package com.github.daltonks.engine.states;

import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.util.interfaces.Drawable;
import com.github.daltonks.engine.util.interfaces.Updatable;

public abstract class Transition implements Drawable, Updatable {
    private EngineState nextEngineState;
    protected boolean wentToNextSubActivity = false;
    private boolean toBeRemoved = false;

    public abstract void onStartTransition(EngineState engineState);

    public void setNextEngineState(EngineState engineState) {
        this.nextEngineState = engineState;
    }

    public void tryGoToNextSubActivity() {
        if(!wentToNextSubActivity) {
            Engine.INSTANCE.setCurrentSubActivity(nextEngineState);
            Engine.INSTANCE.setHaltUpdates(false);
            wentToNextSubActivity = true;
        }
    }

    public void remove() {
        toBeRemoved = true;
    }

    public boolean shouldBeRemoved() {
        return toBeRemoved;
    }
}