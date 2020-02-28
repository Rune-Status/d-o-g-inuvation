package com.dogbot.hookspec.hook;

import com.dogbot.Updater;
import com.dogbot.hookspec.Crypto;
import com.dogbot.visitor.GraphVisitor;
import org.objectweb.casm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.casm.tree.FieldInsnNode;
import org.objectweb.casm.tree.FieldNode;

import java.io.DataOutputStream;
import java.io.IOException;

import static org.objectweb.casm.Opcodes.*;

/**
 * @author Tyler Sedlar
 */
public class FieldHook extends Hook {

    public String clazz;
    public String field;
    public String fieldDesc;
    public boolean isStatic;

    public long multiplier = 0;

    private boolean hasMultiplier;

    public FieldHook(String name, String clazz, String field, String fieldDesc, boolean isStatic, boolean hasMultiplier) {
        super(name);
        this.clazz = clazz;
        this.field = field;
        this.fieldDesc = fieldDesc;
        this.isStatic = isStatic;
        this.hasMultiplier = hasMultiplier;
    }

    public FieldHook(String name, String clazz, String field, String fieldDesc) {
        this(name, clazz, field, fieldDesc, true);
    }

    public FieldHook(String name, FieldInsnNode fin) {
        this(name, fin.owner, fin.name, fin.desc, fin.opcode() == GETSTATIC || fin.opcode() == PUTSTATIC, true);
    }

    public FieldHook(String name, FieldMemberNode fmn) {
        this(name, fmn.fin());
    }

    public FieldHook(String name, FieldNode fn) {
        this(name, fn.owner.name, fn.name, fn.desc, (fn.access & ACC_STATIC) > 0, true);
    }

    public FieldHook(String name, String clazz, String field, String fieldDesc, boolean hasMult) {
        this(name, clazz, field, fieldDesc, false, hasMult);
    }

    public FieldHook(String name, FieldInsnNode fin, boolean hasMult) {
        this(name, fin.owner, fin.name, fin.desc, fin.opcode() == GETSTATIC || fin.opcode() == PUTSTATIC, hasMult);
    }

    public FieldHook(String name, FieldNode fn, boolean hasMult) {
        this(name, fn.owner.name, fn.name, fn.desc, (fn.access & ACC_STATIC) > 0, hasMult);
    }

    @Override
    public byte getType() {
        return Hook.Type.FIELD;
    }

    @Override
    public String getOutput(Updater updater) {
        StringBuilder output = new StringBuilder();
        String returnDesc = org.objectweb.casm.Type.getType(fieldDesc).getClassName();
        String descType = returnDesc.replace("[", "").replace("]", "");
        GraphVisitor v = updater.visitorForClass(descType);
        if (v != null) {
            returnDesc = returnDesc.replace(descType, v.id());
        }
        if (returnDesc.contains(".")) {
            returnDesc = returnDesc.substring(returnDesc.lastIndexOf('.') + 1);
        }
        output.append("∙ ").append(returnDesc).append(" ").append(name).append(" is ").append(clazz).append('.').append(field);
        if (multiplier != 0 && hasMultiplier) {
            output.append(" * ").append(multiplier);
        }
        return output.toString();
    }

    @Override
    protected void writeData(DataOutputStream out) throws IOException {
        out.writeUTF(name);
        out.writeUTF(clazz);
        out.writeUTF(field);
        out.writeUTF(fieldDesc);
        out.writeBoolean(isStatic);
        boolean is2w = multiplier >= Integer.MAX_VALUE || multiplier <= Integer.MIN_VALUE;
        out.writeBoolean(is2w);
        if (is2w) {
            out.writeLong(!hasMultiplier ? 0 : multiplier);
            return;

        }
        out.writeInt(!hasMultiplier ? 0 : (int) multiplier);
    }

    @Override
    protected void writeEncryptedData(DataOutputStream out) throws IOException {
        out.writeUTF(Crypto.encrypt(name));
        out.writeUTF(Crypto.encrypt(clazz));
        out.writeUTF(Crypto.encrypt(field));
        out.writeUTF(Crypto.encrypt(fieldDesc));
        out.writeBoolean(isStatic);
        boolean is2w = multiplier >= Integer.MAX_VALUE || multiplier <= Integer.MIN_VALUE;
        out.writeBoolean(is2w);
        if (is2w) {
            out.writeLong(!hasMultiplier ? 0 : multiplier);
            return;
        }
        out.writeInt(!hasMultiplier ? 0 : (int) multiplier);
    }

    public String key() {
        return clazz + "." + field;
    }
}
