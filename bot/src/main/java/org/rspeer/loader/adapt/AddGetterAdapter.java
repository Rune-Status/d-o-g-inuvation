package org.rspeer.loader.adapt;

import org.rspeer.injector.CodeAdapter;
import org.rspeer.injector.InjectorFactory;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.injector.hook.FieldHook;
import org.rspeer.injector.hook.Modscript;
import org.objectweb.asm.tree.*;

import java.util.Map;

public final class AddGetterAdapter extends CodeAdapter {

    private int count = 0;

    public AddGetterAdapter(Modscript modscript) {
        super(modscript);
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        for (ClassHook clazz : modscript.classes.values()) {
            ClassNode node = classes.get(clazz.getInternalName());
            for (FieldHook field : clazz.getFields().values()) {
                if (field.getDefinedName().equals("objectDefinitionLoader") || field.getDefinedName().equals("itemDefinitionLoader") || field.getDefinedName().equals("parameterDefinitionLoader")) {
                    node.methods.add(loadDefinitionMethod(field));
                } else {
                    node.methods.add(InjectorFactory.createGetter(modscript, field));
                }
                count++;
            }
        }
    }

    private MethodNode loadDefinitionMethod(FieldHook field) {
        MethodNode mn = new MethodNode(ACC_PUBLIC, field.getterName(), "()L" + PROVIDER_PACKAGE + "RSDefinitionCacheLoader;", null, null);
        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new FieldInsnNode(field.isStatic() ? GETSTATIC : GETFIELD, field.getOwner(), field.getInternalName(), field.getDesc()));
        mn.instructions.add(new InsnNode(ARETURN));
        return mn;
    }

    @Override
    public String verbose() {
        return String.format("Injected %d getters!", count);
    }
}
