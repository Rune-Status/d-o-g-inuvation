package org.rspeer.api.commons;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CommonMath {

    private CommonMath() {
        throw new IllegalAccessError();
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        return bd.setScale(places, RoundingMode.HALF_UP).doubleValue();
    }
}