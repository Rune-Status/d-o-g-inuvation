package org.rspeer.game.api.query;

import org.rspeer.game.adapter.world.World;
import org.rspeer.game.api.Worlds;
import org.rspeer.game.api.commons.ArrayUtils;
import org.rspeer.game.api.query.results.WorldQueryResults;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public final class WorldQueryBuilder extends QueryBuilder<World, WorldQueryBuilder, WorldQueryResults> {

    private Boolean members = null;
    private Boolean current = null;
    private Boolean lootshare = null;
    private Boolean skillTotal = null;
    private Boolean vip = null;
    private Boolean legacy = null;
    private Boolean eoc = null;

    private int[] ids = null;

    @Override
    public Supplier<List<? extends World>> getDefaultProvider() {
        return () -> Arrays.asList(Worlds.getLoaded(a -> true));
    }

    @Override
    protected WorldQueryResults createQueryResults(Collection<? extends World> raw) {
        return new WorldQueryResults(raw);
    }

    public WorldQueryBuilder members(boolean members) {
        this.members = members;
        return self();
    }

    public WorldQueryBuilder current(boolean current) {
        this.current = current;
        return self();
    }

    public WorldQueryBuilder lootshare(boolean lootshare) {
        this.lootshare = lootshare;
        return self();
    }

    public WorldQueryBuilder skillTotal(boolean skillTotal) {
        this.skillTotal = skillTotal;
        return self();
    }

    public WorldQueryBuilder vip(boolean vip) {
        this.vip = vip;
        return self();
    }

    public WorldQueryBuilder legacy(boolean legacy) {
        this.legacy = legacy;
        return self();
    }

    public WorldQueryBuilder eoc(boolean eoc) {
        this.eoc = eoc;
        return self();
    }

    public WorldQueryBuilder ids(int... ids) {
        this.ids = ids;
        return self();
    }

    @Override
    public boolean test(World world) {
        if (current != null) {
            World currentWorld = Worlds.getCurrent();
            if (currentWorld != null && (world.getWorld() == currentWorld.getWorld()) != current) {
                return false;
            }
        }

        if (members != null && world.isMembers() != members) {
            return false;
        }

        if (lootshare != null && world.isLootshare() != lootshare) {
            return false;
        }

        if (skillTotal != null && (world.isSkillLevel1500() || world.isSkillLevel2000() || world.isSkillLevel2600()) != skillTotal) {
            return false;
        }

        if (vip != null && world.isVipOnly() != vip) {
            return false;
        }

        if (legacy != null && world.isLegacyOnly() != legacy) {
            return false;
        }

        if (eoc != null && world.isEocOnly() != eoc) {
            return false;
        }

        if (ids != null && !ArrayUtils.contains(ids, world.getWorld())) {
            return false;
        }

        return super.test(world);
    }
}
