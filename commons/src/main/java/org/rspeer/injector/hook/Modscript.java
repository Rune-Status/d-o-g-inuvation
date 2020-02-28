package org.rspeer.injector.hook;

import org.rspeer.game.providers.RSProvider;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public final class Modscript {

    private static final int MAGIC = "WHO LIKES DANK MEMES? BECAUSE I SURE DO!!!!".getBytes().length;

    public final Map<String, ClassHook> classes;

    public Modscript() {
        classes = new HashMap<>();
    }

    public ClassHook resolve(Class<? extends RSProvider> provider) {
        return resolve(provider.getSimpleName().replace("RS", ""));
    }

    public ClassHook resolve(String name) {
        if (classes.get(name) == null) {
            for (ClassHook hook : classes.values()) {
                if (hook.getInternalName().equals(name)) {
                    return hook;
                }
            }
        }
        return classes.get(name);
    }

    public ClassHook getClient() {
        return classes.get("Client");
    }

    public long inverseFor(long value) {
        return BigInteger.valueOf(value).modInverse(new BigInteger(String.valueOf(1L << 64))).longValue();
    }

    public int inverseFor(int value) {
        return BigInteger.valueOf(value).modInverse(new BigInteger(String.valueOf(1L << 32))).intValue();
    }

    public void load(byte[] bytes) {
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes))) {
            int magic = in.readInt();
            if (magic != MAGIC) {
                throw new IOException("Invalid modscript format");
            }
            in.readUTF(); //pack hash TODO re-run updater and re-load modscript if outdated?
            int count = in.readInt();
            for (int i = 0; i < count; i++) {
                boolean valid = in.readBoolean();
                if (valid) {
                    ClassHook clazz = new ClassHook();
                    clazz.read(in);
                    classes.put(clazz.getDefinedName(), clazz);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}