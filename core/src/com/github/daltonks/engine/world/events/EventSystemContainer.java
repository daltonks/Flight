package com.github.daltonks.engine.world.events;

import java.util.ArrayList;
import java.util.HashMap;

public class EventSystemContainer {
    private HashMap<Class, ArrayList<EventListener>> listeners = new HashMap<Class, ArrayList<EventListener>>();

    public void addListener(Class clss, EventListener listener) {
        if(!listeners.containsKey(clss))
            listeners.put(clss, new ArrayList<EventListener>());
        listeners.get(clss).add(listener);
    }

    public void removeListener(EventListener listener) {
        ArrayList<EventListener> list = listeners.get(listener.getEventClass());
        list.remove(listener);
        if(list.isEmpty()) {
            listeners.remove(listener.getEventClass());
        }
    }

    public void performEvent(Event e) {
        ArrayList<EventListener> list = listeners.get(e.getClass());
        if(list == null && !e.isCancelled()) {
            e.run();
        } else {
            for(int i = 0; i < list.size(); i++) {
                EventListener eL = list.get(i);
                if(eL.isArmed()) {
                    eL.beforeEvent(e);
                }
            }

            for(int i = 0; i < list.size(); i++) {
                EventListener eL = list.get(i);
                if(eL.isArmed()) {
                    eL.rightBeforeEvent(e);
                }
            }

            if(e.isCancelled()) {
                return;
            }

            e.run();

            for(int i = 0; i < list.size(); i++) {
                EventListener eL = list.get(i);
                if(eL.isArmed()) {
                    eL.afterEvent(e);
                }
            }
        }
    }
}
