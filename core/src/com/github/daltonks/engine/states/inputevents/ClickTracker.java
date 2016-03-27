package com.github.daltonks.engine.states.inputevents;

import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.world.ui.UIEntity;

public class ClickTracker {
    public int id;
    public int button;
    public float startingX, startingY;
    public float previousX, previousY;
    public float x, y;
    public UIEntity focusedUIEntity;
    public ClickTracker parent;

    public float getDeltaX() {
        return x - previousX;
    }

    public float getDeltaY() {
        return y - previousY;
    }

    public ClickTracker clone() {
        ClickTracker newTracker = Pools.getFingerTracker();
        newTracker.id = id;
        newTracker.button = button;
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