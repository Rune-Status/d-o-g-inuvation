package com.dogbot.visitor.constraint;

import com.dogbot.hookspec.hook.FieldHook;
import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.commons.cfg.BlockVisitor;
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.ArithmeticNode;
import org.objectweb.casm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.casm.commons.cfg.tree.node.NumberNode;

/**
 * Finds the first GETFIELD multiplier for a given hook and assigns the multiplier.
 * Sometimes the multiplier finder fails if a field is hardly used, so this is good for
 * those situations
 */
public final class FirstGetfieldMultiplier extends BlockVisitor {

    private final FieldHook hook;

    public FirstGetfieldMultiplier(FieldHook hook) {
        this.hook = hook;
    }

    @Override
    public boolean validate() {
        return !lock.get();
    }

    @Override
    public void visit(Block block) {
        block.tree().accept(new NodeVisitor() {
            @Override
            public void visitField(FieldMemberNode fmn) {
                if (hook.key().equals(fmn.key()) && fmn.opcode() == GETFIELD
                        && fmn.parent() instanceof ArithmeticNode) {
                    ArithmeticNode expr = (ArithmeticNode) fmn.parent();
                    if (expr.opcode() == IMUL && expr.children() == 2) {
                        NumberNode cst = (NumberNode) expr.layer(LDC);
                        if (cst != null) {
                            hook.multiplier = cst.number();
                            lock.set(true);
                        }
                    }
                }
            }
        });
    }
}