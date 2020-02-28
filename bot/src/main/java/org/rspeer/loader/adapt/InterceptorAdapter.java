package org.rspeer.loader.adapt;

import org.rspeer.game.event.interceptor.impl.Intercept;
import org.rspeer.game.event.interceptor.impl.InterceptType;
import org.rspeer.injector.CodeAdapter;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.injector.hook.Modscript;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class InterceptorAdapter extends CodeAdapter {

    private Class<?> interceptor;

    public InterceptorAdapter(Modscript modscript, Class<?> interceptor) {
        super(modscript);
        this.interceptor = interceptor;
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        List<InterceptedMethod> methods = new ArrayList<>();
        for (Method method : interceptor.getDeclaredMethods()) {
            Intercept annotation = method.getAnnotation(Intercept.class);
            if (annotation != null) {
                methods.add(new InterceptedMethod(method, annotation, annotation.owner()));
            }
        }

        for (ClassNode node : classes.values()) {
            for (MethodNode mn : node.methods) {
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain instanceof MethodInsnNode) {
                        MethodInsnNode min = (MethodInsnNode) ain;
                        InterceptedMethod method = methods.stream().filter(e -> e.isMethod(min)).findFirst().orElse(null);
                        if (method != null) {
                            String desc = min.desc;
                            if (method.intercept.type() == InterceptType.INSTANCE) {
                                desc = "(Ljava/lang/Object;" + desc.substring(1);
                            }
                            mn.instructions.set(min, new MethodInsnNode(INVOKESTATIC, interceptor.getName().replace(".", "/"), min.name, desc, false));
                        }
                    }
                }
            }
        }
    }

    @Override
    public String verbose() {
        return "Injecting intercepts";
    }

    private class InterceptedMethod {
        Method method;
        Intercept intercept;
        String owner;

        InterceptedMethod(Method method, Intercept intercept, String owner) {
            this.method = method;
            this.intercept = intercept;
            this.owner = owner;
        }

        public boolean isMethod(MethodInsnNode min) {
            if (owner.equalsIgnoreCase(min.owner)) {
                return method.getName().equalsIgnoreCase(min.name);
            }
            return false;
        }
    }
}
