/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.dogbot.hookspec.hook;

import com.dogbot.Updater;
import com.dogbot.hookspec.Crypto;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Dogerina
 * @since 08-08-2015
 */
public class ConstantHook extends Hook {

    private final String parent;
    private final int value;

    public ConstantHook(String name, String parent, int value) {
        super(name);
        this.parent = parent;
        this.value = value;
    }

    @Override
    public byte getType() {
        return Type.CONSTANT;
    }

    @Override
    public String getOutput(Updater updater) {
        return String.format("%% %s.%s - %d", parent, name, value);
    }

    @Override
    protected void writeData(DataOutputStream out) throws IOException {
        out.writeUTF(name);
        out.writeUTF(parent);
        out.writeInt(value);
    }

    @Override
    protected void writeEncryptedData(DataOutputStream out) throws IOException {
        out.writeUTF(Crypto.encrypt(name));
        out.writeUTF(Crypto.encrypt(parent));
        out.writeInt(value);
    }

    public int getValue() {
        return value;
    }

    public String getParent() {
        return parent;
    }
}
