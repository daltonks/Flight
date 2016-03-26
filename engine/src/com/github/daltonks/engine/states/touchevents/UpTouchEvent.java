package com.github.daltonks.engine.states.touchevents;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Util;
import com.github.daltonks.engine.world.ui.UIBoundsComponent;

public class UpTouchEvent extends SingleTouchEvent {
    @Override
    public void run(EngineState engineState) {
        if(tracker.parent.focusedUIEntity != null && tracker.parent.focusedUIEntity.getUIBoundsComponent() != null) {
            float openGLX = Util.toOpenGLX(tracker.x);
            float openGLY = Util.toOpenGLY(tracker.y);
            boolean captured = false;
            UIBoundsComponent bounds = tracker.parent.focusedUIEntity.getUIBoundsComponent();
            if(bounds.isInBounds(openGLX, openGLY)) {
                if(bounds.onCollidedUp(engineState, openGLX, openGLY)){
                    captured = true;
                }
            } else {
                if(bounds.onNonCollidedUp(engineState, openGLX, openGLY)){
                    captured = true;
                }
            }

            if(!captured) {
                engineState.onFingerUp(this);
            }
        }
    }
}