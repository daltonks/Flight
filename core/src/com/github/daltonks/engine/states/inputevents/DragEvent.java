package com.github.daltonks.engine.states.inputevents;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.EngineMath;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Util;
import com.github.daltonks.engine.util.interfaces.DeepRecycling;
import com.github.daltonks.engine.world.ui.UIBoundsComponent;

import java.util.LinkedList;

public class DragEvent implements InputRunnable, DeepRecycling {
    private LinkedList<ClickTracker> clickTrackers = new LinkedList<ClickTracker>();

    @Override
    public void run(EngineState engineState) {
        DragEvent capturedEvent = null;
        for(int i = 0; i < clickTrackers.size(); i++) {
            ClickTracker tracker = clickTrackers.get(i);
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
                    if(capturedEvent == null)
                        capturedEvent = Pools.getDragEvent();
                    capturedEvent.clickTrackers.add(clickTrackers.remove(i));
                    i--;
                }
            }
        }
        if(!clickTrackers.isEmpty()) {
            engineState.getInputHandler().onDrag(this);
        }
        if(capturedEvent != null) {
            engineState.getInputHandler().onUICapturedDrag(capturedEvent);
        }
        Pools.recycle(capturedEvent);
    }

    public ClickTracker getFurthestLeft() {
        ClickTracker furthest = clickTrackers.get(0);
        for(int i = 1; i < clickTrackers.size(); i++) {
            ClickTracker tracker = clickTrackers.get(i);
            if(tracker.x < furthest.x) {
                furthest = tracker;
            }
        }
        return furthest;
    }

    public ClickTracker getFurthestRight() {
        ClickTracker furthest = clickTrackers.get(0);
        for(int i = 1; i < clickTrackers.size(); i++) {
            ClickTracker tracker = clickTrackers.get(i);
            if(tracker.x > furthest.x) {
                furthest = tracker;
            }
        }
        return furthest;
    }

    public double getPinchDelta() {
        double centerX = 0;
        double centerY = 0;
        for(int i = 0; i < clickTrackers.size(); i++) {
            ClickTracker tracker = clickTrackers.get(i);
            centerX += tracker.previousX;
            centerY += tracker.previousY;
        }
        centerX /= clickTrackers.size();
        centerY /= clickTrackers.size();

        double delta = 0;
        for(int i = 0; i < clickTrackers.size(); i++) {
            ClickTracker tracker = clickTrackers.get(i);
            double previousDistance = EngineMath.distance(tracker.previousX, tracker.previousY, centerX, centerY);
            double currentDistance = EngineMath.distance(tracker.x, tracker.y, centerX, centerY);
            delta += currentDistance - previousDistance;
        }
        return delta;
    }

    public LinkedList<ClickTracker> getClickTrackers() {
        return clickTrackers;
    }

    public void addFingerTracker(ClickTracker tracker) {
        clickTrackers.add(tracker);
    }

    @Override
    public void beforeRecycle() {
        while(!clickTrackers.isEmpty()) {
            Pools.recycle(clickTrackers.pop());
        }
    }
}