package org.rspeer.game.api.query;

import org.rspeer.game.adapter.scene.SceneObject;
import org.rspeer.game.api.position.Position;
import org.rspeer.game.api.scene.SceneObjects;
import org.rspeer.game.providers.RSSceneObject;
import org.rspeer.game.api.commons.ArrayUtils;
import org.rspeer.game.api.commons.predicate.ActionPredicate;
import org.rspeer.game.api.commons.predicate.IdPredicate;
import org.rspeer.game.api.commons.predicate.NamePredicate;
import org.rspeer.game.api.query.results.PositionableQueryResults;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public final class SceneObjectQueryBuilder extends PositionableQueryBuilder<SceneObject, SceneObjectQueryBuilder> {

    private Class<? extends RSSceneObject>[] types = null;

    private int[] ids = null;
    private int[] mapFunctions = null;

    private String[] names = null;
    private String[] nameContains = null;
    private String[] actions = null;

    @Override
    public Supplier<List<? extends SceneObject>> getDefaultProvider() {
        return () -> SceneObjects.getLoaded().asList();
    }

    @Override
    protected PositionableQueryResults<SceneObject> createQueryResults(Collection<? extends SceneObject> raw) {
        return new PositionableQueryResults<>(raw);
    }

    public SceneObjectQueryBuilder actions(String... actions) {
        this.actions = actions;
        return self();
    }

    public SceneObjectQueryBuilder ids(int... ids) {
        this.ids = ids;
        return self();
    }

    public SceneObjectQueryBuilder names(String... names) {
        this.names = names;
        return self();
    }

    public SceneObjectQueryBuilder nameContains(String... names) {
        this.nameContains = names;
        return self();
    }

    public SceneObjectQueryBuilder mapFunctions(int... mapFunctions) {
        this.mapFunctions = mapFunctions;
        return self();
    }

    public SceneObjectQueryBuilder types(Class<? extends RSSceneObject>... types) {
        this.types = types;
        return self();
    }

    public SceneObjectQueryBuilder on(Position... positions) {
        provider(() -> {
            List<SceneObject> objects = new ArrayList<>();
            for (Position position : positions) {
                objects.addAll(SceneObjects.getLoadedAt(position, x -> true));
            }
            return objects;
        });
        return super.on(positions);
    }

    @Override
    public boolean test(SceneObject obj) {
        if (ids != null && !new IdPredicate<>(ids).test(obj)) {
            return false;
        }

        if (names != null && !new NamePredicate<>(names).test(obj)) {
            return false;
        }

        if (nameContains != null && !new NamePredicate<>(true, nameContains).test(obj)) {
            return false;
        }

        if (actions != null && !new ActionPredicate<>(actions).test(obj)) {
            return false;
        }

        if (types != null && ArrayUtils.contains(types, obj.getType())) {
            return false;
        }

        if (mapFunctions != null && ArrayUtils.contains(mapFunctions, obj.getDefinition().getMapFunction())) {
            return false;
        }

        return super.test(obj);
    }
}
