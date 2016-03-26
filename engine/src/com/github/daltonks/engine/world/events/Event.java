package com.github.daltonks.engine.world.events;

public abstract class Event {
    private boolean cancelled = false;

    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public abstract void run();
}