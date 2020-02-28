package org.rspeer.game.api.action.tree;

import org.rspeer.game.api.action.ActionOpcodes;
import org.rspeer.game.providers.RSMenuItem;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.rspeer.game.api.action.ActionOpcodes.*;

public abstract class Action {

    private static final Map<Integer, String> OPCODE_NAME_MAPPINGS = new LinkedHashMap<>();

    static {
        try {
            for (Field field : ActionOpcodes.class.getDeclaredFields()) {
                if (field.getType() == int.class) {
                    int value = field.getInt(null);
                    OPCODE_NAME_MAPPINGS.put(value, field.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected final int opcode;
    protected final long primary;
    protected final int secondary, tertiary;

    protected Action(int opcode, long primary, int secondary, int tertiary) {
        this.opcode = opcode;
        this.primary = primary;
        this.secondary = secondary;
        this.tertiary = tertiary;
    }

    public static Action valueOf(int op, long primary, int secondary, int tertiary) {
        if (op >= 2000) {
            op -= 2000;
        }

        if (op >= USE_ON_NPC && op <= OP_NPC5 || op == EXAMINE_NPC) {
            return new NpcAction(op, primary);
        } else if (op >= OP_PLAYER1 && op <= OP_PLAYER10 || op == USE_ON_PLAYER) {
            return new PlayerAction(op, primary);
        } else if ((op >= USE_ON_OBJ && op <= OP_OBJ4) || op == EXAMINE_OBJ || op == OP_OBJ5) {
            return new ObjectAction(op, primary, secondary, tertiary);
        } else if (op == CANCEL) {
            return new CancelAction();
        } else if (op == WALK) {
            return new WalkAction(secondary, tertiary);
        } else if (op == USE_ON_GROUND) {
            return new UseOnGroundAction(secondary, tertiary);
        } else if (op >= OP_PICKABLE1 && op <= OP_PICKABLE5) {
            return new GroundItemAction(op, (int) primary, secondary, tertiary);
        }

        switch (op) {
            case OP_COMPONENT1:
            case OP_COMPONENT2: {
                return new IndexedComponentAction((int) (primary + 1), secondary, tertiary);
            }

            case USE_ON_COMPONENT:
            case USE_ON_BUTTON: {
                return new UseOnComponentAction(op == USE_ON_BUTTON, secondary, tertiary);
            }

            case OP_BUTTON: {
                return new ButtonAction(secondary, tertiary);
            }
        }

        return null;
    }

    public static Action valueOf(RSMenuItem action) {
        return valueOf(action.getOpcode(), action.getArg0(), action.getArg1(), action.getArg2());
    }

    public String getOpcodeName() {
        return OPCODE_NAME_MAPPINGS.getOrDefault(getOpcode(), "OPCODE_" + getOpcode());
    }

    public int getOpcode() {
        return opcode;
    }

    public long getPrimary() {
        return primary;
    }

    public int getSecondary() {
        return secondary;
    }

    public int getTertiary() {
        return tertiary;
    }

    @Override
    public String toString() {
        return String.format("%s<%s>", getClass().getSimpleName(), getOpcodeName());
    }
}
