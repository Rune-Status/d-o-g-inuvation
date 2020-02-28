/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.dogbot.util.io.printer;

import com.dogbot.Updater;
import com.dogbot.hookspec.hook.FieldHook;
import com.dogbot.hookspec.hook.Hook;
import com.dogbot.hookspec.hook.InvokeHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;

import java.math.BigInteger;
import java.util.*;

/**
 * @author Dogerina
 * @since 07-08-2015
 * This printer produces an output that is somewhat understandable, while at the same time it is easy to be parsed
 * and sufficient for reflection and basic injection
 */
public class FlatHookPrinter extends HookPrinter {

    public FlatHookPrinter(Updater updater) {
        super(updater);
    }

    @Override
    public void writeTo(StringBuilder builder) {
        Set<GraphVisitor> visitors = new TreeSet<>((g1, g2) -> g1.id().compareTo(g2.id()));
        Collections.addAll(visitors, updater.visitors);
        int totalClasses = 0;
        int classes = 0;
        int totalHooks = 0;
        int hooks = 0;
        int methodHooks = 0;
        for (GraphVisitor gv : visitors) {
            totalClasses++;
            if (gv.cn == null) {
                appendLine(builder, gv.id() + " null");
                continue;
            }
            appendLine(builder, "i " + gv.id() + " " + gv.cn.name);
            if (gv.cn == null) {
                continue;
            }
            classes++;
            hooks += gv.hooks.size();
            for (Hook hook : gv.hooks.values()) {
                if (hook instanceof InvokeHook) {
                    methodHooks++;
                }
            }
            VisitorInfo info = gv.getClass().getAnnotation(VisitorInfo.class);
            if (info == null) {
                continue;
            }
            List<Hook> hookies = new ArrayList<>();
            hookies.addAll(gv.hooks.values());
            hookies.sort((o1, o2) -> o1.name.compareTo(o2.name));

            totalHooks += info.hooks().length;
            for (Hook hook : hookies) {
                if (hook instanceof FieldHook) {
                    FieldHook fh = (FieldHook) hook;
                    if (fh.fieldDesc.equals("I") || fh.fieldDesc.equals("J")) {
                        if (fh.multiplier == 0) {
                            BigInteger bigInt = updater.inverseVisitor.getDecoder(fh.clazz, fh.field, fh.fieldDesc.equals("Z"));
                            if (bigInt != null) {
                                fh.multiplier = bigInt.longValue();
                            }
                        }
                    }
                    if (!fh.isStatic) {
                        fh.clazz = gv.cn.name;
                    }
                    appendLine(builder, "g " + gv.id() + "." + fh.name + " " + fh.field + " " + fh.fieldDesc + " " + fh.multiplier + " " + fh.isStatic);
                } else if (hook instanceof InvokeHook) {
                    InvokeHook ih = (InvokeHook) hook;
                /*    OpaquePredicateVisitor.OpaquePredicate predicate = updater.opaquePredicateVisitor.get(ih.clazz + "." + ih.method + ih.desc);
                    if (predicate != null) {
                        ih.setOpaquePredicate(predicate.predicate, predicate.predicateType);
                    }*/
                    //+ " " + ih.predicate + " "
                    appendLine(builder, "i " + gv.id() + "." + ih.name + " " + ih.method + " " + ih.desc);
                }
            }
        }
        appendLine(builder, String.format("Identified %d/%d classes", classes, totalClasses));
        appendLine(builder, String.format("Identified %d/%d hooks, %d of which were methods", hooks, totalHooks, methodHooks));
    }

    private void appendLine(StringBuilder builder, String value) {
        builder.append(value).append("\n");
    }
}
