package org.rspeer.loader.adapt;

import org.rspeer.game.providers.RSBuffer;
import org.rspeer.game.providers.RSObjectDefinition;
import org.rspeer.injector.CodeAdapter;
import org.rspeer.injector.hook.Modscript;
import org.objectweb.asm.tree.*;

import java.util.List;
import java.util.Map;

public final class ObjectImpassableAdapter extends CodeAdapter {

    private int count = 0;

    public ObjectImpassableAdapter(Modscript modscript) {
        super(modscript);
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        String buffer = modscript.resolve(RSBuffer.class).getInternalName();
        ClassNode definition = classes.get(modscript.resolve(RSObjectDefinition.class).getInternalName());

        definition.fields.add(new FieldNode(ACC_PUBLIC, "impassable", "Z", null, null));
        definition.methods.add(makeBooleanGetter(definition.name, "isImpassable", "impassable", "Z"));

        for (MethodNode mn : definition.methods) {
            if (mn.desc.startsWith("(L" + buffer + ";I") && (mn.access & ACC_STATIC) == 0) {
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.getOpcode() == RETURN) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ILOAD, 2));
                        list.add(new IntInsnNode(BIPUSH, 18));
                        LabelNode nodeToJump = new LabelNode();
                        list.add(new JumpInsnNode(IF_ICMPNE, nodeToJump));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new InsnNode(ICONST_1));
                        list.add(new FieldInsnNode(PUTFIELD, definition.name, "impassable", "Z"));
                        list.add(nodeToJump);

                        mn.instructions.insertBefore(ain, list);
                        count++;
                    }
                }
            }
        }
    }

    private MethodNode makeBooleanGetter(String owner, String name, String field, String desc) {
        MethodNode mn = new MethodNode(ACC_PUBLIC, name, "()" + desc, null, null);
        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new FieldInsnNode(GETFIELD, owner, field, desc));
        mn.instructions.add(new InsnNode(IRETURN));
        mn.visitMaxs(1, 1);
        mn.visitEnd();
        return mn;
    }

    @Override
    public String verbose() {
        return String.format("Injected ObjectDefinition.impassable setter into %d methods", count);
    }
}
