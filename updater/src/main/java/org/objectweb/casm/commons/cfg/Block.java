package org.objectweb.casm.commons.cfg;

import org.objectweb.casm.Label;
import org.objectweb.casm.commons.cfg.query.InsnQuery;
import org.objectweb.casm.commons.cfg.tree.NodeTree;
import org.objectweb.casm.commons.cfg.tree.util.TreeBuilder;
import org.objectweb.casm.commons.util.Assembly;
import org.objectweb.casm.tree.AbstractInsnNode;
import org.objectweb.casm.tree.LabelNode;
import org.objectweb.casm.tree.MethodNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * @author Tyler Sedlar
 */
public class Block implements Comparable<Block> {

    public final Label label;
    public final List<AbstractInsnNode> instructions = new LinkedList<>();
    public final List<Block> preds = new ArrayList<>();
    public MethodNode owner = null;
    public Block next, target;

    public Stack<AbstractInsnNode> stack = new Stack<>();

    private NodeTree tree;

    private int index = -1;

    /**
     * Constructs a block for the given label.
     *
     * @param label The label in which to create a block from.
     */
    public Block(Label label) {
        this.label = label;
        this.instructions.add(new LabelNode(label));
    }

    /**
     * Constructs a NodeTree for the current block.
     */
    public NodeTree tree() {
        if (tree != null) return tree;
        return (tree = TreeBuilder.build(this));
    }

    /**
     * Sets this block's index.
     *
     * @param index The index to set.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Checks if the block is empty.
     *
     * @return true if the block is empty, otherwise false.
     */
    public boolean isEmpty() {
        return preds.isEmpty() && instructions.size() <= 1;
    }

    @Override
    public int compareTo(Block block) {
        return index > block.index ? 1 : -1;
    }

    @Override
    public String toString() {
        String s = "Block #" + index + "\r\n";
        for (AbstractInsnNode ain : instructions)
            s += " " + Assembly.toString(ain) + "\r\n";
        return s;
    }

    /**
     * Gets the amount of times the given opcodes has been matched
     *
     * @param opcode The opcode to match
     * @return The amount of times the given opcode has been matched.
     */
    public int count(int opcode) {
        int count = 0;
        for (AbstractInsnNode ain : instructions) {
            if (ain.opcode() == opcode)
                count++;
        }
        return count;
    }

    /**
     * Gets the amount of times the given query has been matched
     *
     * @param query The query to match
     * @return The amount of times the given query has been matched.
     */
    public int count(InsnQuery query) {
        int count = 0;
        for (AbstractInsnNode ain : instructions) {
            if (query.matches(ain))
                count++;
        }
        return count;
    }

    /**
     * Gets the matched instruction at the given index
     *
     * @param opcode The opcode of the instruction to match
     * @param index  The index to match at
     * @return The matched instruction at the given index
     */
    public AbstractInsnNode get(int opcode, int index) {
        int i = 0;
        for (AbstractInsnNode ain : instructions) {
            if (ain.opcode() == opcode) {
                if (i == index)
                    return ain;
                i++;
            }
        }
        return null;
    }

    /**
     * Gets the first matched instruction
     *
     * @param opcode The opcode of the instruction to match
     * @return The first matched instruction
     */
    public AbstractInsnNode get(int opcode) {
        return get(opcode, 0);
    }

    /**
     * Gets the matched instruction at the given index
     *
     * @param query The query to match
     * @param index The index to match at
     * @return The matched instruction at the given index
     */
    public AbstractInsnNode get(InsnQuery query, int index) {
        int i = 0;
        for (AbstractInsnNode ain : instructions) {
            if (query.matches(ain)) {
                if (i == index)
                    return ain;
                i++;
            }
        }
        return null;
    }

    /**
     * Gets the first matched instruction
     *
     * @param query The query to match
     * @return The first matched instruction
     */
    public AbstractInsnNode get(InsnQuery query) {
        return get(query, 0);
    }

    public Block follow() {
        Block block = new Block(null);
        block.instructions.addAll(instructions);
        List<Block> followed = new ArrayList<>();
        Block next = this;
        followed.add(next);
        while (next.next != null) {
            next = next.next;
            if (followed.contains(next)) {
                break;
            }
            followed.add(next);
            for (AbstractInsnNode ain : next.instructions) {
                if (ain instanceof LabelNode) {
                    continue;
                }
                block.instructions.add(ain);
            }
        }
        return block;
    }
}
