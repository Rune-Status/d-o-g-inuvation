package org.rspeer.api.commons;

public final class GameAccount {

    private final String username, password;
    private final int pin;

    public GameAccount(String details) {
        System.out.println(details);
        String[] split = details.split(":");
        username = split[0];
        password = split[1];
        pin = split.length > 2 ? Integer.parseInt(split[2]) : 0;
    }

    public GameAccount(String username, String password, int pin) {
        this.username = username;
        this.password = password;
        this.pin = pin;
    }

    public GameAccount(String username, String password) {
        this(username, password, -1);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPin() {
        return pin;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GameAccount)) {
            return false;
        }
        GameAccount other = (GameAccount) o;
        return other.username.equals(username) && other.password.equals(password);
    }

    public boolean validate() {
        return !username.isEmpty() && !password.isEmpty();
    }

    @Override
    public String toString() {
        return username;
    }
}
