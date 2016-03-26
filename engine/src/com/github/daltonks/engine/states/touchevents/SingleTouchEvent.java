package com.github.daltonks.engine.states.touchevents;

import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.interfaces.DeepRecycling;
import com.github.daltonks.engine.util.interfaces.EngineRunnable;

public abstract class SingleTouchEvent implements EngineRunnable, DeepRecycling {
    protected FingerTracker tracker;

    public void setTracker(FingerTracker tracker) {
        this.tracker = tracker;
    }

    public FingerTracker getTracker() {
        return tracker;
    }

    @Override
    public void beforeRecycle() {
        Pools.recycle(tracker);
    }
}