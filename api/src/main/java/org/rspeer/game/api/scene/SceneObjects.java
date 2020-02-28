package org.rspeer.game.api.scene;

import org.rspeer.api.commons.Functions;
import org.rspeer.api.commons.Identifiable;
import org.rspeer.game.adapter.scene.SceneObject;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.position.Distance;
import org.rspeer.game.api.position.Position;
import org.rspeer.game.api.position.ScenePosition;
import org.rspeer.game.api.query.results.PositionableQueryResults;
import org.rspeer.game.providers.*;
import org.rspeer.game.api.query.SceneObjectQueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public final class SceneObjects {

    private SceneObjects() {
        throw new IllegalAccessError();
    }

    public static SceneObject getNearest(Predicate<SceneObject> predicate) {
        return Distance.getNearest(getLoaded(), predicate);
    }

    public static SceneObject getNearest(int... ids) {
        return Distance.getNearest(getLoaded(), Identifiable.predicate(ids));
    }

    public static SceneObject getNearest(String... names) {
        return getNearest(Identifiable.predicate(names));
    }

    public static PositionableQueryResults<SceneObject> getLoaded() {
        return getLoaded(e -> true);
    }

    private static PositionableQueryResults<SceneObject> getLoaded(int floor, Predicate<SceneObject> predicate) {
        RSSceneGraph graph = Functions.mapOrNull(() -> Game.getClient().getScene(), RSScene::getGraph);
        if (graph == null || floor < 0 || floor > 3 || graph.getTiles().length <= floor) {
            return new PositionableQueryResults<>(Collections.emptyList());
        }

        List<SceneObject> loaded = new ArrayList<>();
        RSSceneGraphTile[][] level = graph.getTiles()[floor];
        for (RSSceneGraphTile[] tiles : level) {
            for (RSSceneGraphTile tile : tiles) {
                if (tile == null) {
                    continue;
                }
                collect(tile, loaded, predicate);
            }
        }
        return new PositionableQueryResults<>(loaded);
    }

    public static PositionableQueryResults<SceneObject> getLoaded(Predicate<SceneObject> predicate) {
        return getLoaded(Scene.getLevel(), predicate);
    }

    public static SceneObject getFirst(Predicate<SceneObject> predicate) {
        return getLoaded(predicate).first();
    }

    private static void collect(RSSceneGraphTile tile, List<SceneObject> loaded, Predicate<SceneObject> predicate) {
        RSIterableSceneEntity iterable = tile.getIterableEntities();
        while (iterable != null) {
            RSSceneEntity current = iterable.getCurrent();
            if (current instanceof RSSceneObject) {
                SceneObject obj = ((RSSceneObject) current).getAdapter();
                if (predicate.test(obj)) {
                    loaded.add(obj);
                }
            }
            iterable = iterable.getNext();
        }
        collect(tile.getBoundary(), loaded, predicate);
        collect(tile.getBoundary2(), loaded, predicate);
        collect(tile.getBoundaryDecor(), loaded, predicate);
        collect(tile.getBoundaryDecor2(), loaded, predicate);
        collect(tile.getTileDecor(), loaded, predicate);
    }

    private static void collect(RSSceneObject current, List<SceneObject> loaded, Predicate<SceneObject> predicate) {
        if (current != null) {
            SceneObject obj = current.getAdapter();
            if (predicate.test(obj)) {
                loaded.add(obj);
            }
        }
    }

    public static PositionableQueryResults<SceneObject> getLoadedAt(Position loc) {
        return getLoadedAt(loc, x -> true);
    }

    public static PositionableQueryResults<SceneObject> getLoadedAt(Position loc, Predicate<SceneObject> predicate) {        ScenePosition scenePosition = loc.getScenePosition();
        return getLoadedAt(scenePosition.getX(), scenePosition.getY(), predicate);
    }

    public static SceneObject getFirstAt(Position loc, Predicate<SceneObject> predicate) {
        return getLoadedAt(loc, predicate).first();
    }

    public static PositionableQueryResults<SceneObject> getLoadedAt(int regionX, int regionY, Predicate<SceneObject> predicate) {
        List<SceneObject> loaded = new ArrayList<>();
        RSScene scene = Game.getClient().getScene();

        if (scene == null) {
            return new PositionableQueryResults<>(Collections.emptyList());
        }

        RSSceneGraph graph = scene.getGraph();
        if (graph == null) {
            return new PositionableQueryResults<>(Collections.emptyList());
        }

        int z = Scene.getLevel();
        RSSceneGraphTile[][] level = graph.getTiles()[z];
        if (level == null || regionX < 0 || regionX >= level.length) {
            return new PositionableQueryResults<>(Collections.emptyList());
        }

        RSSceneGraphTile[] tiles = level[regionX];
        if (tiles == null || regionY < 0 || regionY >= tiles.length) {
            return new PositionableQueryResults<>(Collections.emptyList());
        }

        RSSceneGraphTile tile = tiles[regionY];
        if (tile != null) {
            collect(tile, loaded, predicate);
        }
        return new PositionableQueryResults<>(loaded);
    }

    public static SceneObjectQueryBuilder newQuery() {
        return new SceneObjectQueryBuilder();
    }
}
