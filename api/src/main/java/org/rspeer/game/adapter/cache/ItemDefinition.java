package org.rspeer.game.adapter.cache;

import org.rspeer.api.commons.Functions;
import org.rspeer.game.adapter.Adapter;
import org.rspeer.game.providers.*;

import java.util.function.Predicate;

public final class ItemDefinition extends Adapter<RSItemDefinition> {

    public ItemDefinition(RSItemDefinition provider) {
        super(provider);
    }

    public int getId() {
        return provider.getId();
    }

    public RSDefinitionLoader getLoader() {
        return provider.getLoader();
    }

    public String getName() {
        return Functions.mapOrDefault(() -> provider, RSItemDefinition::getName, "");
    }

    public RSNodeTable getParameters() {
        return provider.getParameters();
    }

    public String[] getActions() {
        return Functions.mapOrDefault(() -> provider, RSItemDefinition::getActions, new String[0]);
    }

    public String[] getGroundActions() {
        return Functions.mapOrDefault(() -> provider, RSItemDefinition::getGroundActions, new String[0]);
    }

    public boolean isNoted() {
        return provider.getNoteTemplateId() != -1;
    }

    public int getNotedId() {
        return isNoted() ? getId() : provider.getNoteId();
    }

    public int getUnnotedId() {
        return !isNoted() ? getId() : provider.getNoteId();
    }

    public boolean isBorrowed() {
        return provider.getBorrowedTemplateId() != -1;
    }

    public int getBorrowedId() {
        return isBorrowed() ? provider.getBorrowedId() : getId();
    }

    public boolean isStackable() {
        return provider.getStackable() > 0;
    }

    public boolean containsAction(String action) {
        return containsAction(x -> x.equals(action));
    }

    public boolean containsAction(Predicate<String> action) {
        String[] actions = getActions();
        if (actions == null) {
            return false;
        }

        for (String e : actions) {
            if (e != null && action.test(e)) {
                return true;
            }
        }
        return false;
    }

    public int getParameterAsInteger(int key) {
        RSNode node = getParameters().getSynthetic(key);
        return node instanceof RSIntegerNode ? ((RSIntegerNode) node).getValue() : -1;
    }

    public int getParameterAsInteger(Parameter parameter) {
        return getParameterAsInteger(parameter.index);
    }

    public boolean isParameterPresent(int key, int value) {
        return getParameterAsInteger(key) == value;
    }

    public boolean isParameterPresent(Parameter key, int value) {
        return isParameterPresent(key.index, value);
    }

    public enum Parameter {

        MAGIC_ACCURACY(3),
        RANGED_ACCURACY(4),
        ATTACK_SPEED(14),
        EQUIPPED_ACTION_0(528),
        EQUIPPED_ACTION_1(529),
        EQUIPPED_ACTION_3(530),
        EQUIPPED_ACTION_4(531),
        MELEE_DAMAGE(641),
        RANGED_DAMAGE(643),
        WIELD_SKILL_0(749),
        WIELD_LEVEL_0(750),
        WIELD_SKILL_1(751),
        WIELD_LEVEL_1(752),
        WIELD_SKILL_2(753),
        WIELD_LEVEL_2(754),
        WIELD_SKILL_3(755),
        WIELD_LEVEL_3(756),
        WIELD_SKILL_4(757),
        WIELD_LEVEL_4(758),
        WIELD_SKILL_5(759),
        WIELD_LEVEL_5(760),
        COMBAT_LEVEL_REQUIREMENTS(761),
        REQUIREMENTS_SKILL_0(770),
        REQUIREMENTS_LEVEL_0(771),
        REQUIREMENTS_SKILL_1(772),
        REQUIREMENTS_LEVEL_1(773),
        REQUIREMENTS_SKILL_2(774),
        REQUIREMENTS_LEVEL_2(775),
        REQUIREMENTS_SKILL_3(776),
        REQUIREMENTS_LEVEL_3(777),
        HEAL_AMOUNT(963),
        MAGIC_DAMAGE(965),
        MELEE_WEAPON(2825),
        RAGE_WEAPON(2826),
        MAGIC_WEAPON(2827),
        MELEE_ACCURACY(3267);

        private final int index;

        Parameter(int index) {
            this.index = index;
        }
    }
}
