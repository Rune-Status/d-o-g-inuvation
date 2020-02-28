/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.dogbot.visitor.util;

import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.commons.cfg.BlockVisitor;
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.FieldMemberNode;

/**
 * @author Dogerina
 * @since 26-08-2015
 */
public class FieldTreePrinter extends BlockVisitor {

    private final String owner, name;

    public FieldTreePrinter(String owner, String name) {
        this.owner = owner;
        this.name = name;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void visit(Block block) {
        block.tree().accept(new NodeVisitor() {
            @Override
            public void visitField(FieldMemberNode fmn) {
                if (fmn.owner().equals(owner) && fmn.name().equals(name)) {
                    System.out.println("> " + block.owner.key());
                    System.out.println(fmn.tree());
                    System.out.println();
                }
            }
        });
    }
}
