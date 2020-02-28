package org.rspeer.game.adapter.component;

import org.rspeer.api.commons.Functions;
import org.rspeer.api.commons.Predicates;
import org.rspeer.game.adapter.Adapter;
import org.rspeer.game.api.Definitions;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.action.ActionOpcodes;
import org.rspeer.game.api.action.ActionProcessor;
import org.rspeer.game.api.action.Interactable;
import org.rspeer.game.api.action.tree.*;
import org.rspeer.game.api.component.InterfaceAddress;
import org.rspeer.game.providers.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public final class InterfaceComponent extends Adapter<RSInterfaceComponent> implements Interactable {

    private static final String TAGS = "<.*>";

    public InterfaceComponent(RSInterfaceComponent provider) {
        super(provider);
    }

    public int getX() {
        return provider.getX();
    }

    public int getY() {
        return provider.getY();
    }

    public int getWidth() {
        return provider.getWidth();
    }

    public int getHeight() {
        return provider.getHeight();
    }

    public int getIndex() {
        return provider.getIndex();
    }

    public int getGroupIndex() {
        return provider.getGroupIndex();
    }

    public int getParentIndex() {
        return provider.getParentIndex();
    }

    public int getItemId() {
        return provider.getItemId();
    }

    public int getItemStackSize() {
        return provider.getItemQuantity();
    }

    public int getParentUid() {
        return provider.getParentUid();
    }

    public int getUid() {
        return provider.getUid();
    }

    public int getType() {
        return provider.getType();
    }

    public int getContentType() {
        return provider.getContentType();
    }

    public int getMaterialId() {
        return provider.getMaterialId();
    }

    public int getBorderThickness() {
        return provider.getBorderThickness();
    }

    public int getShadowColor() {
        return provider.getShadowColor();
    }

    public int getComponentIndex() {
        return provider.getComponentIndex();
    }

    public int getForeground() {
        return provider.getForeground();
    }

    public int getModelId() {
        return provider.getModelId();
    }

    public int getAnimation() {
        return provider.getAnimation();
    }

    public int getAnimationFrame() {
        return provider.getAnimationFrame();
    }

    public boolean isGrandchild() {
        return provider.isGrandchild();
    }

    public Object[] getLoadListeners() {
        return provider.getLoadListeners();
    }

    public Object[] getMouseEnterListeners() {
        return provider.getMouseEnterListeners();
    }

    public Object[] getMouseExitListeners() {
        return provider.getMouseExitListeners();
    }

    public Object[] getMouseHoverListeners() {
        return provider.getMouseHoverListeners();
    }

    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    private String findSelectableAction(RSClient client, RSInterfaceComponentDefinition cfg) {
        if (cfg != null && client.isComponentSelected()) {
            int uid = client.getSelectedComponentUid();
            RSParameterDefinition param = uid == -1 ? null : Definitions.getParameter(uid);
            if (cfg.isUsable() && (client.getSelectedComponentAttribute() % 32) != 0) {
                if (param == null || provider.getNumericProperty(uid, param.getDefaultInt()) != param.getDefaultInt()) {
                    String action = client.getSelectedComponentAction();
                    if (action != null) {
                        return action.replaceAll(TAGS, "");
                    }
                }
            }
        }
        return null;
    }

    public String[] getActions() {
        List<String> actions = new ArrayList<>();
        RSClient client = Game.getClient();
        RSInterfaceComponentDefinition cfg = client.loadComponentDefinition2(provider);

        String selectable = findSelectableAction(client, cfg);
        if (selectable != null) {
            actions.add(selectable);
        }

        String[] rawActions = getRawActions();
        if (rawActions != null) {
            for (int i = 9; i >= 0; --i) {
                if (rawActions.length > i && rawActions[i] != null) {
                    actions.add(rawActions[i].replaceAll(TAGS, ""));
                }
            }
        }

        String useAction = client.getComponentUseAction2(provider);
        if (useAction != null) {
            actions.add(useAction.replaceAll(TAGS, ""));
        }

        if (cfg != null && cfg.isDialogOption()) {
            actions.add(Functions.mapOrDefault(provider::getToolTip, Function.identity(), "Continue"));
        }

        return actions.toArray(new String[0]);
    }

    public InterfaceAddress toAddress() {
        return new InterfaceAddress(getGroupIndex())
                .component(isGrandchild() ? getParentIndex() : getIndex())
                .subComponent(getComponentIndex());
    }

    public String getName() {
        String name = provider.getName();
        return name == null ? "" : name.replaceAll(TAGS, "");
    }

    public String getText() {
        return Functions.mapOrDefault(() -> provider, RSInterfaceComponent::getText, "");
    }

    public InterfaceComponent[] getComponents(Predicate<InterfaceComponent> predicate) {
        RSInterfaceComponent[] components = getRawComponents();
        List<InterfaceComponent> list = new ArrayList<>();
        if (components != null) {
            for (RSInterfaceComponent component : components) {
                if (component != null) {
                    InterfaceComponent adapter = component.getAdapter();
                    if (predicate.test(adapter)) {
                        list.add(adapter);
                    }
                }
            }
        }
        return list.toArray(new InterfaceComponent[0]);
    }

    public RSInterfaceComponent[] getRawComponents() {
        return Functions.mapOrDefault(() -> provider, RSInterfaceComponent::getComponents, provider.getComponentsCopy());
    }

    public InterfaceComponent[] getComponents() {
        return getComponents(Predicates.always());
    }

    public InterfaceComponent getComponent(int index) {
        RSInterfaceComponent[] raw = provider.getComponents();
        return raw != null && index >= 0 && index < raw.length && raw[index] != null
                ? raw[index].getAdapter() : null;
    }

    public InterfaceComponent getComponent(Predicate<InterfaceComponent> predicate) {
        for (InterfaceComponent component : getComponents()) {
            if (predicate.test(component)) {
                return component;
            }
        }
        return null;
    }

    public String getTooltip() {
        return Functions.mapOrDefault(() -> provider, RSInterfaceComponent::getToolTip, "");
    }

    @Override
    public String[] getRawActions() {
        return provider.getActions();
    }

    @Override
    public boolean interact(int opcode) {
        if (opcode == ActionOpcodes.OP_COMPONENT1 || opcode == ActionOpcodes.OP_COMPONENT2) {
            throw new UnsupportedOperationException("Use interact(String)");
        }
        ActionProcessor.submit(Action.valueOf(opcode, 0, getComponentIndex(), getUid()));
        return true;
    }

    @Override
    public ComponentAction actionOf(String action) {
        RSClient client = Game.getClient();
        RSInterfaceComponentDefinition cfg = client.loadComponentDefinition2(provider);

        String selectable = findSelectableAction(client, cfg);
        if (selectable != null && action.equalsIgnoreCase(selectable.replaceAll(TAGS, ""))) {
            return new UseOnComponentAction(false, getComponentIndex(), getUid());
        }

        String[] rawActions = getRawActions();
        if (rawActions != null) {
            for (int i = 9; i >= 0; --i) {
                if (rawActions.length > i && rawActions[i] != null && action.equalsIgnoreCase(rawActions[i].replaceAll(TAGS, ""))) {
                    return new IndexedComponentAction(i + 1, getComponentIndex(), getUid());
                }
            }
        }

        String useAction = client.getComponentUseAction2(provider);
        if (action.equalsIgnoreCase(useAction)) {
            return new UseOnComponentAction(true, getComponentIndex(), getUid());
        }

        if (cfg != null && cfg.isDialogOption()) {
            String tooltip = getTooltip();
            if (action.equalsIgnoreCase(tooltip) || action.equalsIgnoreCase("Continue")) {
                return new ButtonAction(getComponentIndex(), getUid());
            }
        }
        return null;
    }

    @Override
    public boolean interact(String action) {
        Action resolved = actionOf(action);
        if (resolved != null) {
            ActionProcessor.submit(resolved);
            return true;
        }
        return false;
    }

    public boolean isVisible() {
        return provider.getRenderCycle() + 20 >= Game.getEngineCycle() && !provider.isExplicitlyHidden();
    }

    public boolean containsParameter(Predicate<Object> predicate) {
        RSNodeTable<RSNode> props = getProvider().getProperties();
        if (props == null) {
            return false;
        }

        List<RSNode> parameters = props.toList();
        for (RSNode node : parameters) {
            Object value = null;
            if (node instanceof RSObjectNode) {
                value = ((RSObjectNode) node).getReferent();
            }

            if (value != null && predicate.test(value)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsParameter(int value) {
        RSNodeTable<RSNode> props = getProvider().getProperties();
        if (props == null) {
            return false;
        }

        List<RSNode> parameters = props.toList();
        for (RSNode node : parameters) {
            int val = -1;
            if (node instanceof RSIntegerNode) {
                val = ((RSIntegerNode) node).getValue();
            }

            if (value == val) {
                return true;
            }
        }
        return false;
    }

    public enum Type {

        PANEL(0),
        UNNAMED(1),
        TABLE(2),
        BOX(3),
        LABEL(4),
        SPRITE(5),
        MODEL(6),
        MEDIA(7),
        TOOLTIP(8),
        DIVIDER(9);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public static Type get(int value) {
            for (Type type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            throw new IllegalArgumentException("An unknown interface component type provided for value: " + value);
        }

        public int getValue() {
            return value;
        }
    }
}
