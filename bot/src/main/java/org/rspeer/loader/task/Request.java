package org.rspeer.loader.task;

import com.allatori.annotations.DoNotRename;

public final class Request {

    @DoNotRename
    private final String sha1;
    @DoNotRename
    private final String archive;
    @DoNotRename
    private final String secret;
    @DoNotRename
    private final String vector;

    public Request(String sha1, String archive, String secret, String vector) {
        this.sha1 = sha1;
        this.archive = archive;
        this.secret = secret;
        this.vector = vector;
    }
}
