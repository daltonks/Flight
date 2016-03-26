package com.github.daltonks.engine.world.events;

public abstract class EventListener<T extends Event> {
    private boolean armed = true;
    private Class<T> eventClass;

    public EventListener(Class<T> eventClass) {
        this.eventClass = eventClass;
    }

    public EventListener(Class<T> eventClass, boolean armed) {
        this(eventClass);
        this.armed = armed;
    }

    public void arm() {
        armed = true;
    }

    public void disarm() {
        armed = false;
    }

    public boolean isArmed() {
        return armed;
    }

    public Class<T> getEventClass() {
        return eventClass;
    }

    public abstract void beforeEvent(T e);
    public abstract void rightBeforeEvent(T e);
    public abstract void afterEvent(T e);
}