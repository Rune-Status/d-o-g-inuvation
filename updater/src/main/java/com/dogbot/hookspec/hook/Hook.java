package com.dogbot.hookspec.hook;

import com.dogbot.Updater;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Tyler Sedlar
 */
public abstract class Hook implements Comparable<Hook> {

    public final String name;

    public Hook(String name) {
        this.name = name;
    }

    public abstract byte getType();

    public abstract String getOutput(Updater updater);

    protected abstract void writeData(DataOutputStream out) throws IOException;

    protected abstract void writeEncryptedData(DataOutputStream out) throws IOException;

    public void writeToStream(DataOutputStream out) throws IOException {
        out.writeByte(getType());
        writeData(out);
    }

    public void writeToEncryptedStream(DataOutputStream out) throws IOException {
        out.writeByte(getType());
        writeEncryptedData(out);
    }

    @Override
    public int compareTo(Hook o) {
        if (this instanceof ConstantHook && o instanceof ConstantHook) {
            return name.compareTo(o.name);
        }

        if (this instanceof ConstantHook) {
            return -1;
        }

        if (this instanceof FieldHook && o instanceof FieldHook) {
            int cmp = ((FieldHook) this).fieldDesc.compareTo(((FieldHook) o).fieldDesc);
            if (cmp == 0) {
                cmp = name.compareTo(o.name);
            }
            return cmp;
        }

        if (this instanceof FieldHook && o instanceof InvokeHook) {
            return 1;
        }

        if (this instanceof FieldHook && o instanceof ConstantHook) {
            return 1;
        }

        if (this instanceof InvokeHook && o instanceof ConstantHook) {
            return 1;
        }

        if (this instanceof InvokeHook && o instanceof FieldHook) {
            return -1;
        }

        return name.compareTo(o.name);
    }

    protected class Type {

        public static final byte FIELD = 0;
        public static final byte INVOKE = 1;
        public static final byte CONSTANT = 2;

    }
}
