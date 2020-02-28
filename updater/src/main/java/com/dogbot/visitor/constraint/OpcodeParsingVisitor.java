package com.dogbot.visitor.constraint;

import com.dogbot.hookspec.hook.FieldHook;
import com.dogbot.visitor.GraphVisitor;
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.AbstractNode;
import org.objectweb.casm.commons.cfg.tree.node.JumpNode;
import org.objectweb.casm.commons.util.Filter;
import org.objectweb.casm.tree.AbstractInsnNode;
import org.objectweb.casm.tree.FieldInsnNode;
import org.objectweb.casm.tree.JumpInsnNode;

import java.util.Map;
import java.util.Objects;

/**
 * easy way to hook definition fields in the method that reads
 * data from the buffer, usually has a fuck ton of if statements
 * comparing opcode and then reading
 * <p>
 * See {@code com.runedroid.oldschool.visitor.config.HitsplatDefinition}
 * for example on usage
 * <p>
 * Does NOT currently work for identifying multiple fields in 1 block
 * So in the example below, it will not be able to identfiy all 3 fields
 * in the opcode 17/18 block, only the first one it sees
 * <p>
 * TODO support identifying multiple fields in 1 block. A map is not necessarily a good
 * data structure to use for this because there can be multiple fields in 1 opcode block
 * <p>
 * Example of said method in HitsplatDefinition class:
 * <code>
 * void decode(Buffer buffer, int opcode) {
 * if (opcode == 1) {
 * fontId = buffer.readSmart32();
 * } else if (opcode == 2) {
 * textColor = buffer.method868();
 * } else if (opcode == 3) {
 * iconId = buffer.readSmart32();
 * } else if (opcode == 4) {
 * leftSpriteId = buffer.readSmart32();
 * } else if (opcode == 5) {
 * middleSpriteId = buffer.readSmart32();
 * } else if (opcode == 6) {
 * rightSpriteId = buffer.readSmart32();
 * } else if (opcode == 7) {
 * offsetX = buffer.readShort();
 * } else if (opcode == 8) {
 * amount = buffer.readPrefixedString();
 * } else if (opcode == 9) {
 * duration = buffer.readUnsignedShort();
 * } else if (opcode == 10) {
 * offsetY = buffer.readShort();
 * } else if (opcode == 11) {
 * fade = 0;
 * } else if (opcode == 12) {
 * comparisonType = buffer.readUnsignedByte();
 * } else if (opcode == 13) {
 * anInt213 = buffer.readShort();
 * } else if (opcode == 14) {
 * fade = buffer.readUnsignedShort();
 * } else if (opcode == 17 || opcode == 18) {
 * varpbitIndex = buffer.readUnsignedShort();
 * if (varpbitIndex == 65535) {
 * varpbitIndex = -1;
 * }
 * <p>
 * varpIndex = buffer.readUnsignedShort();
 * if (varpIndex == 65535) {
 * varpIndex = -1;
 * }
 * <p>
 * int transformId = -1;
 * if (opcode == 18) {
 * transformId = buffer.readUnsignedShort();
 * if (transformId == 65535) {
 * transformId = -1;
 * }
 * }
 * <p>
 * int transformCount = buffer.readUnsignedByte();
 * transformIds = new int[transformCount + 2];
 * <p>
 * for (int i = 0; i <= transformCount; ++i) {
 * transformIds[i] = buffer.readUnsignedShort();
 * if (transformIds[i] == 65535) {
 * transformIds[i] = -1;
 * }
 * }
 * <p>
 * transformIds[transformCount + 1] = transformId;
 * }
 * <p>
 * }
 * </code>
 */
public class OpcodeParsingVisitor extends NodeVisitor {

    private static final Filter<AbstractNode> NUMERIC_PREDICATE = an ->
            an.opcode() == SIPUSH || an.opcode() == BIPUSH
                    || an.opcode() == ICONST_1 || an.opcode() == ICONST_2
                    || an.opcode() == ICONST_3 || an.opcode() == ICONST_4
                    || an.opcode() == ICONST_5;
    private static final boolean DEBUG = false;

    private final GraphVisitor parent;
    /* Opcode, Hook name */
    private final Map<Integer, FieldHook> opcodes;

    public OpcodeParsingVisitor(GraphVisitor parent, Map<Integer, FieldHook> opcodes) {
        this.parent = Objects.requireNonNull(parent);
        this.opcodes = Objects.requireNonNull(opcodes);
    }

    private static FieldInsnNode next(AbstractInsnNode from, int op, String desc, String owner, int skips) {
        int skipped = 0;
        int maxfollow = 100;
        int follow = 0;
        while ((from = from.next()) != null) {
            if (from.opcode() == op) {
                FieldInsnNode topkek = (FieldInsnNode) from;
                if ((desc == null || topkek.desc.equals(desc)) && (owner == null || owner.equals(topkek.owner))) {
                    if (skipped == skips) {
                        return topkek;
                    }
                    skipped++;
                }
            } else if (from.opcode() == GOTO && follow < maxfollow) {
                from = ((JumpInsnNode) from).label.next();
                follow++;
            }
        }
        return null;
    }

    @Override
    public boolean validate() {
        for (FieldHook hook : opcodes.values()) {
            if (!parent.hooks.containsKey(hook.name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void visitJump(JumpNode jn) {
        //search for an if statement comparing a local var to a bipush/sipush
        if (jn.children() == 2 && jn.hasChild(ILOAD) && jn.opcode() != GOTO
                && jn.hasChild(NUMERIC_PREDICATE)) {
            int value = jn.firstNumber().number();
            for (Map.Entry<Integer, FieldHook> opcode : opcodes.entrySet()) {
                if (value == opcode.getKey()) {
                    FieldHook fh = opcode.getValue();
                    FieldInsnNode fin = next(jn.insn(), PUTFIELD, fh.fieldDesc, parent.cn.name, 0);
                    if (fin != null) {
                        if (DEBUG) {
                            System.out.println("Opcode: " + value + ", " + fin.owner + "." + fin.name);
                        }
                        fh.clazz = parent.cn.name;
                        fh.field = fin.name;
                        parent.addHook(fh);
                    }
                }
            }
        }
    }
}
