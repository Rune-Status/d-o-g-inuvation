package org.rspeer.injector.hook;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Objects;

//Maybe add write methods here and remove from updater?
public abstract class Hook {

    public static Hook readFrom(DataInputStream in) throws IOException {
        int type = in.readByte();
        Class<? extends Hook> clazz = Type.forID(type);
        Hook hook;
        try {
            hook = Objects.requireNonNull(clazz).newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
        hook.read(in);
        return hook;
    }

    protected abstract void read(DataInputStream in) throws IOException;

    public abstract String getDefinedName();

    private enum Type {

        FIELD(ID.FIELD, FieldHook.class),
        INVOKE(ID.INVOKE, MethodHook.class),
        CONSTANT(ID.CONSTANT, ConstantHook.class);

        private final int id;
        private final Class<? extends Hook> clazz;

        Type(int id, Class<? extends Hook> clazz) {
            this.id = id;
            this.clazz = clazz;
        }

        public static Class<? extends Hook> forID(int id) {
            for (Type t : values()) {
                if (t.id == id) {
                    return t.clazz;
                }
            }
            return null;
        }
    }

    private interface ID {
        byte FIELD = 0;
        byte INVOKE = 1;
        byte CONSTANT = 2;
    }
}