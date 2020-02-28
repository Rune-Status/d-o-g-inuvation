package org.rspeer.event;

import org.rspeer.event.impl.EventDispatcher;

/**
 * For internal use only. Scripters should use Game.getEventDispatcher()
 */
public class EventDispatcherProvider {

    private static final Object lock = new Object();

    private static EventDispatcherProvider provider;

    public static EventDispatcher provide() {
        synchronized (lock) {
           return getInstance().dispatcher;
        }
    }

    public static EventDispatcherProvider getInstance() {
        synchronized (lock) {
            if(provider == null) {
                provider = new EventDispatcherProvider();
            }
            return provider;
        }
    }

    private EventDispatcher dispatcher;

    public void setDispatcherOnce(EventDispatcher dispatcher) {
        if(this.dispatcher == null) {
            this.dispatcher = dispatcher;
        }
    }
}
