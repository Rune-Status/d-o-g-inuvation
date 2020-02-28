package org.rspeer.game.adapter.scene;

import org.rspeer.api.commons.Functions;
import org.rspeer.api.commons.Identifiable;
import org.rspeer.game.providers.RSMobile;
import org.rspeer.game.providers.RSProjectile;

public final class Projectile extends SceneEntity<RSProjectile> implements Identifiable {

    public Projectile(RSProjectile provider) {
        super(provider);
    }

    public int getId() {
        return provider.getId();
    }

    public int getAnimation() {
        return provider.getAnimation();
    }

    public Mobile getTarget() {
        return Functions.mapOrDefault(() -> provider.resolve(provider.getTargetIndex()), RSMobile::getAdapter, null);
    }

    public Mobile getSource() {
        return Functions.mapOrDefault(() -> provider.resolve(provider.getSourceIndex()), RSMobile::getAdapter, null);
    }

    public int getSourceEquipmentSlot() {
        return provider.getSourceEquipmentSlot();
    }
}
