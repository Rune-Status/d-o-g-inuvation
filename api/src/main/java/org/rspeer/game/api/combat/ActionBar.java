package org.rspeer.game.api.combat;

import org.rspeer.api.commons.Identifiable;
import org.rspeer.api.commons.Predicates;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.api.VarpComposite;
import org.rspeer.game.api.VarpbitComposite;
import org.rspeer.game.api.Varps;
import org.rspeer.game.api.action.Interactable;
import org.rspeer.game.api.action.tree.Action;
import org.rspeer.game.api.component.InterfaceAddress;
import org.rspeer.game.api.component.InterfaceComposite;
import org.rspeer.game.api.component.Interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class ActionBar {

    private static final int[] GROUPS = {
            InterfaceComposite.MAIN_ACTIONBAR.getGroup(),
            InterfaceComposite.SECOND_ACTIONBAR.getGroup(),
            InterfaceComposite.THIRD_ACTIONBAR.getGroup(),
            InterfaceComposite.FOURTH_ACTIONBAR.getGroup(),
            InterfaceComposite.FIFTH_ACTIONBAR.getGroup(),
            InterfaceComposite.SIXTH_ACTIONBAR.getGroup(),
    };

    private static final InterfaceAddress LOCK_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(GROUPS[0], x -> x.containsAction(y -> y.toLowerCase().contains("lock action bar")))
    );

    private static final int SLOTS_PER_BAR = 14;

    private static final List<Slot> SLOTS = new ArrayList<>(SLOTS_PER_BAR * GROUPS.length);

    private ActionBar() {
        throw new IllegalAccessError();
    }

    public static boolean activate(int id, String action) {
        return activate(Identifiable.predicate(id), action);
    }

    public static boolean activate(String name, String action) {
        return activate(Identifiable.predicate(name), action);
    }

    public static boolean activate(Pattern pattern, String action) {
        return activate(Identifiable.predicate(pattern), action);
    }

    public static boolean activate(Predicate<Slot> predicate, String action) {
        Slot slot = getFirst(predicate);
        if (slot != null) {
            InterfaceComponent component = slot.getComponent();
            if (component != null) {
                return component.interact(action);
            }
        }
        return false;
    }

    public static boolean activate(int... ids) {
        return activate(Identifiable.predicate(ids));
    }

    public static boolean activate(String... names) {
        return activate(Identifiable.predicate(names));
    }

    public static boolean activate(Pattern... patterns) {
        return activate(Identifiable.predicate(patterns));
    }

    public static boolean activate(Predicate<Slot> predicate) {
        Slot slot = getFirst(predicate);
        return slot != null && slot.activate();
    }

    public static Slot getFirst(int... ids) {
        return getFirst(Identifiable.predicate(ids));
    }

    public static Slot getFirst(String... names) {
        return getFirst(Identifiable.predicate(names));
    }

    public static Slot getFirst(Pattern... patterns) {
        return getFirst(Identifiable.predicate(patterns));
    }

    public static Slot getFirst(Predicate<Slot> predicate) {
        for (Slot slot : getSlots()) {
            if (predicate.test(slot)) {
                return slot;
            }
        }
        return null;
    }

    private static Slot[] getSlots() {
        if (SLOTS.isEmpty()) {
            for (int group : GROUPS) {
                InterfaceComponent[] query = Interfaces.getComponents(group, x -> x.containsAction("Customise-keybind"));
                for (InterfaceComponent component : query) {
                    SLOTS.add(new Slot(component.toAddress()));
                }
            }
        }
        return SLOTS.toArray(new Slot[0]);
    }

    public static int getPrimaryIndex() {
        return Varps.getValue(VarpComposite.PRIMARY_ACTION_BAR_NUMBER) >> 5 & 0x7;
    }

    public static int getRevolutionSize() {
        return Varps.getBitValue(VarpbitComposite.REVOLUTION_SLOTS_USED);
    }

    public static boolean isAutoRetaliating() {
        return Varps.getValue(VarpComposite.AUTO_RETALIATE) == 0;
    }

    public static boolean isLocked() {
        return Varps.getBitValue(VarpbitComposite.ACTION_BAR_LOCKED) == 1;
    }

    public static boolean lock(boolean locked) {
        if (locked == isLocked()) {
            return true;
        }
        return LOCK_ADDRESS.filter(InterfaceComponent::isVisible)
                .mapToBoolean(x -> x.interact(Predicates.always()));
    }

    public static final class Slot implements Identifiable, Interactable {

        private static final int MATERIAL_READY = 14659;
        private static final int READY_LEEWAY = 21;
        private final InterfaceAddress address;

        private Slot(InterfaceAddress address) {
            this.address = address;
        }

        public InterfaceComponent getComponent() {
            return address.resolve();
        }

        @Override
        public int getId() {
            InterfaceComponent component = getComponent();
            if (component == null) {
                return -1;
            }
            Type type = getType();
            if (type == Type.EMPTY) {
                return -1;
            }
            return type == Type.ITEM ? component.getItemId() : component.getMaterialId();
        }

        @Override
        public String getName() {
            return address.map(InterfaceComponent::getName);
        }

        public Type getType() {
            InterfaceComponent comp = address.resolve();
            if (comp == null || comp.getName().trim().isEmpty()) {
                return Type.EMPTY;
            }
            return comp.getItemId() != -1 ? Type.ITEM : Type.ABILITY;
        }

        @Override
        public String[] getActions() {
            return address.map(InterfaceComponent::getActions);
        }

        @Override
        public String[] getRawActions() {
            return address.map(InterfaceComponent::getRawActions);
        }

        @Override
        public boolean interact(int opcode) {
            return address.mapToBoolean(x -> x.interact(opcode));
        }

        @Override
        public Action actionOf(String action) {
            return address.map(x -> x.actionOf(action));
        }

        @Override
        public boolean interact(String action) {
            return address.mapToBoolean(x -> x.interact(action));
        }

        //cooled down and activatable
        public boolean isReady() {
            if (isEmpty()) {
                return false;
            }
            InterfaceComponent cmp = getComponent();
            if (cmp == null || cmp.getForeground() != 0xffffff) {
                return false;
            }
            InterfaceComponent next = Interfaces.getComponent(cmp.getGroupIndex(), cmp.getIndex() + 1);
            return next != null && (!next.isVisible() || next.getMaterialId() >= (MATERIAL_READY - READY_LEEWAY));
        }

        //doesnt look at cooldown, just means the slot isnt greyed out
        //(meaning u have enough adren for it)
        public boolean isActivatable() {
            InterfaceComponent cmp = getComponent();
            return cmp != null && cmp.getForeground() == 0xffffff; //white power LOOOOOL
        }

        public boolean isCoolingDown() {
            InterfaceComponent cmp = getComponent();
            return cmp != null && Interfaces.isVisible(cmp.getGroupIndex(), cmp.getIndex() + 1);
        }

        public boolean isEmpty() {
            return getType() == Type.EMPTY;
        }

        public boolean activate() {
            InterfaceComponent component = getComponent();
            if (component != null) {
                String[] actions = component.getActions();
                if (actions.length == 0) {
                    return component.interact(Predicates.always());
                }
                //actionbar slot actions are reversed in the array
                //so interact with last instead of first
                return component.interact(actions[actions.length - 1]);
            }
            return false;
        }

        public enum Type {
            ITEM, ABILITY, EMPTY
        }
    }
}
