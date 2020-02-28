package com.dogbot.visitor.constraint.multiplier;

import java.math.BigInteger;

public final class Modulus {

    private final BigInteger quotient;
    private final int bits;

    public Modulus(BigInteger quotient, int bits) {
        this.quotient = quotient;
        this.bits = bits;
    }

    public BigInteger compute() {
        try {
            BigInteger shift = BigInteger.ONE.shiftLeft(bits);
            return quotient.modInverse(shift);
        } catch (ArithmeticException e) {
            return null;
        }
    }

    public boolean validate() {
        return compute() != null;
    }

    public BigInteger getQuotient() {
        return quotient;
    }
}