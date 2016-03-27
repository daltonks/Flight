package com.github.daltonks.engine.util.interfaces;

public interface StateListener {
    void init();
    void onEnterState();
    void onLeaveState();
    void onPause();
}