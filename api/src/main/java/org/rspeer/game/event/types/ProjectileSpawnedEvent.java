package org.rspeer.game.event.types;

import org.rspeer.event.Event;
import org.rspeer.game.adapter.scene.Projectile;
import org.rspeer.game.event.listener.EventListener;
import org.rspeer.game.event.listener.GrandExchangeOfferChangedListener;
import org.rspeer.game.event.listener.ProjectileSpawnedListener;

public class ProjectileSpawnedEvent extends Event<Projectile> {

    public ProjectileSpawnedEvent(Projectile spawnedProjectile) {
        super(spawnedProjectile, "Static");
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof ProjectileSpawnedListener) {
            ((ProjectileSpawnedListener) listener).notify(this);
        }
    }
}
