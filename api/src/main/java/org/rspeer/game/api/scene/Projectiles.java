package org.rspeer.game.api.scene;

import org.rspeer.api.commons.Identifiable;
import org.rspeer.api.commons.Predicates;
import org.rspeer.game.adapter.node.NodeDeque;
import org.rspeer.game.adapter.scene.Projectile;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.position.Distance;
import org.rspeer.game.api.query.results.PositionableQueryResults;
import org.rspeer.game.providers.RSProjectileNode;
import org.rspeer.game.api.query.ProjectileQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class Projectiles {

    private Projectiles() {
        throw new IllegalAccessError();
    }

    public static PositionableQueryResults<Projectile> getLoaded() {
        return getLoaded(Predicates.always());
    }

    public static PositionableQueryResults<Projectile> getLoaded(Predicate<Projectile> predicate) {
        List<Projectile> projectiles = new ArrayList<>();
        NodeDeque<RSProjectileNode> nodes = new NodeDeque<>(Game.getClient().getProjectileNodeDeque());
        for (RSProjectileNode node : nodes) {
            if (node != null && node.getProjectile() != null) {
                Projectile projectile = node.getProjectile().getAdapter();
                if (predicate.test(projectile)) {
                    projectiles.add(projectile);
                }
            }
        }
        return new PositionableQueryResults<>(projectiles);
    }

    public static Projectile getNearest(Predicate<Projectile> pred) {
        return Distance.getNearest(getLoaded(), pred);
    }

    public static Projectile getNearest(int... ids) {
        return getNearest(Identifiable.predicate(ids));
    }

    public static ProjectileQueryBuilder newQuery() {
        return new ProjectileQueryBuilder();
    }
}
