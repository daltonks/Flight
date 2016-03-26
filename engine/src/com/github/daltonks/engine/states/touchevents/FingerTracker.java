package com.github.daltonks.engine.states.touchevents;

import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.world.ui.UIEntity;

public class FingerTracker {
    public int id;
    public float startingX, startingY;
    public float previousX, previousY;
    public float x, y;
    public UIEntity focusedUIEntity;
    protected FingerTracker parent;

    public float getDeltaX() {
        return x - previousX;
    }

    public float getDeltaY() {
        return y - previousY;
    }

    protected FingerTracker clone() {
        FingerTracker newTracker = Pools.getFingerTracker();
        newTracker.id = id;
        newTracker.x = x;
        newTracker.y = y;
        newTracker.previousX = previousX;
        newTracker.previousY = previousY;
        newTracker.startingX = startingX;
        newTracker.startingY = startingY;
        newTracker.parent = this;
        return newTracker;
    }
}