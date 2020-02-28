package org.rspeer.game.providers;

public interface RSGlobalPlayer extends RSProvider {

    int getOrientation();

    int getTargetIndex();

    int getPositionHash();

    int getBaseX();

    int getBaseY();

    void setBaseX(int baseX);

    void setBaseY(int baseY);

    boolean isClanMate();
}
