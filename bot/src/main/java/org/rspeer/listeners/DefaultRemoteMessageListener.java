package org.rspeer.listeners;

import org.rspeer.Inuvation;
import org.rspeer.event.listeners.RemoteMessageListener;
import org.rspeer.event.types.RemoteMessageEvent;
import org.rspeer.script.ScriptReloader;

public class DefaultRemoteMessageListener implements RemoteMessageListener {

    @Override
    public void notify(RemoteMessageEvent e) {
        String message = e.getSource().getMessage();
        System.out.println(message);
        System.out.println("Received message: " + message);
        if (message.equals(":kill")) {
            System.out.println("Closing client, reason: " + e.getSource().getSource());
            Inuvation.shutdown();
            return;
        }
        if (message.equals(":reload_script")) {
            new ScriptReloader().execute();
        }
    }
}
