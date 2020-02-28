package org.rspeer.game.api.component.tab;

import org.rspeer.api.commons.Identifiable;
import org.rspeer.api.commons.Predicates;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.api.ItemTables;
import org.rspeer.game.api.Varps;
import org.rspeer.game.api.component.InterfaceAddress;
import org.rspeer.game.api.component.InterfaceComposite;
import org.rspeer.game.api.component.Interfaces;
import org.rspeer.game.api.component.Item;
import org.rspeer.game.api.query.ItemQueryBuilder;
import org.rspeer.game.api.query.results.ItemQueryResults;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class Summoning {

    private Summoning() {
        throw new IllegalAccessError();
    }

    public static int getMinutesRemaining() {
        return Varps.getBitValue(6055);
    }

    public static int getPoints() {
        return Varps.getBitValue(41524) / 10;
    }

    public static int getMaximumPoints() {
        return Skills.getLevel(Skill.SUMMONING) * 10;
    }

    public static int getSpecialMovePoints() {
        return Varps.getValue(1787);
    }

    public static final class Familiar {

        private static final int TABLE_KEY = ItemTables.FAMILIAR;
        private static final int GROUP_INDEX = InterfaceComposite.BEAST_OF_BURDEN.getGroup();
        private static final int ITEM_CONTAINER_INDEX = 5;

        private static final Pattern RESTORE_PATTERN = Pattern.compile(".*([Rr])estore.*");

        private static final InterfaceAddress ADDRESS = new InterfaceAddress(
                GROUP_INDEX, ITEM_CONTAINER_INDEX
        );

        private static final InterfaceAddress CALL_ADDRESS = new InterfaceAddress(
                () -> Interfaces.getFirst(GROUP_INDEX, a -> a.containsAction("Call Follower"))
        );

        private static final InterfaceAddress TAKE_BOB_ADDRESS = new InterfaceAddress(
                () -> Interfaces.getFirst(GROUP_INDEX, a -> a.containsAction("Take BoB"))
        );

        private static final InterfaceAddress GIVE_BOB_ADDRESS = new InterfaceAddress(
                () -> Interfaces.getFirst(GROUP_INDEX, a -> a.containsAction("Give BoB"))
        );

        private static final InterfaceAddress RENEW_ADDRESS = new InterfaceAddress(
                () -> Interfaces.getFirst(GROUP_INDEX, a -> a.containsAction("Renew Familiar"))
        );

        private static final InterfaceAddress DISMISS_ADDRESS = new InterfaceAddress(
                () -> Interfaces.getFirst(GROUP_INDEX, a -> a.containsAction("Dismiss Now"))
        );

        private static final InterfaceAddress ATTACK_ADDRESS = new InterfaceAddress(
                () -> Interfaces.getFirst(GROUP_INDEX, a -> a.containsAction("Attack"))
        );

        private static final InterfaceAddress SPECIAL_MOVE_ADDRESS = new InterfaceAddress(
                () -> Interfaces.getFirst(GROUP_INDEX, a -> a.containsAction(x -> x.contains("Cast")), true)
        );

        private Familiar() {
            throw new IllegalAccessError();
        }

        public static ItemQueryResults getItems(Predicate<Item> predicate) {
            return ItemTables.getItems(TABLE_KEY, ADDRESS, predicate);
        }

        public static ItemQueryResults getItems() {
            return getItems(Predicates.always());
        }

        public static Item getFirst(Predicate<Item> predicate) {
            return ItemTables.getFirst(TABLE_KEY, ADDRESS, predicate);
        }

        public static Item getFirst(int... ids) {
            return getFirst(Identifiable.predicate(ids));
        }

        public static Item getFirst(String... names) {
            return getFirst(Identifiable.predicate(names));
        }

        public static Item getFirst(Pattern... patterns) {
            return getFirst(Identifiable.predicate(patterns));
        }

        public static int getItemCapacity() {
            InterfaceComponent component = ADDRESS.resolve();
            return component == null ? -1 : component.getComponents().length;
        }

        public static int getCount(boolean includeStacks, Predicate<Item> predicate) {
            int count = 0;
            for (Item item : getItems(predicate)) {
                count += includeStacks ? item.getStackSize() : 1;
            }
            return count;
        }

        public static int getCount(Predicate<Item> predicate) {
            return getCount(false, predicate);
        }

        public static int getCount(boolean includeStacks, String... names) {
            return getCount(includeStacks, Identifiable.predicate(names));
        }

        public static int getCount(String... names) {
            return getCount(false, names);
        }

        public static int getCount(boolean includeStacks, Pattern... patterns) {
            return getCount(includeStacks, Identifiable.predicate(patterns));
        }

        public static int getCount(Pattern... patterns) {
            return getCount(false, patterns);
        }

        public static int getCount() {
            return getCount(Predicates.always());
        }

        public static boolean isEmpty() {
            return getCount() == 0;
        }

        public static boolean isFull() {
            return getCount() == getItemCapacity();
        }

        public static boolean isPresent() {
            return Varps.getBitValue(6055) > 0;
        }

        public static boolean summon(Predicate<Item> predicate) {
            if (isPresent()) {
                return false;
            }
            Item restore;
            if (getPoints() < 160 && (restore = Backpack.getFirst(RESTORE_PATTERN)) != null) {
                restore.interact("Drink");
            } else {
                Item pouch = Backpack.getFirst(predicate);
                return pouch != null && pouch.containsAction("Summon") && pouch.interact("Summon");
            }
            return false;
        }

        public static boolean summon(String... names) {
            return summon(Identifiable.predicate(names));
        }

        public static boolean summon(Pattern... patterns) {
            return summon(Identifiable.predicate(patterns));
        }

        public static boolean summon(int... ids) {
            return summon(Identifiable.predicate(ids));
        }

        public static boolean summonFirst() {
            return summon(x -> x.containsAction("Summon"));
        }

        public static boolean call() {
            InterfaceComponent component = CALL_ADDRESS.resolve();
            return component != null && component.interact("Call Familiar");
        }

        public static boolean takeAll() {
            InterfaceComponent component = TAKE_BOB_ADDRESS.resolve();
            return component != null && component.interact("Take BoB");
        }

        public static boolean giveAll() {
            InterfaceComponent component = GIVE_BOB_ADDRESS.resolve();
            return component != null && component.interact("Give BoB");
        }

        public static boolean renew() {
            Item restore;
            if (getPoints() < 160 && (restore = Backpack.getFirst(RESTORE_PATTERN)) != null) {
                restore.interact("Drink");
            } else {
                InterfaceComponent component = RENEW_ADDRESS.resolve();
                return component != null && component.interact("Renew Familiar");
            }
            return false;
        }

        public static boolean dismiss() {
            InterfaceComponent component = DISMISS_ADDRESS.resolve();
            return component != null && component.interact("Dismiss Now");
        }

        public static boolean orderAttack() {
            InterfaceComponent component = ATTACK_ADDRESS.resolve();
            return component != null && component.interact("Attack");
        }

        public static boolean castSpecialMove() {
            InterfaceComponent component = SPECIAL_MOVE_ADDRESS.resolve();
            return component != null && component.interact(x -> x.contains("Cast"));
        }

        public static boolean withdraw(Predicate<Item> predicate) {
            Item item = getFirst(predicate);
            return item != null && item.interact("Withdraw-1");
        }

        public static boolean withdraw(String... names) {
            return withdraw(Identifiable.predicate(names));
        }

        public static boolean withdraw(int... ids) {
            return withdraw(Identifiable.predicate(ids));
        }

        public static boolean withdraw(Pattern... pattern) {
            return withdraw(Identifiable.predicate(pattern));
        }

        public static boolean withdrawAll(Predicate<Item> predicate) {
            Item item = getFirst(predicate);
            return item != null && item.interact("Withdraw-All");
        }

        public static boolean withdrawAll(String name) {
            return withdrawAll(Identifiable.predicate(name));
        }

        public static boolean withdrawAll(int id) {
            return withdrawAll(Identifiable.predicate(id));
        }

        public static boolean contains(Predicate<Item> predicate) {
            return getFirst(predicate) != null;
        }

        public static boolean contains(int... ids) {
            return ItemTables.contains(TABLE_KEY, ids);
        }

        public static boolean containsAll(int... ids) {
            return ItemTables.containsAll(TABLE_KEY, ids);
        }

        public static boolean contains(String... names) {
            return contains(Identifiable.predicate(names));
        }

        public static boolean containsAll(String... names) {
            for (String name : names) {
                if (!contains(name)) {
                    return false;
                }
            }
            return true;
        }

        public static boolean contains(Pattern... patterns) {
            return contains(Identifiable.predicate(patterns));
        }

        public static boolean containsAll(Pattern... patterns) {
            for (Pattern pattern : patterns) {
                if (!contains(pattern)) {
                    return false;
                }
            }
            return true;
        }

        public static ItemQueryBuilder newQuery() {
            return new ItemQueryBuilder(() -> getItems().asList());
        }
    }
}
