package com.github.daltonks.engine.states.inputevents;

import com.badlogic.gdx.InputProcessor;
import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.util.Pools;

public class ComputerInputProcessor implements InputProcessor {

    @Override
    public boolean keyDown(int keycode) {
        Engine.INSTANCE.getCurrentSubActivity().onKeyDown(keycode);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        Engine.INSTANCE.getCurrentSubActivity().onKeyUp(keycode);
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        Engine.INSTANCE.getCurrentSubActivity().onKeyTyped(character);
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        InputQueue queue = Engine.INSTANCE.getCurrentSubActivity().getInputQueue();
        queue.addClickDown(screenX, screenY, 0, button);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        InputQueue queue = Engine.INSTANCE.getCurrentSubActivity().getInputQueue();
        queue.addClickUp(0);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        InputQueue queue = Engine.INSTANCE.getCurrentSubActivity().getInputQueue();
        DragEvent touchEvent = Pools.getMoveTouchEvent();
        ClickTracker tracker = queue.getFingerTracker(0);
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

    //Not used, because it doesn't work on iOS
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        Engine.INSTANCE.getCurrentSubActivity().onScrolled(amount);
        return true;
    }
}