package com.github.daltonks.engine.states.inputevents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.util.Pools;

public class ComputerInputProcessor implements InputProcessor {

    @Override
    public boolean keyDown(int keycode) {
        Engine.INSTANCE.getCurrentSubActivity().getInputHandler().onKeyDown(keycode);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        Engine.INSTANCE.getCurrentSubActivity().getInputHandler().onKeyUp(keycode);
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        Engine.INSTANCE.getCurrentSubActivity().getInputHandler().onKeyType(character);
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        InputQueue queue = Engine.INSTANCE.getCurrentSubActivity().getInputQueue();
        queue.addClickDown(screenX, screenY, pointer, button);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        InputQueue queue = Engine.INSTANCE.getCurrentSubActivity().getInputQueue();
        queue.addClickUp(pointer);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        InputQueue queue = Engine.INSTANCE.getCurrentSubActivity().getInputQueue();
        DragEvent touchEvent = Pools.getDragEvent();
        ClickTracker tracker = queue.getFingerTracker(pointer);
        if(tracker != null) {
            tracker.x = screenX;
            tracker.y = screenY;
            touchEvent.addFingerTracker(tracker.clone());
            tracker.previousX = tracker.x;
            tracker.previousY = tracker.y;
            queue.add(touchEvent);
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Engine.INSTANCE.getCurrentSubActivity().getInputHandler().onMouseMove(Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        Engine.INSTANCE.getCurrentSubActivity().getInputHandler().onScroll(amount);
        return true;
    }
}