package org.rspeer.loader.adapt;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.rspeer.api.collections.Pair;
import org.rspeer.game.adapter.Adapter;
import org.rspeer.game.adapter.scene.SceneObject;
import org.rspeer.game.providers.RSProvider;
import org.rspeer.injector.CodeAdapter;
import org.rspeer.injector.InjectorFactory;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.injector.hook.Modscript;

import java.util.Map;

public final class AddWrapperAdapter extends CodeAdapter {

    private final Pair<Class<? extends Adapter>, Class<? extends RSProvider>>[] mappings;
    private int count = 0;

    public AddWrapperAdapter(Modscript modscript,
            Pair<Class<? extends Adapter>,
                    Class<? extends RSProvider>>... mappings) {
        super(modscript);
        this.mappings = mappings;
        count = mappings.length;
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        for (Pair<Class<? extends Adapter>, Class<? extends RSProvider>> mapping : mappings) {
            ClassHook clazz = modscript.resolve(mapping.getRight());
            ClassNode node = classes.get(clazz.getInternalName());

            String fieldDesc = mapping.getLeft().getName().replace('.', '/');
            node.fields.add(new FieldNode(ACC_PRIVATE, "adapter", "L" + fieldDesc + ";", null, null));

            MethodNode getter = InjectorFactory.newMethod("getAdapter", "()L" + fieldDesc + ";");
            getter.instructions.add(new VarInsnNode(ALOAD, 0));
            getter.instructions.add(new FieldInsnNode(GETFIELD, clazz.getInternalName(), "adapter", "L" + fieldDesc + ";"));
            getter.instructions.add(new InsnNode(ARETURN));

            node.methods.add(getter);

            for (MethodNode mn : node.methods) {
                if (mn.name.equals("<init>")) {
                    InsnList list = new InsnList();
                    list.add(new VarInsnNode(ALOAD, 0));
                    list.add(new TypeInsnNode(Opcodes.NEW, fieldDesc));
                    list.add(new InsnNode(DUP));
                    list.add(new VarInsnNode(ALOAD, 0));
                    if (mapping.getLeft() == SceneObject.class) {
                        list.add(new MethodInsnNode(INVOKESPECIAL, fieldDesc, "<init>",
                                "(Lorg/rspeer/game/providers/RSSceneObject;)V", false));
                    } else {
                        list.add(new MethodInsnNode(INVOKESPECIAL, fieldDesc, "<init>", "(L" + mapping.getRight().getName().replace('.', '/') + ";)V", false));
                    }
                    list.add(new FieldInsnNode(PUTFIELD, node.name, "adapter", "L" + fieldDesc + ";"));
                    mn.instructions.insertBefore(mn.instructions.getFirst(), list);
                }
            }
        }
    }

    @Override
    public String verbose() {
        return String.format("Injected %d adapters!", count);
    }
}
