package org.rspeer.loader.adapt;

import org.rspeer.game.providers.RSGrandExchangeOffer;
import org.rspeer.injector.CodeAdapter;
import org.rspeer.injector.InjectorFactory;
import org.rspeer.injector.hook.Modscript;
import org.objectweb.asm.tree.*;

import java.util.List;
import java.util.Map;

public final class GEOfferCallback extends CodeAdapter {

    public GEOfferCallback(Modscript modscript) {
        super(modscript);
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        String internalName = modscript.resolve(RSGrandExchangeOffer.class).getInternalName();
        String definedName = modscript.resolve(RSGrandExchangeOffer.class).getDefinedName();
        for (MethodNode mn : classes.get(internalName).methods) {
            if (mn.name.equals("<init>")) {
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.getOpcode() == RETURN) {
                        InsnList list = new InsnList();
                        InsnList params = new InsnList();
                        params.add(new VarInsnNode(ALOAD, 0));
                        list.add(InjectorFactory.createCallback(params, "onGEOfferChanged", "(L" + PROVIDER_PACKAGE + "RS" + definedName + ";)V"));
                        mn.instructions.insertBefore(ain, list);
                    }
                }
            }
        }
    }

    @Override
    public String verbose() {
        return "Added grand exchange offer callback";
    }
}
