package org.rspeer.game.api.action;

import org.rspeer.game.api.Game;
import org.rspeer.game.api.action.tree.Action;
import org.rspeer.game.providers.RSMenuItem;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public final class ActionProcessor {

    private static final Queue<RSMenuItem> queue = new ArrayBlockingQueue<>(2000);
    private static RSMenuItem last;
    private static long lastProcess;

    private ActionProcessor() {
        throw new IllegalAccessError();
    }

    public static void flush() {
        Iterator<RSMenuItem> iterator = queue.iterator();
        while (iterator.hasNext()) {
            RSMenuItem cur = iterator.next();
            boolean fire = true;
            if (last != null && (System.currentTimeMillis() - lastProcess) < 600) {
                if (last.getOpcode() == cur.getOpcode() && last.getArg1() == cur.getArg1() && last.getArg2() == cur.getArg2()) {
                    fire = false;
                }
            }
            if (fire) {
                Game.getClient().processAction(cur, 0, 0, false);
                lastProcess = System.currentTimeMillis();
                last = cur;
            }
            iterator.remove();
        }
    }

    public static void submit(RSMenuItem item) {
        queue.offer(item);
    }

    public static void submit(Action action) {
        if (action != null) {
            submit(Game.getClient().createMenuItem(
                    0,
                    action.getOpcode(),
                    action.getPrimary(),
                    action.getSecondary(),
                    action.getTertiary())
            );
        }
    }
}
