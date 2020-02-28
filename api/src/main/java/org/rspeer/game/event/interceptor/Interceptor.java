package org.rspeer.game.event.interceptor;

import org.rspeer.game.api.Game;
import org.rspeer.game.event.interceptor.impl.Proxy;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public abstract class Interceptor implements Opcodes {

    private static final HashMap<Class, Object> PROXY_CACHE = new HashMap<>();

    protected static <K> K getProxyFor(Class<K> clazz) {
        if (PROXY_CACHE.containsKey(clazz)) {
            return (K) PROXY_CACHE.get(clazz);
        }
        if (!clazz.isInterface()) {
            throw new IllegalArgumentException(clazz + " is not an interface");
        }
        Proxy acc = clazz.getAnnotation(Proxy.class);
        if (acc == null) {
            throw new IllegalArgumentException(clazz + " is not a proxy");
        }

        String hook = acc.value();
        final String implementation = clazz.getName().replace(".", "/") + "$" + System.identityHashCode(Game.getClient().getClass().getClassLoader());
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        MethodVisitor mv;

        cw.visit(V1_6, ACC_PUBLIC | ACC_SUPER, implementation, null, "java/lang/Object", new String[]{clazz.getName().replace(".", "/")});
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        for (Method call : clazz.getDeclaredMethods()) {
            if (!Modifier.isAbstract(call.getModifiers()))
                continue;
            String name = call.getName();
            String descriptor = Type.getMethodDescriptor(call);
            mv = cw.visitMethod(ACC_PUBLIC, name, descriptor, null, null);
            if (call.getParameterTypes().length > 0) {
                Type[] args = Type.getArgumentTypes(call);
                for (int idx = 0; idx != args.length; idx++) {
                    Type arg = args[idx];
                    mv.visitVarInsn(arg.getOpcode(ILOAD), idx + 1);
                }
            }
            mv.visitMethodInsn(INVOKESTATIC, hook, name, descriptor, false);
            mv.visitInsn(Type.getReturnType(call).getOpcode(IRETURN));
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        cw.visitEnd();
        byte[] raw = cw.toByteArray();

        try {
            Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            defineClass.setAccessible(true);
            Object impl = ((Class) defineClass.invoke(Game.getClient().getClass().getClassLoader(), implementation.replace("/", "."), raw, 0, raw.length)).newInstance();
            defineClass.setAccessible(false);
            PROXY_CACHE.put(clazz, impl);
            return (K) impl;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
