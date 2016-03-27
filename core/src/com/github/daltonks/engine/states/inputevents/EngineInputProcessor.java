package com.github.daltonks.engine.states.inputevents;

public interface EngineInputProcessor {
    //Cross-platform
    void onClickDown(ClickDownEvent event);
    void onDrag(DragEvent event);
    void onClickUp(ClickUpEvent event);

    //Only computer
    void onKeyDown(int keycode);
    void onKeyUp(int keycode);
    void onKeyTyped(char character);
    void onScrolled(int amount);
}