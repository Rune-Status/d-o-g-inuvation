package org.rspeer.game.api.combat;

import org.rspeer.game.adapter.scene.Mobile;
import org.rspeer.game.api.scene.Players;
import org.rspeer.game.providers.RSMobileSpotAnimation;

/**
 * An enum consisting of spot animation details for abilities.
 * This is most particularly useful for checking how far into an animation you are.
 * As en example, see how far into asphyxiate you are. This was chosen over
 * regular animations due to having less ids to deal with - in regular animations,
 * a lot of different weaponry did not share the same animation ids.
 * <p>
 * One other good use, albeit less significant, is to use this information
 * to start preparing for your next attack. As an example, sonic wave increases
 * the accuracy of your next attack, so you may want to use this information to
 * prepare a more powerful ability next.
 */
public enum AbilityGraphic {

    ASPHYXIATE(50, 18401, 18400),
    SONIC_WAVE(15, 19996);

    private final int duration;
    private final int[] ids;

    AbilityGraphic(int duration, int... ids) {
        this.duration = duration;
        this.ids = ids;
    }

    /**
     * @return The duration of this spot animation (in frames)
     */
    public int getDuration() {
        return duration;
    }

    /**
     * @return The possible ids of this spot animation (May be different between staff/wand etc)
     */
    public int[] getIds() {
        return ids;
    }

    public int getProgress(Mobile mobile) {
        for (RSMobileSpotAnimation graphic : mobile.getGraphics()) {
            if (graphic.isActive()) {
                for (int id : ids) {
                    if (id == graphic.getEffect()) {
                        return graphic.getEffectFrame();
                    }
                }
            }
        }
        return -1;
    }

    public int getProgressPercent(Mobile mobile) {
        int progress = getProgress(mobile);
        return (int) ((double) progress * 100D / ((double) duration));
    }

    public int getProgress() {
        return getProgress(Players.getLocal());
    }

    public int getProgressPercent() {
        return getProgressPercent(Players.getLocal());
    }

    public boolean isActive(Mobile mobile) {
        for (RSMobileSpotAnimation graphic : mobile.getGraphics()) {
            if (graphic.isActive()) {
                for (int id : ids) {
                    if (id == graphic.getEffect()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isActive() {
        return isActive(Players.getLocal());
    }
}
