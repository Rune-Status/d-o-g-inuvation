package org.rspeer.networking.entities;

import com.allatori.annotations.DoNotRename;

@DoNotRename
public class RemoteMessage {

    @DoNotRename
    public int id;
    @DoNotRename
    private String source;
    @DoNotRename
    private String message;

    @DoNotRename
    public String getMessage() {
        return message;
    }

    @DoNotRename
    public String getSource() {
        return source;
    }

    @DoNotRename
    public int getId() {
        return id;
    }
}
