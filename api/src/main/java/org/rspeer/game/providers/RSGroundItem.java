package org.rspeer.game.providers;

import org.rspeer.game.adapter.scene.GroundItem;

public interface RSGroundItem extends RSNode {

    int getQuantity();

    int getId();

    GroundItem getAdapter();
}