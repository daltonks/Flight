package com.github.daltonks.engine.states.touchevents;

import android.view.MotionEvent;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.interfaces.EngineRunnable;

import java.util.concurrent.ConcurrentLinkedQueue;

public class EngineQueue {
    private EngineState engineState;
    private FingerTracker[] fingerTrackers = new FingerTracker[20];
    private ConcurrentLinkedQueue<EngineRunnable> runnables = new ConcurrentLinkedQueue<>();

    public EngineQueue(EngineState engineState) {
        this.engineState = engineState;
    }

    public void update() {
        EngineRunnable runnable;
        while((runnable = runnables.poll()) != null) {
            runnable.run(engineState);
            Pools.recycle(runnable);
        }
    }

    public void onTouchEvent(MotionEvent e) {
        int pointerCount = e.getPointerCount();

        switch(e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                for(int i = 0; i < pointerCount; i++) {
                    int pointerID = e.getPointerId(i);
                    FingerTracker tracker = fingerTrackers[pointerID];
                    if(tracker == null) {
                        tracker = Pools.getFingerTracker();
                        fingerTrackers[pointerID] = tracker;
                    }
                    tracker.id = pointerID;
                    tracker.focusedUIEntity = null;
                    tracker.parent = null;
                    tracker.x = tracker.previousX = tracker.startingX = e.getX(i);
                    tracker.y = tracker.previousY = tracker.startingY = e.getY(i);

                    DownTouchEvent touchEvent = Pools.getDownTouchEvent();
                    touchEvent.setTracker(tracker.clone());
                    runnables.add(touchEvent);
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                MoveTouchEvent touchEvent = Pools.getMoveTouchEvent();
                for(int i = 0; i < pointerCount; i++) {
                    FingerTracker tracker = fingerTrackers[e.getPointerId(i)];
                    if(tracker == null)
                        continue;
                    tracker.x = e.getX(i);
                    tracker.y = e.getY(i);
                    touchEvent.addFingerTracker(tracker.clone());
                    tracker.previousX = tracker.x;
                    tracker.previousY = tracker.y;
                }
                if(!touchEvent.getFingerTrackers().isEmpty()) {
                    runnables.add(touchEvent);
                } else {
                    Pools.recycleMoveTouchEvent(touchEvent);
                }
                return;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP: {
                for(int i = 0; i < pointerCount; i++) {
                    FingerTracker tracker = fingerTrackers[e.getPointerId(i)];
                    if(tracker == null)
                        continue;
                    UpTouchEvent touchEvent = Pools.getUpTouchEvent();
                    touchEvent.setTracker(tracker.clone());
                    runnables.add(touchEvent);
                }
                break;
            }
        }
    }

    public void add(EngineRunnable runnable) {
        runnables.add(runnable);
    }

    public void recycleLists() {
        for(int i = 0; i < fingerTrackers.length; i++) {
            FingerTracker tracker = fingerTrackers[i];
            if(tracker != null) {
                Pools.recycle(tracker);
                fingerTrackers[i] = null;
            }
        }

        for(EngineRunnable runnable : runnables)
            Pools.recycle(runnable);
        runnables.clear();
    }
}