package org.rspeer.event.impl;

import org.rspeer.api.commons.ExecutionService;
import org.rspeer.event.Event;
import org.rspeer.game.event.listener.EventListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public final class EventDispatcher {

    private final Queue<Event> delayed;
    private final List<EventListener> listeners;
    private final Object mutex;
    private boolean active;

    public EventDispatcher() {
        delayed = new LinkedList<>();
        listeners = new CopyOnWriteArrayList<>();
        mutex = new Object();
        active = true;
        ExecutionService.scheduleAtFixedRate(this::process, 0, 20, TimeUnit.MILLISECONDS);
    }

    public List<EventListener> getActive() {
        return Collections.unmodifiableList(listeners);
    }

    public List<EventListener> clearAll() {
        List<EventListener> active = getActive();
        for (EventListener listener : active) {
            deregister(listener);
        }
        return active;
    }

    public void register(EventListener listener) {
        synchronized (mutex) {
            listeners.add(listener);
        }
    }

    public void deregister(EventListener listener) {
        synchronized (mutex) {
            listeners.remove(listener);
        }
    }

    public void immediate(Event event) {
        synchronized (mutex) {
            if (active) {
                for (EventListener listener : listeners) {
                    event.forward(listener);
                }
            }
        }
    }

    public void delay(Event event) {
        synchronized (delayed) {
            delayed.add(event);
            delayed.notify();
        }
    }

    private Event poll() {
        while (true) {
            if (!active) {
                continue;
            }
            synchronized (delayed) {
                while (active && delayed.isEmpty()) {
                    try {
                        delayed.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                Event event = delayed.peek();
                if (event == null || event.getTime() > System.currentTimeMillis()) {
                    return null;
                }
                delayed.remove(event);
                return event;
            }
        }
    }

    public void clear() {
        delayed.clear();
    }

    private void process() {
        Event event = poll();
        while (event != null) {
            for (EventListener listener : listeners) {
                event.forward(listener);
            }
            event = poll();
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        synchronized (delayed) {
            delayed.notify();
        }
    }

    public boolean isRegistered(EventListener e) {
        return listeners.contains(e);
    }
}

