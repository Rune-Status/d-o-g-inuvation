package org.rspeer.game.providers;

public interface RSCombatBar extends RSStatusNode {

    int getWidth();

    int getCycle();

    int getType();

    int getIdk();

    default int getPercent() {
        return (int) Math.ceil(getWidth() * 100D / 255); //0xffD
    }

//    var25 = var18.w() * -714693597 * var15.v / 255;
//    var106 = var24 * (var23 - var25) / (562395071 * var15.g) + var25;


    default int getNewPercent(RSCombatGaugeDefinition definition) {
//        var24 = 606795069 * var16.g == 0 ? 0 : 606795069 * var16.g * (var22 / (606795069 * var16.g));
//        int multiplier = definition
        int percent = getPercent();
        int newMax = getIdk() / 255;
        return (percent - newMax) / getType() + newMax;
    }
}