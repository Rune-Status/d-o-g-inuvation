package org.rspeer.game.api.component;

import org.rspeer.game.api.Varps;

import java.util.function.IntSupplier;

public final class InventionPouch {

    private InventionPouch() {
        throw new IllegalAccessError();
    }

    public static int getAmount(Material material) {
        return material.provider.getAsInt();
    }

    public enum Material {

        SIMPLE(5998),
        CRAFTED(6011),
        JUNK(5997),
        SUBTLE(6039),
        TENSILE(6005);

        private final IntSupplier provider;

        Material(IntSupplier provider) {
            this.provider = provider;
        }

        Material(int varp) {
            this(() -> Varps.getValue(varp));
        }
    }
}
