package org.rspeer.game.adapter.world;

import org.rspeer.game.adapter.Adapter;
import org.rspeer.game.api.Game;
import org.rspeer.game.providers.RSWorld;

public final class World extends Adapter<RSWorld> {

    private static final int FLAG_MEMBERS = 0x1;
    private static final int FLAG_LOOTSHARE = 0x8;
    private static final int FLAG_SKILL_TOTAL_1500 = 0x80;
    private static final int FLAG_SKILL_TOTAL_2000 = 0x40000;
    private static final int FLAG_SKILL_TOTAL_2600 = 0x80000;
    private static final int FLAG_VIP_ONLY = 0x100000;
    private static final int FLAG_LEGACY_ONLY = 0x400000;
    private static final int FLAG_EOC_ONLY = 0x800000;

    private int world;

    public World(RSWorld provider) {
        super(provider);
    }

    public boolean isMembers() {
        return (provider.getMask() & FLAG_MEMBERS) == FLAG_MEMBERS;
    }

    public boolean isLootshare() {
        return (provider.getMask() & FLAG_LOOTSHARE) == FLAG_LOOTSHARE;
    }

    public boolean isSkillLevel1500() {
        return (provider.getMask() & FLAG_SKILL_TOTAL_1500) == FLAG_SKILL_TOTAL_1500;
    }

    public boolean isSkillLevel2000() {
        return (provider.getMask() & FLAG_SKILL_TOTAL_2000) == FLAG_SKILL_TOTAL_2000;
    }

    public boolean isSkillLevel2600() {
        return (provider.getMask() & FLAG_SKILL_TOTAL_2600) == FLAG_SKILL_TOTAL_2600;
    }

    public boolean isVipOnly() {
        return (provider.getMask() & FLAG_VIP_ONLY) == FLAG_VIP_ONLY;
    }

    public boolean isLegacyOnly() {
        return (provider.getMask() & FLAG_LEGACY_ONLY) == FLAG_LEGACY_ONLY;
    }

    public boolean isEocOnly() {
        return (provider.getMask() & FLAG_EOC_ONLY) == FLAG_EOC_ONLY;
    }

    public int getWorld() {
        return world;
    }

    public void setWorld(int world) {
        this.world = world;
    }

    public int getMask() {
        return provider.getMask();
    }

    public int getPopulation() {
        return provider.getPopulation();
    }

    public void hopTo() {
        if (Game.getClient().getConnectionState() != Game.getClient().getConstant("CONNECTION_STATE_WORLD_HOPPING")) {
            Game.getClient().setWorld(getWorld(), getProvider().getUrl(), 43594, 443);
            Game.getClient().getActiveConnection().close();
            Game.getClient().setConnectionState(Game.getClient().getConstant("CONNECTION_STATE_WORLD_HOPPING"));
        }
    }

    @Override
    public String toString() {
        return "World{" +
                "world=" + world + "\n" +
                "legacy=" + isLegacyOnly() + "\n" +
                "members=" + isMembers() + "\n" +
                "lootShare=" + isLootshare() + "\n" +
                "1500=" + isSkillLevel1500() + "\n" +
                "2000=" + isSkillLevel2000() + "\n" +
                "2600=" + isSkillLevel2600() + "\n" +
                "vip=" + isVipOnly() + "\n" +
                "eoc=" + isEocOnly() + "\n" +
                '}';
    }
}
