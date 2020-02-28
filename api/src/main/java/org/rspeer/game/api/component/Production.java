package org.rspeer.game.api.component;

import org.rspeer.api.commons.Time;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.api.query.results.InterfaceComponentQueryResults;
import org.rspeer.game.api.query.results.QueryResults;

import java.util.ArrayList;
import java.util.List;

public final class Production {

    private static final int MAIN_INTERFACE = InterfaceComposite.PRODUCTION.getGroup();
    private static final int PROGRESS_INTERFACE = InterfaceComposite.PRODUCTION_PROGRESS.getGroup();

    private static final InterfaceAddress INTERFACE_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(MAIN_INTERFACE, a -> true)
    );

    private static final InterfaceAddress CONFIRM_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(MAIN_INTERFACE, a -> a.containsAction(x -> x.startsWith("Make")))
    );

    private static final InterfaceAddress PROGRESS_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(PROGRESS_INTERFACE, a -> a.getText().endsWith("s"))
    );

    private static final InterfaceAddress ITEM_PROGRESS_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(PROGRESS_INTERFACE, a -> a.getText().contains("/"))
    );

    private static final InterfaceAddress CATEGORY_SELECTED = new InterfaceAddress(() -> {
        return Interfaces.getFirst(1371, a -> {
            String text = a.getText();
            return text != null && !text.isEmpty() && a.getWidth() == 212 && a.getHeight() == 22;
        }, true);
    });

    private static final InterfaceAddress OPEN_CATEGORIES = new InterfaceAddress(() -> {
        return Interfaces.getFirst(1371, a -> a.containsAction("View options"), true);
    });

    public static boolean isOpen() {
        InterfaceComponent component = INTERFACE_ADDRESS.resolve();
        return component != null && component.isVisible();
    }

    public static boolean confirm() {
        InterfaceComponent component = CONFIRM_ADDRESS.resolve();
        if (component != null && component.isVisible()) {
            for (String action : component.getActions()) {
                if (action != null && action.startsWith("Make")) {
                    return component.interact(action);
                }
            }
        }
        return false;
    }

    public static boolean isProgressOpen() {
        InterfaceComponent component = Interfaces.getFirst(PROGRESS_INTERFACE, a -> a.getText().endsWith("s"));
        return component != null && component.isVisible();
    }

    //TODO add something to get the number aftr the /
    public static int getProgress() {
        InterfaceComponent component = ITEM_PROGRESS_ADDRESS.resolve();
        if (component != null && component.isVisible()) {
            String text = component.getText();
            if (!text.contains("/")) {
                return -1;
            }
            text = text.substring(0, text.indexOf("/"));
            try {
                return Integer.valueOf(text.replace("/", ""));
            } catch (Exception ignored) {

            }
        }
        return -1;
    }

    public static boolean selectCategory(String categoryName) {
        if (getSelectedCategory().equals(categoryName)) {
            return true;
        }

        if (!expandCategories() || !Time.sleepUntil(Production::isCategoriesExpanded, 3000)) {
            return false;
        }

        List<String> categories = new ArrayList<>(getCategoryNames());
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).equals(categoryName)) {
                if (expandCategories()) {
                    selectCategory(i);
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean expandCategories() {
        InterfaceComponent resolve = OPEN_CATEGORIES.resolve();
        return resolve != null && resolve.interact("View options");
    }

    public static String getSelectedCategory() {
        InterfaceComponent resolve = CATEGORY_SELECTED.resolve();
        return resolve == null ? "" : resolve.getText();
    }

    /**
     * @return {@code true} if the categories dropdown is expanded
     */
    public static boolean isCategoriesExpanded() {
        return !getCategories().isEmpty();
    }

    public static QueryResults<String, QueryResults<String, ?>> getCategoryNames() {
        return getCategories().map(InterfaceComponent::getText);
    }

    public static boolean selectCategory(int categoryIndex) {
        InterfaceComponentQueryResults categoriesSelection = getCategoriesSelection();
        if (categoriesSelection.size() <= categoryIndex) {
            return false;
        }
        InterfaceComponent interfaceComponent = categoriesSelection.get(categoryIndex);
        return interfaceComponent != null && interfaceComponent.interact("Select");
    }

    private static InterfaceComponentQueryResults getCategories() {
        return Interfaces.newQuery().groups(1477).filter(a -> {
            String text = a.getText();
            return text != null && !text.isEmpty() && a.getWidth() == 215 && a.getHeight() == 15;
        }).includeSubcomponents().results();
    }

    private static InterfaceComponentQueryResults getCategoriesSelection() {
        return Interfaces.newQuery().groups(1477).filter(a ->
                a.containsAction("Select") && a.getWidth() == 215 && a.getHeight() == 15
        ).includeSubcomponents().results();
    }
}
