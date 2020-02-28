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
import org.objectweb.casm.Opcodes;

import java.math.BigInteger;
import java.util.*;

/**
 * @author Dogerina
 * @since 06-08-2015
 * This printer produces an output that is pretty and readable. Do not use this output for injection, instead use the Modscript.
 */
public class DefaultHookPrinter extends HookPrinter {

    public DefaultHookPrinter(Updater updater) {
        super(updater);
    }

    @Override
    public void writeTo(StringBuilder builder) {
        Set<GraphVisitor> visitors = new TreeSet<>(Comparator.comparing(GraphVisitor::id));
        Collections.addAll(visitors, updater.visitors);
        int totalClasses = 0;
        int classes = 0;
        int totalHooks = 0;
        int hooks = 0;
        int methodHooks = 0;
        for (GraphVisitor gv : visitors) {
            totalClasses++;
            if (gv.cn == null) {
                appendLine(builder, "\t^ " + gv.id() + " is BROKEN");
                appendLine(builder, "");
                continue;
            }
            boolean isInterface = (gv.cn.access & Opcodes.ACC_INTERFACE) > 0;
            appendLine(builder, "\t^ " + gv.cn.name + (isInterface ? " extends " : " implements ") + gv.id());
            if (gv.cn == null) {
                appendLine(builder, "");
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
                appendLine(builder, "");
                continue;
            }
            List<Hook> hookies = new ArrayList<>(gv.hooks.values());
            hookies.sort(Hook::compareTo);

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
                }
                appendLine(builder, "\t" + hook.getOutput(updater));
            }
            appendLine(builder, "");
        }
        for (GraphVisitor gv : visitors) {
            VisitorInfo vi = gv.getClass().getAnnotation(VisitorInfo.class);
            if (vi != null) {
                for (String s : gv.hooks.keySet()) {
                    if (!Arrays.asList(vi.hooks()).contains(s)) {
                        System.out.println(gv.getClass().getSimpleName() + " does not contain VisitorInfo annotation --> " + s);
                    }
                }
                for (String hook : vi.hooks()) {
                    if (!gv.hooks.containsKey(hook)) {
                        appendLine(builder, "\t! BROKEN: " + gv.id() + "#" + hook);
                    }
                }
            }
        }
        appendLine(builder, "");
        appendLine(builder, String.format("\tIdentified %d/%d classes", classes, totalClasses));
        appendLine(builder, String.format("\tIdentified %d/%d hooks, %d of which were methods", hooks, totalHooks, methodHooks));
    }

    private void appendLine(StringBuilder builder, String value) {
        builder.append(value).append("\n");
    }
}
