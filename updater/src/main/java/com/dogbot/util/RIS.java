/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.dogbot.util;

import org.objectweb.casm.Opcodes;
import org.objectweb.casm.tree.AbstractInsnNode;
import org.objectweb.casm.tree.InsnList;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RIS {
    public static final Map<Integer, String> OPCODE_NAME_MAP;
    private static final Pattern[] NON_INSTRUCTION_CONST_PATTERNS = new Pattern[]{
            Pattern.compile("acc_.+"), Pattern.compile("t_.+"), Pattern.compile("v1_.+")
    };

    static {
        OPCODE_NAME_MAP = new HashMap<>();
        final Class<?> opcodes = Opcodes.class;
        final Field[] declaredFields = opcodes.getDeclaredFields();
        for (final Field field : declaredFields) {
            int modifiers = field.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) &&
                    field.getType() == Integer.TYPE) {
                try {
                    final String name = field.getName().toLowerCase();
                    boolean failed = false;
                    for (final Pattern pattern : NON_INSTRUCTION_CONST_PATTERNS) {
                        final Matcher matcher = pattern.matcher(name);
                        if (matcher.find() && matcher.start() == 0) {
                            failed = true;
                            break;
                        }
                    }
                    if (failed) {
                        continue;
                    }
                    final int constant = field.getInt(null);
                    OPCODE_NAME_MAP.put(constant, name);
                } catch (final IllegalAccessException ex) {
                }
            }
        }
    }

    private InsnList insns;
    private Map<AbstractInsnNode, Integer> instrIndexMap;
    private int currentIndex = 0;
    private String mappedCode;

    public RIS(final InsnList insns) {
        this.insns = insns;
        reload();
    }

    public static Pattern mkPattern(int... opcodes) {
        StringBuilder pattern = new StringBuilder();
        String cur;
        for (int i = 0; i < opcodes.length; i++) {
            cur = OPCODE_NAME_MAP.get(opcodes[i]);
            if (cur == null) throw new Error("Operation does not exist: " + opcodes[i]);
            pattern.append(cur);
            if (i != opcodes.length - 1) pattern.append(' ');
        }
        return Pattern.compile(pattern.toString().toLowerCase());
    }

    @SuppressWarnings("unchecked")
    public void reload() {
        StringBuffer buffer = new StringBuffer();
        instrIndexMap = new HashMap<>();
        Iterator<AbstractInsnNode> iterator = insns.iterator();
        while (iterator.hasNext()) {
            AbstractInsnNode insn = iterator.next();
            if (insn.opcode() < 0) {
                continue;
            }
            instrIndexMap.put(insn, buffer.length());
            buffer.append(OPCODE_NAME_MAP.get(insn.opcode())).append(" ");
        }
        mappedCode = buffer.toString();
    }

    private AbstractInsnNode getKey(final Integer val) {
        for (Map.Entry<AbstractInsnNode, Integer> entry : instrIndexMap.entrySet()) {
            if (entry.getValue().equals(val)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private AbstractInsnNode[] getMatchFromRange(int start, int end) {
        AbstractInsnNode startInsn = getKey(start);
        int realEndIdx = -1;
        for (int x = end - 1; x >= start; --x) {
            if (mappedCode.charAt(x) == ' ') {
                realEndIdx = x + 1;
                break;
            }
        }
        AbstractInsnNode endInsn = getKey(realEndIdx);
        int startInsnIdx = 0;
        if (startInsn != null)
            startInsnIdx = insns.indexOf(startInsn);
        AbstractInsnNode[] match = new AbstractInsnNode[insns.indexOf(endInsn) - startInsnIdx + 1];
        for (int idx = 0; idx < match.length; ++idx) {
            match[idx] = insns.get(startInsnIdx + idx);
        }
        return match;
    }

    public int getCurrentIndex() {
        return this.currentIndex;
    }

    public List<AbstractInsnNode[]> search(final String pattern, final AbstractInsnNode from) {
        return search(Pattern.compile(pattern.toLowerCase()), from, null);
    }

    public List<AbstractInsnNode[]> search(final String pattern, final Constraint constraint) {
        return search(Pattern.compile(pattern.toLowerCase()), insns.getFirst(), constraint);
    }

    public List<AbstractInsnNode[]> search(final String pattern) {
        return search(Pattern.compile(pattern.toLowerCase()), insns.getFirst());
    }

    public List<AbstractInsnNode[]> search(final Pattern pattern, final AbstractInsnNode from) {
        return search(pattern, from, null);
    }

    public List<AbstractInsnNode[]> search(Pattern pattern, Constraint constraint) {
        return search(pattern, insns.getFirst(), constraint);
    }

    public List<AbstractInsnNode[]> search(Pattern pattern) {
        return search(pattern, insns.getFirst());
    }

    public List<AbstractInsnNode[]> search(Pattern pattern, AbstractInsnNode from, Constraint constraint) {
        Matcher matcher = pattern.matcher(mappedCode);
        Integer ret = instrIndexMap.get(from);
        int startIdx = 0;
        if (ret != null)
            startIdx = ret.intValue();
        List<AbstractInsnNode[]> matches = new LinkedList<AbstractInsnNode[]>();
        while (matcher.find(startIdx)) {
            int start = matcher.start();
            int end = matcher.end();
            AbstractInsnNode[] match = getMatchFromRange(start, end);
            if (constraint == null || constraint.accept(match)) {
                matches.add(match);
            }
            startIdx = end;
        }
        currentIndex = startIdx;
        return matches;
    }

    public interface Constraint {
        boolean accept(final AbstractInsnNode[] match);
    }
}
