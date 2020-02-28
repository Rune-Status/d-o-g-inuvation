package org.rspeer.game.api;

import org.rspeer.game.adapter.cache.ItemDefinition;
import org.rspeer.game.providers.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Definitions {

    private static final Map<Integer, ItemDefinition> items = Collections.synchronizedMap(new LinkedHashMap<>());
//    private static final Map<Integer, RSObjectDefinition> objects = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Map<Integer, RSParameterDefinition> parameters = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Map<Integer, RSInterfaceComponentDefinition> interfaces = Collections.synchronizedMap(new LinkedHashMap<>());

    private static final int LOAD_LIMIT = 1 << 16;

    private Definitions() {
        throw new IllegalAccessError();
    }

    /**
     * Loads all item and object definitions - This method is intended for internal use
     */
    public static synchronized void populate() {
        RSClient client = Game.getClient();
        if (client != null) {
            loadDefinitions(items, id -> Game.getClient().getItemDefinition(id), Function.identity(), LOAD_LIMIT);
            loadDefinitions(parameters, id -> Game.getClient().loadParameterDefinition(id), Function.identity(), LOAD_LIMIT);
//            loadDefinitions(objects, id -> Game.getClient().getObjectDefinition(id), RSObjectDefinition::transform, LOAD_LIMIT);
        }
    }

    public static boolean isLoaded() {
        return !items.isEmpty() /*&& !objects.isEmpty()*/ && !parameters.isEmpty();
    }

    private static <K> void loadDefinitions(Map<Integer, K> dest,
                                            Function<Integer, K> invoker,
                                            Function<K, K> transformer,
                                            int limit) {
        loadDefinitions(dest, invoker, transformer, 0, limit);
    }

    private static <K> void loadDefinitions(Map<Integer, K> dest,
                                            Function<Integer, K> invoker,
                                            Function<K, K> transformer,
                                            int start,
                                            int limit) {
        if (!dest.isEmpty()) {
            return;
        }

        for (int i = start; i < limit; i++) {
            K definition = invoker.apply(i);
            if (definition != null) {
                K transformed = transformer.apply(definition);
                if (transformed != null) {
                    dest.put(i, transformed);
                } else {
                    dest.put(i, definition);
                }
            }
        }
    }

    /**
     * @param id The definition id
     * @return An RSObjectDefinition with the given id, or null if not present
     */
//    public static RSObjectDefinition getObject(int id) {
//        return objects.get(id);
//    }

    /**
     * @param id The definition id
     * @return An RSParameterDefinition with the given id, or null if not present
     */
    public static RSParameterDefinition getParameter(int id) {
        return parameters.get(id);
    }

    public static RSInterfaceComponentDefinition getComponent(RSInterfaceComponent component) {
        int uid = component.getUid();
        if (!interfaces.containsKey(uid)) {
            RSInterfaceComponentDefinition definition = Game.getClient().loadComponentDefinition2(component);
            if (definition != null) {
                interfaces.put(uid, definition);
            }
        }
        return interfaces.get(uid);
    }

    /**
     * @param id The definition id
     * @return An RSItemDefinition with the given id, or null if not present
     */
    public static ItemDefinition getItem(int id) {
        return items.get(id);
    }

    /**
     * @param name      The name of the item to search for
     * @param predicate
     * @return The first item definition with the given name, matching the given predicate
     */
    public static ItemDefinition getItem(String name, Predicate<ItemDefinition> predicate) {
        for (int i = 0; i < LOAD_LIMIT; i++) {
            ItemDefinition d = items.get(i);
            if (d != null && d.getName() != null && d.getName().equalsIgnoreCase(name) && predicate.test(d)) {
                return d;
            }
        }
        return null;
    }

//    public static Map<Integer, RSObjectDefinition> getObjects() {
//        return objects;
//    }

    public static Map<Integer, ItemDefinition> getItems() {
        return items;
    }

    public static Map<Integer, RSInterfaceComponentDefinition> getInterfaces() {
        return interfaces;
    }
}
