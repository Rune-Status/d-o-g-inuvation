package org.rspeer.game.adapter.cache;

import org.rspeer.game.adapter.Adapter;
import org.rspeer.game.providers.RSDefinitionLoader;
import org.rspeer.game.providers.RSNpcDefinition;

public final class NpcDefinition extends Adapter<RSNpcDefinition> {

    public NpcDefinition(RSNpcDefinition provider) {
        super(provider);
    }

    public int getVarpIndex() {
        return provider.getVarpIndex();
    }

    public RSDefinitionLoader getLoader() {
        return provider.getLoader();
    }

    public boolean isShowingOnMinimap() {
        return provider.isShowingOnMinimap();
    }

    public String getName() {
        return provider.getName();
    }

    public int[] getTransformIds() {
        int[] trans = provider.getTransformIds();
        return trans != null ? trans : new int[0];
    }

    public int getVarpBitIndex() {
        return provider.getVarpBitIndex();
    }

    public int getId() {
        return provider.getId();
    }

    public String[] getActions() {
        return provider.getActions();
    }

    public int getTeam() {
        return provider.getTeam();
    }

    public int getHeight() {
        return provider.getHeight();
    }

    public int getBoundSize() {
        return provider.getBoundSize();
    }

    public int getCombatLevel() {
        return provider.getCombatLevel();
    }

    public int getMapFunction() {
        return provider.getMapFunction();
    }

    public boolean isHalfStep() {
        return provider.isHalfStep();
    }
}
