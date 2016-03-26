package com.github.daltonks.engine.states.touchevents;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.EngineMath;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Util;
import com.github.daltonks.engine.util.interfaces.DeepRecycling;
import com.github.daltonks.engine.util.interfaces.EngineRunnable;
import com.github.daltonks.engine.world.ui.UIBoundsComponent;

import java.util.LinkedList;

public class MoveTouchEvent implements EngineRunnable, DeepRecycling {
    private LinkedList<FingerTracker> fingerTrackers = new LinkedList<>();

    @Override
    public void run(EngineState engineState) {
        for(int i = 0; i < fingerTrackers.size(); i++) {
            FingerTracker tracker = fingerTrackers.get(i);
            if(tracker.parent.focusedUIEntity != null) {
                UIBoundsComponent bounds = tracker.parent.focusedUIEntity.getUIBoundsComponent();
                if(bounds == null) {
                    continue;
                }
                float openGLX = Util.toOpenGLX(tracker.x);
                float openGLY = Util.toOpenGLY(tracker.y);
                boolean captured = false;
                if(bounds.isInBounds(openGLX, openGLY)) {
                    if(tracker.parent.focusedUIEntity.getUIBoundsComponent().onCollidedSwipe(
                            engineState, tracker.x - tracker.previousX, tracker.y - tracker.previousY,
                            openGLX, openGLY)) {

                        captured = true;
                    }
                } else {
                    if(tracker.parent.focusedUIEntity.getUIBoundsComponent().onNonCollidedSwipe(
                            engineState, tracker.x - tracker.previousX, tracker.y - tracker.previousY,
                            openGLX, openGLY)) {

                        captured = true;
                    }
                }
                if(captured) {
                    Pools.recycle(fingerTrackers.remove(i));
                    i--;
                }
            }
        }
        if(!fingerTrackers.isEmpty()) {
            engineState.onFingersMove(this);
        }
    }

    public FingerTracker getFurthestLeft() {
        FingerTracker furthest = fingerTrackers.get(0);
        for(int i = 1; i < fingerTrackers.size(); i++) {
            FingerTracker tracker = fingerTrackers.get(i);
            if(tracker.x < furthest.x) {
                furthest = tracker;
            }
        }
        return furthest;
    }

    public FingerTracker getFurthestRight() {
        FingerTracker furthest = fingerTrackers.get(0);
        for(int i = 1; i < fingerTrackers.size(); i++) {
            FingerTracker tracker = fingerTrackers.get(i);
            if(tracker.x > furthest.x) {
                furthest = tracker;
            }
        }
        return furthest;
    }

    public double getPinchDelta() {
        double centerX = 0;
        double centerY = 0;
        for(int i = 0; i < fingerTrackers.size(); i++) {
            FingerTracker tracker = fingerTrackers.get(i);
            centerX += tracker.previousX;
            centerY += tracker.previousY;
        }
        centerX /= fingerTrackers.size();
        centerY /= fingerTrackers.size();

        double delta = 0;
        for(int i = 0; i < fingerTrackers.size(); i++) {
            FingerTracker tracker = fingerTrackers.get(i);
            double previousDistance = EngineMath.distance(tracker.previousX, tracker.previousY, centerX, centerY);
            double currentDistance = EngineMath.distance(tracker.x, tracker.y, centerX, centerY);
            delta += currentDistance - previousDistance;
        }
        return delta;
    }

    public LinkedList<FingerTracker> getFingerTrackers() {
        return fingerTrackers;
    }

    protected void addFingerTracker(FingerTracker tracker) {
        fingerTrackers.add(tracker);
    }

    @Override
    public void beforeRecycle() {
        while(!fingerTrackers.isEmpty()) {
            Pools.recycle(fingerTrackers.pop());
        }
    }
}