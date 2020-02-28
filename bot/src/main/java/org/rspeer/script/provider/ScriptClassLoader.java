package org.rspeer.script.provider;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public final class ScriptClassLoader extends ClassLoader {

    private final Map<String, byte[]> target;

    public ScriptClassLoader(Map<String, byte[]> target) {
        super(ScriptClassLoader.class.getClassLoader());
        this.target = target;
    }

    private static byte[] toByteArray(ClassNode node) {
        ClassWriter cw = new ClassWriter(0);
        node.accept(cw);
        return cw.toByteArray();
    }

    private Class<?> loadOrDefine(String name, ClassNode node) {
        Class<?> loaded = findLoadedClass(name);
        if (loaded != null) {
            return loaded;
        }
        try {
            byte[] data = toByteArray(node);
            return data != null && data.length > 0 ? defineClass(name, data, 0, data.length) : null;
        } catch (NoClassDefFoundError e) {

        }
        return null;
    }

    @Override
    public Class<?> loadClass(String name) {
        try {
            name = name.replace("\\.", "/");
            String reflectName = name.replace('/', '.');
            Class<?> clazz = super.findLoadedClass(reflectName);
            if (clazz != null) {
                return clazz;
            }

            byte buffer[] = target.get(name.replace('.', '/'));
            if (buffer != null) {
                try {
                    return defineClass(null, buffer, 0, buffer.length);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ClassReader reader = new ClassReader(buffer);
                ClassNode node = new ClassNode();
                reader.accept(node, 0);
                return loadOrDefine(reflectName, node);
            }

            if ((clazz = super.loadClass(reflectName)) != null) {
                return clazz;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}