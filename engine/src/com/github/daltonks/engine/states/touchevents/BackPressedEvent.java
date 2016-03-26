package com.github.daltonks.engine.states.touchevents;
import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.interfaces.EngineRunnable;

public class BackPressedEvent implements EngineRunnable {
    @Override
    public void run(EngineState engineState) {
        if(!engineState.onBackPressed()) {
            Engine.INSTANCE.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Engine.INSTANCE.superOnBackPressed();
                }
            });
        }
    }
}