package org.rspeer.game.event.listener;

import org.rspeer.game.event.types.ProjectileSpawnedEvent;

public interface ProjectileSpawnedListener extends EventListener {
    void notify(ProjectileSpawnedEvent e);
}
