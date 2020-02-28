package org.rspeer.listeners;


import org.rspeer.Configuration;
import org.rspeer.bot.Bot;
import org.rspeer.event.listeners.ServerConnectionChangeListener;
import org.rspeer.event.types.ServerConnectionEvent;
import org.rspeer.ui.BotTitlePaneHelper;

public class ServerConnectionListener implements ServerConnectionChangeListener {

    private final Bot bot;
    private ServerConnectionEvent.Status last;

    public ServerConnectionListener(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void notify(ServerConnectionEvent e) {
        switch (e.getSource()) {
            case CONNECTED:
                onConnect();
                last = e.getSource();
                break;
            case DISCONNECTED:
                onDisconnect();
                last = e.getSource();
                break;
        }
    }

    private void onConnect() {
        if (last != null && last == ServerConnectionEvent.Status.DISCONNECTED) {
            System.out.println("Successfully reconnected to RSPeer servers.");
        }
        BotTitlePaneHelper.refreshFrameTitle(bot);
    }

    private void onDisconnect() {
        System.out.println("Your client has lost connection to the RSPeer server for over 5 minutes. This can happen if you are over your instance limit. " +
                "Your client will be closed in 5 minutes unless connection is regained.");
        bot.getView().getFrame().setTitle(Configuration.APPLICATION_NAME + " | Closing In 5 Minutes");
    }
}
