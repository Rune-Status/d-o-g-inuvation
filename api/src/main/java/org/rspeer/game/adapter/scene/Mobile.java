package org.rspeer.game.adapter.scene;

import org.rspeer.api.commons.Functions;
import org.rspeer.api.commons.Identifiable;
import org.rspeer.game.adapter.node.StatusList;
import org.rspeer.game.api.action.Interactable;
import org.rspeer.game.api.combat.Hitsplat;
import org.rspeer.game.providers.*;

import java.util.ArrayList;
import java.util.List;

public abstract class Mobile<K extends RSMobile> extends SceneEntity<K> implements Identifiable, Interactable {

    protected Mobile(K provider) {
        super(provider);
    }

    public int getOrientation() {
        return provider.getOrientation();
    }

    public int getOrientationAsAngle() {
        return (630 - (getOrientation() * 45 / 2048)) % 360;
    }

    public int getQueueSize() {
        return provider.getQueueSize();
    }

    public int getTargetIndex() {
        return provider.getTargetIndex();
    }

    public int[] getHitsplatTypes() {
        return provider.getHitsplatTypes();
    }

    public String getOverheadText() {
        return Functions.mapOrDefault(provider::getOverheadMessage, RSOverheadMessage::getText, "");
    }

    public RSMobileAnimator getStanceAnimator() {
        return provider.getStanceAnimator();
    }

    public Mobile getTarget() {
        return Functions.mapOrDefault(provider::getTarget, RSMobile::getAdapter, null);
    }

    public boolean isMoving() {
        return getQueueSize() > 0;
    }

    //TODO maybe inject statuslist wrapper? just for consistency
    public RSCombatBar getAdrenalineBar() {
        StatusList<RSCombatGauge> list = new StatusList<>(provider.getCombatGaugeStatusList());
        return Functions.mapOrDefault(
                () -> list.find(gauge -> gauge.isAdrenaline() && gauge.getCombatBar() != null),
                RSCombatGauge::getCombatBar, null
        );
    }

    public RSCombatBar getHealthBar() {
        StatusList<RSCombatGauge> list = new StatusList<>(provider.getCombatGaugeStatusList());
        return Functions.mapOrDefault(
                () -> list.find(gauge -> gauge.isHealth() && gauge.getCombatBar() != null),
                RSCombatGauge::getCombatBar, null
        );
    }

    public RSCombatBar getProgressBar() {
        StatusList<RSCombatGauge> list = new StatusList<>(provider.getCombatGaugeStatusList());
        return Functions.mapOrDefault(
                () -> list.find(gauge -> gauge.isProgressBar() && gauge.getCombatBar() != null),
                RSCombatGauge::getCombatBar, null
        );
    }

    public int getIndex() {
        return provider.getIndex();
    }

    public List<Hitsplat> getHitsplats() {
        List<Hitsplat> hitsplats = new ArrayList<>();
        for (int i = 0; i < getHitsplatCycles().length; i++) {
            hitsplats.add(new Hitsplat(getHitsplatDamages()[i], getHitsplatTypes()[i], getHitsplatCycles()[i]));
        }
        return hitsplats;
    }

    public int[] getHitsplatDamages() {
        return provider.getHitsplatDamages();
    }

    public int[] getHitsplatCycles() {
        return provider.getHitsplatCycles();
    }

    public int getAnimation() {
        return Functions.mapOrM1(provider::getAnimator, RSAnimator::getAnimationId);
    }

    public int getStance() {
        return Functions.mapOrM1(provider::getStanceAnimator, RSAnimator::getAnimationId);
    }

    public int getStanceFrame() {
        return Functions.mapOrM1(provider::getStanceAnimator, RSAnimator::getAnimationFrame);
    }

    public int getAnimationFrame() {
        return Functions.mapOrM1(provider::getAnimator, RSAnimator::getAnimationFrame);
    }

    public RSMobileSpotAnimation[] getGraphics() {
        RSMobileSpotAnimation[] graphics = provider.getGraphics();
        return graphics == null ? new RSMobileSpotAnimation[0] : graphics;
    }

    public abstract int getCombatLevel();
}
