package com.github.daltonks.engine.util.interfaces;

import com.github.daltonks.engine.states.touchevents.DownTouchEvent;
import com.github.daltonks.engine.states.touchevents.MoveTouchEvent;
import com.github.daltonks.engine.states.touchevents.UpTouchEvent;

public interface ActivityIsh {
    void init();
    void onFingerDown(DownTouchEvent event);
    void onFingersMove(MoveTouchEvent event);
    void onFingerUp(UpTouchEvent event);
    void onEnterSubActivity();
    void onLeaveSubActivity();
    void onActivityPause();
}