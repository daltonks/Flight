package com.github.daltonks.engine.states.inputevents;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Pools;

import java.util.concurrent.ConcurrentLinkedQueue;

public class InputQueue {
    private EngineState engineState;
    private ClickTracker[] clickTrackers = new ClickTracker[20];
    private ConcurrentLinkedQueue<InputRunnable> runnables = new ConcurrentLinkedQueue<InputRunnable>();

    public InputQueue(EngineState engineState) {
        this.engineState = engineState;
    }

    public void update() {
        InputRunnable runnable;
        while((runnable = runnables.poll()) != null) {
            runnable.run(engineState);
            Pools.recycle(runnable);
        }
    }

    public void add(InputRunnable runnable) {
        runnables.add(runnable);
    }

    public void addClickDown(int x, int y, int pointerID, int button) {
        ClickTracker tracker = getFingerTracker(pointerID);
        if(tracker == null) {
            tracker = Pools.getFingerTracker();
            setFingerTracker(pointerID, tracker);
        }
        tracker.id = pointerID;
        tracker.button = button;
        tracker.focusedUIEntity = null;
        tracker.parent = null;
        tracker.x = tracker.previousX = tracker.startingX = x;
        tracker.y = tracker.previousY = tracker.startingY = y;

        ClickDownEvent touchEvent = Pools.getDownTouchEvent();
        touchEvent.setTracker(tracker.clone());
        add(touchEvent);
    }

    public void addClickUp(int pointerID) {
        ClickTracker tracker = getFingerTracker(pointerID);
        if(tracker != null) {
            ClickUpEvent touchEvent = Pools.getUpTouchEvent();
            touchEvent.setTracker(tracker.clone());
            add(touchEvent);
        }
    }

    public void recycleLists() {
        for(int i = 0; i < clickTrackers.length; i++) {
            ClickTracker tracker = clickTrackers[i];
            if(tracker != null) {
                Pools.recycle(tracker);
                clickTrackers[i] = null;
            }
        }

        for(InputRunnable runnable : runnables)
            Pools.recycle(runnable);
        runnables.clear();
    }

    public void setFingerTracker(int index, ClickTracker tracker) {
        clickTrackers[index] = tracker;
    }

    public ClickTracker getFingerTracker(int index) {
        return clickTrackers[index];
    }
}