package org.rspeer.game.providers;

public interface RSFriendsChatMember extends RSProvider {

    int getWorld();

    byte getRank();

    String getName();
}