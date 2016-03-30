package com.github.daltonks.engine.states.inputevents;

import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.interfaces.Updatable;
import com.github.daltonks.engine.world.EngineWorld;

public class EngineInputHandler<T extends EngineState> implements Updatable {

    protected T state;

    public EngineInputHandler(T state) {
        this.state = state;
    }

    @Override
    public void update(EngineState engineState, double delta) {

    }

    public void onClickDown(ClickDownEvent event){}
    public void onDrag(DragEvent event){}
    public void onClickUp(ClickUpEvent event){}
    public void onMouseMove(int deltaX, int deltaY){}
    public void onKeyDown(int keycode){}
    public void onKeyUp(int keycode){}
    public void onKeyType(char character){}
    public void onScroll(int amount){}

    public void onUICapturedClickDown(ClickDownEvent event) {}
    public void onUICapturedDrag(DragEvent event){}
    public void onUICapturedClickUp(ClickUpEvent event){}

    public T getEngineState() {
        return state;
    }
}