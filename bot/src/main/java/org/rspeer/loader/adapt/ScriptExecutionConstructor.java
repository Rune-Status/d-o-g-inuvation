package org.rspeer.loader.adapt;

import org.rspeer.game.providers.RSScriptContext;
import org.rspeer.injector.CodeAdapter;
import org.rspeer.injector.hook.Modscript;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Map;

public final class ScriptExecutionConstructor extends CodeAdapter {

    public ScriptExecutionConstructor(Modscript modscript) {
        super(modscript);
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        ClassNode client = classes.get("client");
        String scriptContext = modscript.resolve(RSScriptContext.class).getInternalName();

        MethodNode methodNode = new MethodNode(Opcodes.ACC_PUBLIC, "createScriptContext", "()L" + PROVIDER_PACKAGE + "RSScriptContext;", null, null);

        methodNode.instructions.add(new TypeInsnNode(Opcodes.NEW, scriptContext));
        methodNode.instructions.add(new InsnNode(Opcodes.DUP));
        methodNode.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, scriptContext, "<init>", "()V", false));
        methodNode.instructions.add(new InsnNode(Opcodes.ARETURN));

        client.methods.add(methodNode);
    }

    @Override
    public String verbose() {
        return "Added script execution constructor";
    }
}
