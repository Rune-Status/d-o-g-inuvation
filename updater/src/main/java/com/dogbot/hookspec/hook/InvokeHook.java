package com.dogbot.hookspec.hook;

import com.dogbot.Updater;
import com.dogbot.hookspec.Crypto;
import com.dogbot.visitor.GraphVisitor;
import org.objectweb.casm.Opcodes;
import org.objectweb.casm.tree.MethodInsnNode;
import org.objectweb.casm.tree.MethodNode;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Tyler Sedlar, Dogerina
 */
public class InvokeHook extends Hook {

    public String clazz, method, desc, expectedDesc;
    public boolean isStatic;

    public InvokeHook(String name, String clazz, String method, String desc, String expectedDesc, boolean isStatic) {
        super(name);
        this.clazz = clazz;
        this.method = method;
        this.desc = desc;
        this.expectedDesc = expectedDesc;
        this.isStatic = isStatic;
    }

    public InvokeHook(String name, MethodNode mn, String expectedDesc) {
        this(name, mn.owner.name, mn.name, mn.desc, expectedDesc, (mn.access & Opcodes.ACC_STATIC) != 0);
    }

    public InvokeHook(String name, MethodInsnNode min, String expectedDesc) {
        this(name, min.owner, min.name, min.desc, expectedDesc, min.opcode() == Opcodes.INVOKESTATIC);
    }

    @Override
    public byte getType() {
        return Type.INVOKE;
    }

    @Override
    public String getOutput(Updater updater) {
        String desc0 = org.objectweb.casm.Type.getType(desc).getReturnType().getClassName();
        org.objectweb.casm.Type[] args = org.objectweb.casm.Type.getArgumentTypes(desc);
        String params = "(";
        int i = 0;
        for (org.objectweb.casm.Type arg : args) {
            String ok = arg.getClassName();
            if (ok.lastIndexOf('.') != -1) {
                ok = ok.substring(ok.lastIndexOf('.') + 1);
            }
            params += ok;
            if (++i != args.length) {
                params += ", ";
            }
        }
        String descType = desc0.replace("[", "").replace("]", "");
        GraphVisitor v = updater.visitorForClass(descType);
        if (v != null) {
            desc0 = desc0.replace(descType, v.id());
        }
        if (desc0.contains(".")) {
            desc0 = desc0.substring(desc0.lastIndexOf('.') + 1);
        }
        params += ")";
        return "¤ " + desc0 + " " + name + " is " + clazz + "." + method + params;
    }

    @Override
    protected void writeData(DataOutputStream out) throws IOException {
        out.writeUTF(name);
        out.writeUTF(clazz);
        out.writeUTF(method);
        out.writeUTF(desc);
        out.writeUTF(expectedDesc);
        out.writeBoolean(isStatic);
        //out.writeInt(predicate);
        //out.writeUTF(predicateType == int.class ? "I" : (predicateType == byte.class ? "B" : "S"));
    }

    @Override
    protected void writeEncryptedData(DataOutputStream out) throws IOException {
        out.writeUTF(Crypto.encrypt(name));
        out.writeUTF(Crypto.encrypt(clazz));
        out.writeUTF(Crypto.encrypt(method));
        out.writeUTF(Crypto.encrypt(desc));
        out.writeUTF(Crypto.encrypt(expectedDesc));
        out.writeBoolean(isStatic);
        //out.writeInt(predicate);
        //out.writeUTF(Crypto.encrypt(predicateType == int.class ? "I" : (predicateType == byte.class ? "B" : "S")));
    }

    public String key() {
        return clazz + "." + method + desc;
    }
}