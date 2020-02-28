package org.rspeer.injector.hook;

import org.rspeer.io.Crypto;
import org.objectweb.asm.tree.FieldInsnNode;

import java.io.DataInputStream;
import java.io.IOException;

public final class FieldHook extends Hook {

    private ClassHook parent;
    private String definedName, owner, internalName, desc;
    private boolean isStatic;
    private boolean longMultiplier;
    private long multiplier;

    public FieldHook(String definedName, String owner, String internalName, String desc, boolean isStatic, boolean longMultiplier, long multiplier) {
        this.definedName = definedName;
        this.owner = owner;
        this.internalName = internalName;
        this.desc = desc;
        this.isStatic = isStatic;
        this.longMultiplier = longMultiplier;
        this.multiplier = multiplier;
    }

    public FieldHook() {
    }

    @Override
    protected void read(DataInputStream in) throws IOException {
        definedName = Crypto.decrypt(in.readUTF());
        owner = Crypto.decrypt(in.readUTF());
        internalName = Crypto.decrypt(in.readUTF());
        desc = Crypto.decrypt(in.readUTF());
        isStatic = in.readBoolean();
        multiplier = (longMultiplier = in.readBoolean()) ? in.readLong() : in.readInt();
    }

    @Override
    public String getDefinedName() {
        return definedName;
    }

    public boolean isLongMultiplier() {
        return longMultiplier;
    }

    public long getMultiplier() {
        return multiplier;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public String getOwner() {
        return owner;
    }

    public String getInternalName() {
        return internalName;
    }

    public String getDesc() {
        return desc;
    }

    public String setterName() {
        return String.format("set%s%s", definedName.toUpperCase().charAt(0), definedName.substring(1));
    }

    public String getterName() {
        return String.format("%s%s%s", desc.equals("Z") ? "is" : "get",
                definedName.toUpperCase().charAt(0),
                definedName.substring(1)
        );
    }

    public String callbackName() {
        return callbackName(null);
    }

    public String callbackName(String owner) {
        if (owner != null) {
            return String.format("notify%s%s%s", String.format("%s%s", owner.toUpperCase().charAt(0), owner.substring(1)), definedName.toUpperCase().charAt(0), definedName.substring(1));
        }
        return String.format("notify%s%s", definedName.toUpperCase().charAt(0), definedName.substring(1));
    }

    public boolean matches(FieldInsnNode fin) {
        return fin.owner.equals(owner)
                && fin.name.equals(internalName)
                && fin.desc.equals(desc);
    }

    public ClassHook getParent() {
        return parent;
    }

    public void setParent(ClassHook parent) {
        this.parent = parent;
    }
}
