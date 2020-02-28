package org.rspeer.loader.adapt;

import org.objectweb.asm.tree.*;
import org.rspeer.game.providers.RSGrandExchangeOffer;
import org.rspeer.injector.CodeAdapter;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.injector.hook.FieldHook;
import org.rspeer.injector.hook.Modscript;

import java.util.Map;
import java.util.function.Predicate;

public final class GEIndexAdapter extends CodeAdapter {

    private final ClassHook parent;
    private final FieldHook hook;
    private int count = 0;

    public GEIndexAdapter(Modscript modscript) {
        super(modscript);
        parent = modscript.resolve(RSGrandExchangeOffer.class);
        hook = modscript.getClient().getField("grandExchangeOffers");
    }

    private static AbstractInsnNode firstWithin(AbstractInsnNode src,
            Predicate<AbstractInsnNode> predicate,
            int opcode, int dist, boolean next) {
        for (int i = 0; i < dist; i++) {
            src = next ? src.getNext() : src.getPrevious();
            if (src == null) {
                break;
            } else if (src.getOpcode() == opcode && predicate.test(src)) {
                return src;
            }
        }
        return null;
    }

    private static AbstractInsnNode firstWithin(AbstractInsnNode src, int opcode, int dist, boolean next) {
        return firstWithin(src, x -> true, opcode, dist, next);
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        for (ClassNode cn : classes.values()) {
            for (MethodNode mn : cn.methods) {
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain != null && ain.getOpcode() == GETSTATIC) {
                        FieldInsnNode fin = (FieldInsnNode) ain;
                        if (!hook.matches(fin)) {
                            continue;
                        }

                        AbstractInsnNode caret = firstWithin(ain, AASTORE, 9, true);
                        if (caret != null) {
                            AbstractInsnNode idx = firstWithin(ain, ILOAD, 2, true);
                            if (idx == null) {
                                continue;
                            }

                            AbstractInsnNode idx2 = firstWithin(ain, x -> x != idx, ILOAD, 4, true);
                            if (idx2 == null) {
                                continue;
                            }

                            InsnList stack = new InsnList();
                            //grandExchangeOffers[index1][index2].setIndex(index2);
                            stack.add(new FieldInsnNode(GETSTATIC, hook.getOwner(), hook.getInternalName(), hook.getDesc()));
                            stack.add(new VarInsnNode(ILOAD, ((VarInsnNode) idx).var));
                            stack.add(new InsnNode(AALOAD));
                            stack.add(new VarInsnNode(ILOAD, ((VarInsnNode) idx2).var));
                            stack.add(new InsnNode(AALOAD));
                            stack.add(new VarInsnNode(ILOAD, ((VarInsnNode) idx2).var));
                            stack.add(new MethodInsnNode(INVOKEVIRTUAL, parent.getInternalName(), "setIndex", "(I)V", false));

                            mn.instructions.insert(caret, stack);
                            count++;
                        }
                    }
                }
            }
        }
    }

    @Override
    public String verbose() {
        return "Inserted " + count + " grand exchange offer index setters";
    }
}
