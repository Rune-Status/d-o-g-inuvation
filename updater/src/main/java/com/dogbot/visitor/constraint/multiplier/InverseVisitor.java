package com.dogbot.visitor.constraint.multiplier;

import org.rspeer.api.collections.Multiset;

import java.math.BigInteger;

public interface InverseVisitor {

    BigInteger getDecoder(String clazz, String field, boolean longType);

    Multiset<BigInteger> encoderSet(String clazz, String field);

    Multiset<BigInteger> decoderSet(String clazz, String field);

    void apply();

    void debug();
}
