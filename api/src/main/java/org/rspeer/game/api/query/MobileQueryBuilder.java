package org.rspeer.game.api.query;

import org.rspeer.game.adapter.scene.Mobile;
import org.rspeer.game.api.commons.ArrayUtils;
import org.rspeer.game.api.commons.Range;
import org.rspeer.game.api.commons.predicate.NamePredicate;

public abstract class MobileQueryBuilder<K extends Mobile, Q extends QueryBuilder>
        extends PositionableQueryBuilder<K, Q> {

    private Boolean animating = null;
    private Boolean targeting = null;

    private Range health = null;

    private int[] animations = null;
    private int[] stances = null;

    private Mobile[] targets = null;

    private String[] names = null;
    private String[] nameContains = null;
    private String[] dialogues = null;

    public Q animating() {
        animating = true;
        return self();
    }

    public Q inanimate() {
        animating = false;
        return self();
    }

    public Q animations(int... animations) {
        this.animations = animations;
        return self();
    }

    public Q dialogues(String... dialogues) {
        this.dialogues = dialogues;
        return self();
    }

    public Q health(int minPercent, int maxPercent) {
        health = Range.of(minPercent, maxPercent);
        return self();
    }

    public Q health(int minPercent) {
        return health(minPercent, Integer.MAX_VALUE);
    }

    public Q names(String... names) {
        this.names = names;
        return self();
    }

    public Q nameContains(String... names) {
        this.nameContains = names;
        return self();
    }

    public Q stances(int... stances) {
        this.stances = stances;
        return self();
    }

    public Q targetless() {
        targeting = false;
        return self();
    }

    public Q targeting() {
        targeting = true;
        return self();
    }

    public Q targeting(Mobile... targets) {
        this.targets = targets;
        return self();
    }

    //TODO targetedBy

    @Override
    public boolean test(K e) {
        if (names != null && !new NamePredicate<>(names).test(e)) {
            return false;
        }

        if (nameContains != null && !new NamePredicate<>(true, nameContains).test(e)) {
            return false;
        }

        if (animating != null && animating == (e.getAnimation() == -1)) {
            return false;
        }

        if (targeting != null && targeting == (e.getTargetIndex() == -1)) {
            return false;
        }

        if (health != null && e.getHealthBar() != null && !health.within(e.getHealthBar().getPercent())) {
            return false;
        }

        if (animations != null && ArrayUtils.contains(this.animations, e.getAnimation())) {
            return false;
        }

        if (dialogues != null && !ArrayUtils.contains(dialogues, e.getOverheadText())) {
            return false;
        }

        if (stances != null && !ArrayUtils.contains(stances, e.getStance())) {
            return false;
        }

        if (targets != null && !ArrayUtils.contains(targets, e.getTarget())) {
            return false;
        }

        return super.test(e);
    }
}
