/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.dogbot.util.io.printer;

import com.dogbot.Updater;

/**
 * @author Dogerina
 * @since 06-08-2015
 */
public abstract class HookPrinter {

    protected final Updater updater;

    public HookPrinter(Updater updater) {
        this.updater = updater;
    }

    public abstract void writeTo(StringBuilder builder);
}
