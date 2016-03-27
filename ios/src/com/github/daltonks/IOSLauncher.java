package com.github.daltonks;

import com.badlogic.gdx.Gdx;
import com.github.daltonks.engine.Engine;
import com.github.daltonks.engine.states.inputevents.ComputerInputProcessor;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.glkit.GLKViewDrawableMultisample;
import org.robovm.apple.glkit.GLKViewDrawableStencilFormat;
import org.robovm.apple.uikit.UIApplication;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;

public class IOSLauncher extends IOSApplication.Delegate {
    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        config.stencilFormat = GLKViewDrawableStencilFormat._8;
        config.multisample = GLKViewDrawableMultisample._4X;
        return new IOSApplication(new Engine(), config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }
}