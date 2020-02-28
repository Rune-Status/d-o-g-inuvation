package org.rspeer.game.api.component;

import org.rspeer.game.api.ItemTables;
import org.rspeer.game.api.VarpComposite;
import org.rspeer.game.api.VarpbitComposite;
import org.rspeer.game.api.Varps;

import java.util.function.IntSupplier;

public final class Currency {

    private Currency() {
        throw new IllegalAccessError();
    }

    public static int getAmount(Type type) {
        return type.provider.getAsInt();
    }

    public enum Type {

        MONEY_POUCH(() -> ItemTables.getCount(ItemTables.MONEY_POUCH, true, x -> true)),
        MEMORY_STRANDS(() -> Varps.getBitValue(VarpbitComposite.MEMORY_STRANDS)),
        CHIMES(() -> Varps.getValue(VarpComposite.CHIMES));

        private final IntSupplier provider;

        Type(IntSupplier provider) {
            this.provider = provider;
        }
    }
}
