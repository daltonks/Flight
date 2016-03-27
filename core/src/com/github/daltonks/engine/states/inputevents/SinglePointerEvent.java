package com.github.daltonks.engine.states.inputevents;

import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.interfaces.DeepRecycling;

public abstract class SinglePointerEvent implements InputRunnable, DeepRecycling {
    protected ClickTracker tracker;

    public void setTracker(ClickTracker tracker) {
        this.tracker = tracker;
    }

    public ClickTracker getTracker() {
        return tracker;
    }

    @Override
    public void beforeRecycle() {
        Pools.recycle(tracker);
    }
}