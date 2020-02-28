package org.rspeer.game.api.query;

import org.rspeer.game.adapter.scene.Mobile;
import org.rspeer.game.adapter.scene.Projectile;
import org.rspeer.game.api.scene.Projectiles;
import org.rspeer.game.api.commons.ArrayUtils;
import org.rspeer.game.api.commons.predicate.IdPredicate;
import org.rspeer.game.api.query.results.PositionableQueryResults;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public final class ProjectileQueryBuilder extends PositionableQueryBuilder<Projectile, ProjectileQueryBuilder> {

    private Boolean targeting = null;

    private int[] ids = null;

    private Mobile[] targets = null;

    @Override
    public Supplier<List<? extends Projectile>> getDefaultProvider() {
        return () -> Projectiles.getLoaded().asList();
    }

    @Override
    protected PositionableQueryResults<Projectile> createQueryResults(Collection<? extends Projectile> raw) {
        return new PositionableQueryResults<>(raw);
    }

    public ProjectileQueryBuilder ids(int... ids) {
        this.ids = ids;
        return self();
    }

    public ProjectileQueryBuilder targetless() {
        targeting = false;
        return self();
    }

    public ProjectileQueryBuilder targeting() {
        targeting = true;
        return self();
    }

    public ProjectileQueryBuilder targeting(Mobile... targets) {
        this.targets = targets;
        return self();
    }

    @Override
    public boolean test(Projectile projectile) {
        if (ids != null && !new IdPredicate<>(ids).test(projectile)) {
            return false;
        }

        if (targeting != null && targeting == (projectile.getProvider().getTargetIndex() == -1)) {
            return false;
        }

        if (targets != null && !ArrayUtils.contains(targets, projectile.getTarget())) {
            return false;
        }

        return super.test(projectile);
    }
}
