package com.github.daltonks.engine.states.inputevents;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Util;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.ui.UIBoundsComponent;
import com.github.daltonks.engine.world.ui.UIEntity;

import java.util.ArrayList;

public class ClickDownEvent extends SinglePointerEvent {
    @Override
    public void run(EngineState engineState) {
        float glX = Util.toOpenGLX(tracker.x);
        float glY = Util.toOpenGLY(tracker.y);
        Vec3d worldLoc = engineState.getUICamera().screenToWorldZPlaneNew(glX, glY);
        ArrayList<UIEntity> underlying = engineState.getUIEntities().getUnderlyingList();
        boolean captured = false;
        for(int i = 0; i < underlying.size(); i++) {
            UIEntity entity = underlying.get(i);
            UIBoundsComponent uiBounds = entity.getUIBoundsComponent();
            if(uiBounds != null && uiBounds.isInBounds(worldLoc)) {
                if(uiBounds.onCollidedDown(engineState, glX, glY)) {
                    tracker.parent.focusedUIEntity = entity;
                    captured = true;
                    break;
                }
            }
        }
        if(!captured) {
            engineState.onClickDown(this);
        }
        Pools.recycle(worldLoc);
    }
}