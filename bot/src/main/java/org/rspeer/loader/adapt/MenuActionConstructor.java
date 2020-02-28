package org.rspeer.loader.adapt;

import org.rspeer.game.providers.RSMenuItem;
import org.rspeer.injector.CodeAdapter;
import org.rspeer.injector.hook.Modscript;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Map;

public final class MenuActionConstructor extends CodeAdapter {

    public MenuActionConstructor(Modscript modscript) {
        super(modscript);
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        ClassNode client = classes.get("client");
        String menuClass = modscript.resolve(RSMenuItem.class).getInternalName();

        MethodNode methodNode = new MethodNode(Opcodes.ACC_PUBLIC, "createMenuItem", "(IIJII)L" + PROVIDER_PACKAGE + "RSMenuItem;", null, null);

        methodNode.instructions.add(new TypeInsnNode(Opcodes.NEW, menuClass));
        methodNode.instructions.add(new InsnNode(Opcodes.DUP));
        methodNode.instructions.add(new LdcInsnNode(""));
        methodNode.instructions.add(new LdcInsnNode(""));
        methodNode.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
        methodNode.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
        methodNode.instructions.add(new InsnNode(Opcodes.ICONST_M1));
        methodNode.instructions.add(new VarInsnNode(Opcodes.LLOAD, 3));
        methodNode.instructions.add(new VarInsnNode(Opcodes.ILOAD, 5));
        methodNode.instructions.add(new VarInsnNode(Opcodes.ILOAD, 6));
        methodNode.instructions.add(new InsnNode(Opcodes.ICONST_0));
        methodNode.instructions.add(new InsnNode(Opcodes.ICONST_0));
        methodNode.instructions.add(new InsnNode(Opcodes.LCONST_0));
        methodNode.instructions.add(new InsnNode(Opcodes.ICONST_0));
        methodNode.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, menuClass, "<init>", "(Ljava/lang/String;Ljava/lang/String;IIIJIIZZJZ)V", false));
        methodNode.instructions.add(new InsnNode(Opcodes.ARETURN));

        client.methods.add(methodNode);
    }

    @Override
    public String verbose() {
        return "Injected RSMenuItem instantiator!";
    }
}
