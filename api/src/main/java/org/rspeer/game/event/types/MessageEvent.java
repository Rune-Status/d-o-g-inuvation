package org.rspeer.game.event.types;

import org.rspeer.event.Event;
import org.rspeer.game.event.listener.EventListener;
import org.rspeer.game.event.listener.MessageListener;
import org.rspeer.game.providers.RSChatMessageIcon;

public final class MessageEvent extends Event<String> {

    public static final int TYPE_GAME = 0;
    public static final int TYPE_PLAYER = 2;
    public static final int TYPE_SEND_PRIVATE = 3;
    public static final int TYPE_RECEIVE_PRIVATE = 6;

    private final int type;
    private final String message, channel;
    private final RSChatMessageIcon icon;

    public MessageEvent(int type, String source, String message, String channel, RSChatMessageIcon icon) {
        super(source, "Static");
        this.type = type;
        this.message = message;
        this.channel = channel;
        this.icon = icon;
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof MessageListener) {
            ((MessageListener) listener).notify(this);
        }
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getChannel() {
        return channel;
    }

    public RSChatMessageIcon getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return "[" + channel + "] " + source + ": " + message + " <" + type + ">";
    }
}
