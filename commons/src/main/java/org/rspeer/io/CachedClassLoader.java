package org.rspeer.io;

import java.util.HashMap;
import java.util.Map;

public final class CachedClassLoader extends ClassLoader {

    public final Map<String, byte[]> classes;

    public final Map<String, Class<?>> loaded = new HashMap<>();
    public final Map<String, Class<?>> defined = new HashMap<>();

    public CachedClassLoader(Map<String, byte[]> classes) {
        this.classes = classes;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (loaded.containsKey(name)) {
            return loaded.get(name);
        } else if (!classes.containsKey(name)) {
            return super.loadClass(name);
        } else if (defined.containsKey(name)) {
            return defined.get(name);
        }
        byte[] def = classes.get(name);
        Class<?> clazz = super.defineClass(name, def, 0, def.length);
        loaded.put(name, clazz);
        defined.put(name, clazz);
        return clazz;
    }
}
