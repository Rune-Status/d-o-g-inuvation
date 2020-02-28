package org.rspeer.injector.hook;

import org.rspeer.io.Crypto;
import org.objectweb.asm.tree.MethodNode;

import java.io.DataInputStream;
import java.io.IOException;

public final class MethodHook extends Hook {

    private ClassHook parent;
    private String name, owner, internalName, desc, expectedDesc;
    private boolean isStatic;

    @Override
    protected void read(DataInputStream in) throws IOException {
        name = Crypto.decrypt(in.readUTF());
        owner = Crypto.decrypt(in.readUTF());
        internalName = Crypto.decrypt(in.readUTF());
        desc = Crypto.decrypt(in.readUTF());
        expectedDesc = Crypto.decrypt(in.readUTF());
        isStatic = in.readBoolean();
    }

    public boolean matches(MethodNode mn) {
        return mn.name.equals(internalName) && mn.desc.equals(desc);
    }

    @Override
    public String getDefinedName() {
        return name;
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

    public String getExpectedDesc() {
        return expectedDesc;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public ClassHook getParent() {
        return parent;
    }

    public void setParent(ClassHook parent) {
        this.parent = parent;
    }
}