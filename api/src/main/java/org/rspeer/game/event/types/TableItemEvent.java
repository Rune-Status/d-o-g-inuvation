package org.rspeer.game.event.types;

import org.rspeer.event.Event;
import org.rspeer.game.api.Game;
import org.rspeer.game.event.listener.EventListener;
import org.rspeer.game.event.listener.TableItemListener;
import org.rspeer.game.providers.RSItemTable;

public final class TableItemEvent extends Event<Integer> {

    private final int slot;
    private final boolean idk;
    private final int newId;
    private final int newQuantity;
    private final int oldId;
    private final int oldQuantity;

    public TableItemEvent(Integer source,
                          boolean idk,
                          int slot,
                          int newId, int newQuantity,
                          int oldId, int oldQuantity) {
        super(source, null);
        this.idk = idk;
        this.slot = slot;
        this.newId = newId;
        this.newQuantity = newQuantity;
        this.oldId = oldId;
        this.oldQuantity = oldQuantity;
    }

    public int getTableId() {
        return getSource();
    }

    public long getTableKey() {
        return getSource() | (idk ? Integer.MAX_VALUE : 0);
    }

    public RSItemTable getTable() {
        return Game.getClient().getItemTables().getSynthetic(getTableKey());
    }

    public int getSlot() {
        return slot;
    }

    public int getOldId() {
        return oldId;
    }

    public int getOldQuantity() {
        return oldQuantity;
    }

    public int getNewId() {
        return newId;
    }

    public int getNewQuantity() {
        return newQuantity;
    }

    //Nothing changed, this is just a redundant/update event.
    public boolean noChange() {
        return oldId == newId && oldQuantity == newQuantity;
    }

    //Does this event mean a new item was added?
    public boolean itemAdded() {
        return oldId == -1 && newId != -1;
    }

    //Does this event mean the old item was removed?
    public boolean itemRemoved() {
        return newId == -1 && oldId != -1;
    }

    //Does this event mean the item was replaced with a new id (not removed, or added, but replaced in id)
    public boolean itemReplaced() {
        return oldId != -1 && newId != -1 && oldId != newId;
    }

    //Does this event mean the id is the same and only the quantity was changed.
    public boolean itemUpdated() {
        return oldId == newId && oldQuantity != newQuantity;
    }

    //Does this event mean the item increased in quantity?
    public boolean isIncrease() {
        return oldId == newId && newQuantity > oldQuantity;
    }

    //Does this event mean the item decrased in quantity?
    public boolean isDecrease() {
        return oldId == newId && newQuantity < oldQuantity;
    }

    //Gets the change in quantity
    public int getChange() {
        return newQuantity - oldQuantity;
    }


    @Override
    public String toString() {
        return "TableEvent(" + source + "." + slot + ")[" + oldId + "x" + oldQuantity + " => " + newId + "x" + newQuantity + "]";
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof TableItemListener) {
            ((TableItemListener) listener).notify(this);
        }
    }
}
