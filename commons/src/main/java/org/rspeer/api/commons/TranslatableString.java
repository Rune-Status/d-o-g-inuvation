package org.rspeer.api.commons;

public final class TranslatableString {

    private final String[] strings = new String[4];

    public TranslatableString(String english, String german, String french, String spanish) {
        this.strings[0] = english;
        this.strings[1] = german;
        this.strings[2] = french;
        this.strings[3] = spanish;
    }

    public String getActive() {
        return strings[0];
    }
}
