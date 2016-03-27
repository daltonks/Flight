package com.github.daltonks;

import android.os.Bundle;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidGraphics;
import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.states.inputevents.*;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.states.inputevents.InputRunnable;

public class AndroidLauncher extends AndroidApplication implements View.OnKeyListener, View.OnTouchListener {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.stencil = 8;
		config.numSamples = 2;
		config.hideStatusBar = true;
		initialize(new Engine(), config);
        ((AndroidGraphics) Gdx.graphics).getView().setOnKeyListener(this);
        ((AndroidGraphics) Gdx.graphics).getView().setOnTouchListener(this);
	}

    @Override
    public boolean onTouch(View view, MotionEvent e) {
        InputQueue queue = Engine.INSTANCE.getCurrentSubActivity().getInputQueue();
        if(!Engine.INSTANCE.isHaltingUpdates()) {
            int pointerCount = e.getPointerCount();

            switch(e.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN: {
                    System.out.println("Down: ");
                    for(int i = 0; i < pointerCount; i++) {
                        int pointerID = e.getPointerId(i);
                        queue.addClickDown((int) e.getX(i), (int) e.getY(i), pointerID, Input.Buttons.LEFT);

                    }
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    DragEvent touchEvent = Pools.getMoveTouchEvent();
                    for(int i = 0; i < pointerCount; i++) {
                        ClickTracker tracker = queue.getFingerTracker(e.getPointerId(i));
                        if(tracker == null)
                            continue;
                        tracker.x = e.getX(i);
                        tracker.y = e.getY(i);
                        touchEvent.addFingerTracker(tracker.clone());
                        tracker.previousX = tracker.x;
                        tracker.previousY = tracker.y;
                    }
                    if(!touchEvent.getClickTrackers().isEmpty()) {
                        queue.add(touchEvent);
                    } else {
                        Pools.recycleMoveTouchEvent(touchEvent);
                    }
                    break;
                }

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP: {
                    for(int i = 0; i < pointerCount; i++) {
                        queue.addClickUp(e.getPointerId(i));
                    }
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return false;
    }

    @Override
    public void onBackPressed() {
        if(!Engine.INSTANCE.isHaltingUpdates()) {
            Engine.INSTANCE.getCurrentSubActivity().getInputQueue().add(backPressedEvent);
        }
    }

    private BackPressedEvent backPressedEvent = new BackPressedEvent();

    public class BackPressedEvent implements InputRunnable {
        @Override
        public void run(EngineState engineState) {
            if(!engineState.onBackPressed()) {
                AndroidLauncher.this.onBackPressed();
            }
        }
    }
}