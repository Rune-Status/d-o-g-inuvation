package org.rspeer.game.api.scene;

import org.rspeer.api.commons.Functions;
import org.rspeer.api.commons.Identifiable;
import org.rspeer.api.commons.Predicates;
import org.rspeer.game.adapter.scene.Player;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.position.Distance;
import org.rspeer.game.api.query.results.PositionableQueryResults;
import org.rspeer.game.providers.RSPlayer;
import org.rspeer.game.api.query.PlayerQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class Players {

    private Players() {
        throw new IllegalAccessError();
    }

    public static PositionableQueryResults<Player> getLoaded(Predicate<Player> predicate) {
        List<Player> players = new ArrayList<>();
        for (RSPlayer player : Game.getClient().getPlayers()) {
            if (player != null && predicate.test(player.getAdapter())) {
                players.add(player.getAdapter());
            }
        }
        return new PositionableQueryResults<>(players);
    }

    public static Player getLocal() {
        return Functions.mapOrNull(() -> Game.getClient().getPlayer(), RSPlayer::getAdapter);
    }

    public static PositionableQueryResults<Player> getLoaded() {
        return getLoaded(Predicates.always());
    }

    public static Player getNearest(Predicate<Player> predicate) {
        return Distance.getNearest(getLoaded(), predicate);
    }

    public static Player getNearest(String... names) {
        return getNearest(Identifiable.predicate(names));
    }

    public static Player getNearest() {
        return getNearest(Predicates.always());
    }

    public static PlayerQueryBuilder newQuery() {
        return new PlayerQueryBuilder();
    }
}
