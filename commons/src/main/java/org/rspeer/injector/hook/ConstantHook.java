package org.rspeer.injector.hook;

import org.rspeer.io.Crypto;

import java.io.DataInputStream;
import java.io.IOException;

public final class ConstantHook extends Hook {

    private String name, owner;
    private int value;

    @Override
    protected void read(DataInputStream in) throws IOException {
        name = Crypto.decrypt(in.readUTF());
        owner = Crypto.decrypt(in.readUTF());
        value = in.readInt();
    }

    @Override
    public String getDefinedName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public String getOwner() {
        return owner;
    }
}
