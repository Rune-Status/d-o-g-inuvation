package org.rspeer.game.api.query.results;

import org.rspeer.game.adapter.world.World;

import java.util.Collection;
import java.util.Comparator;

public final class WorldQueryResults extends QueryResults<World, WorldQueryResults> {
    public WorldQueryResults(Collection<? extends World> results) {
        super(results);
    }

    /**
     * @return Sorts results by world id and then returns self
     */
    public WorldQueryResults indexed() {
        return sort(Comparator.comparingInt(World::getWorld));
    }

    /**
     * @return The world with the lowest id from the results
     */
    public World min() {
        return indexed().first();
    }

    /**
     * @return The world with the highest id from the results
     */
    public World max() {
        return indexed().last();
    }


    private WorldQueryResults sortByPopulation() {
        return sort(Comparator.comparingInt(World::getPopulation));
    }

    /**
     * @return The world with the lowest population from the results
     */
    public World quietest() {
        return sortByPopulation().first();
    }

    /**
     * @return The world with the highest population from the results
     */
    public World busiest() {
        return sortByPopulation().last();
    }
}
