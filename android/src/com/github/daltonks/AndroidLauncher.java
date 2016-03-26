package com.github.daltonks;

import android.os.Bundle;

import android.view.MotionEvent;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.github.daltonks.engine.Engine;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.stencil = 8;
		config.numSamples = 2;
		config.hideStatusBar = true;
		initialize(new Engine(), config);
	}

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if(!Engine.INSTANCE.isHaltingUpdates()) {
            Engine.INSTANCE.getCurrentSubActivity().onTouchEvent(e);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Engine.INSTANCE.onBackPressed();
    }
}