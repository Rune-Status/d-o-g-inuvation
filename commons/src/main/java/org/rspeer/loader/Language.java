package org.rspeer.loader;

public enum Language {

    ENGLISH(0),
    GERMAN(1),
    FRENCH(2),
    SPANISH(3);

    private final int id;

    Language(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
