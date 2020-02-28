package org.objectweb.casm.commons.cfg.query;

import org.objectweb.casm.tree.AbstractInsnNode;
import org.objectweb.casm.tree.FieldInsnNode;
import org.objectweb.casm.tree.MethodInsnNode;
import org.objectweb.casm.tree.MethodNode;

/**
 * @author Tyler Sedlar
 */
public class MemberQuery extends InsnQuery {

    protected final String owner, name, desc;

    public MemberQuery(int opcode, String owner, String name, String desc) {
        super(opcode);
        this.owner = owner;
        this.name = name;
        this.desc = desc;
    }

    public MemberQuery(int opcode, MethodNode mn) {
        this(opcode, mn.owner.name, mn.name, mn.desc);
    }

    public MemberQuery(int opcode, String owner, String desc) {
        this(opcode, owner, null, desc);
    }

    public MemberQuery(int opcode, String desc) {
        this(opcode, null, desc);
    }

    public MemberQuery(String desc) {
        this(-1, desc);
    }

    public MemberQuery(int opcode) {
        this(opcode, null, null, null);
    }

    @Override
    public boolean matches(AbstractInsnNode ain) {
        if (!(ain instanceof FieldInsnNode) && !(ain instanceof MethodInsnNode)) return false;
        int opcode = ain.opcode();
        String owner, name, desc;
        if (ain instanceof FieldInsnNode) {
            FieldInsnNode fin = (FieldInsnNode) ain;
            owner = fin.owner;
            name = fin.name;
            desc = fin.desc;
        } else {
            MethodInsnNode min = (MethodInsnNode) ain;
            owner = min.owner;
            name = min.name;
            desc = min.desc;
        }
        if (this.opcode == -1 || this.opcode == opcode) {
            if (this.owner == null || this.owner.equals(owner)) {
                if (this.name == null || this.name.equals(name)) {
                    return this.desc == null || this.desc.equals(desc) || desc.matches(this.desc);
                }
            }
        }
        return false;
    }
}
