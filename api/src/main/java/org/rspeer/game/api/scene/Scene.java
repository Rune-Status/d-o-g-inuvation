package org.rspeer.game.api.scene;

import org.rspeer.api.commons.Functions;
import org.rspeer.api.commons.Identifiable;
import org.rspeer.api.commons.Predicates;
import org.rspeer.game.adapter.scene.Player;
import org.rspeer.game.adapter.scene.SceneObject;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.position.*;
import org.rspeer.game.api.query.results.PositionableQueryResults;
import org.rspeer.game.providers.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

public final class Scene {

    public static final int TILE_UNITS = 512;
    public static final int TILE_SCALE = 9;

    private static final int DEFAULT_WIDTH = 104;
    private static final int DEFAULT_LENGTH = 104;
    private static final int DEFAULT_DEPTH = 4;

    private Scene() {
        throw new IllegalAccessError();
    }

    public static boolean isDynamic() {
        RSScene scene = Game.getClient().getScene();
        if (scene != null) {
            RSSceneFormat format = scene.getFormat();
            if (format != null) {
                return format.isDynamic();
            }
        }
        return false;
    }

    public static WorldPosition getBase() {
        int x = -1;
        int y = -1;
        int floor = -1;
        RSScene scene = Game.getClient().getScene();
        if (scene != null) {
            RSSceneOffset offset = scene.getOffset();
            if (offset != null) {
                x = offset.getX();
                y = offset.getY();
                floor = getLevel();
            }
        }
        return Position.global(x, y, floor);
    }

    public static int getLevel() {
        Player player = Players.getLocal();
        return player != null ? player.getFloorLevel() : -1;
    }

    public static int getBaseTileHeight(Position position) {
        position = position.getAbsolutePosition();
        int fineX = position.getX();
        int fineY = position.getY();

        int x = fineX >> TILE_SCALE;
        int y = fineY >> TILE_SCALE;

        RSSceneGraphLevel level = Game.getClient().getScene().getLevel();
        if (level == null || x < 0 || y < 0 || x > level.getWidth() - 1 || y > level.getLength() - 1) {
            return 0;
        }

        int[][] heights = level.getTileHeights();
        int dx = fineX & TILE_UNITS - 1;
        int dy = fineY & TILE_UNITS - 1;
        int xOff = dx * heights[x + 1][y] + heights[x][y] * (TILE_UNITS - dx) >> TILE_SCALE;
        int yOff = (TILE_UNITS - dx) * heights[x][y + 1] + dx * heights[x + 1][y + 1] >> TILE_SCALE;
        return dy * yOff + (TILE_UNITS - dy) * xOff >> TILE_SCALE;
    }

    public static PositionableQueryResults<Region> getRegions() {
        return getRegions(Predicates.always());
    }

    public static PositionableQueryResults<Region> getRegions(Predicate<Region> predicate) {
        int[] ids = Functions.mapOrDefault(() -> Game.getClient().getScene(), RSScene::getRegionIds, new int[0]);
        List<Region> regions = new ArrayList<>();
        for (int id : ids) {
            Region region = new Region(id);
            if (predicate.test(region)) {
                regions.add(region);
            }
        }
        return new PositionableQueryResults<>(regions);
    }

    public static PositionableQueryResults<Region> getRegions(int... ids) {
        return getRegions(Identifiable.predicate(ids));
    }

    public static Region getRegion(Predicate<Region> predicate) {
        int[] ids = Functions.mapOrDefault(() -> Game.getClient().getScene(), RSScene::getRegionIds, new int[0]);
        for (int id : ids) {
            Region region = new Region(id);
            if (predicate.test(region)) {
                return region;
            }
        }
        return null;
    }

    public static Region getRegion(int... ids) {
        return getRegion(Identifiable.predicate(ids));
    }

    public static boolean isRegionLoaded(int... ids) {
        int[] loaded = Functions.mapOrDefault(() -> Game.getClient().getScene(), RSScene::getRegionIds, new int[0]);
        for (int load : loaded) {
            for (int id : ids) {
                if (load == id) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Region getCurrentRegion() {
        return getRegion(x -> x.getArea().contains(Players.getLocal()));
    }

    public static CollisionMap getCollisionMap() {
        return new CollisionMap(new Dimension(DEFAULT_WIDTH, DEFAULT_LENGTH));
    }

    public static final class Region implements Identifiable, Positionable {

        private static final int DEFAULT_WIDTH = 64;
        private static final int DEFAULT_LENGTH = 64;

        private final int id;
        private final Dimension dimension;
        private final Area area;

        private Region(int id) {
            this(id, new Dimension(DEFAULT_WIDTH, DEFAULT_LENGTH));
        }

        private Region(int id, Dimension dimension) {
            this.id = id;
            this.dimension = dimension;
            area = Area.rectangular(getPosition(), getPosition().translate(DEFAULT_WIDTH, DEFAULT_LENGTH));
        }

        @Override
        public int getId() {
            return id;
        }

        public Area getArea() {
            return area;
        }

        @Override
        public ScenePosition getScenePosition() {
            return getPosition().getScenePosition();
        }

        @Override
        public WorldPosition getPosition() {
            return Position.global((id >> 8) * 64, (id & 0xff) * 64);
        }

        @Override
        public FinePosition getAbsolutePosition() {
            return getPosition().getAbsolutePosition();
        }

        public Dimension getDimension() {
            return dimension;
        }

        @Override
        public String toString() {
            return "Region[id=" + id + ",x=" + ((id >> 8) * 64) + ",y=" + ((id & 0xff) * 64) + "]";
        }
    }

    public static class CollisionMap {

        private final Dimension dimension;
        private final int[][][] flags;

        private CollisionMap(Dimension dimension) {
            this.dimension = dimension;
            flags = build();
        }

        private static boolean isBlocked(int var0, int... flagOffsets) {
            int var3 = flagOffsets.length;

            int var4;
            for (int var10000 = var4 = 0; var10000 < var3; var10000 = var4) {
                int $ = flagOffsets[var4];
                if ((var0 >> $ & 1) == 1) {
                    return true;
                }

                ++var4;
            }

            return false;
        }

        private static void IIiiiiiiIiiII(byte[][][] var0, int[][][] var1) {
            int var2;
            for (int var10000 = var2 = 0; var10000 < 4; var10000 = var2) {
                int var3;
                for (var10000 = var3 = 0; var10000 < 104; var10000 = var3) {
                    int var4;
                    for (var10000 = var4 = 0; var10000 < 104; var10000 = var4) {
                        if ((var0[var2][var3][var4] & 1) == 1) {
                            int var5 = var2;
                            if ((var0[1][var3][var4] & 2) == 2) {
                                var5 = var2 - 1;
                            }

                            if (var5 >= 0) {
                                IIIiiiiiIiiii(var1, var5, var3, var4, 2097152);
                            }
                        }
                        ++var4;
                    }
                    ++var3;
                }
                ++var2;
            }
        }

        private static void IIIiiiiiIiiii(int[][][] var0, int var1, int var2, int var3, int var4) {
            if (var2 >= 0 && var2 < 104 && var3 >= 0 && var3 < 104) {
                var0[var1][var2][var3] |= var4;
            }

        }

        private static void padMap(int[][][] var0) {
            int var1;
            for (int var10000 = var1 = 0; var10000 < 4; var10000 = var1) {
                int var2;
                for (var10000 = var2 = 0; var10000 < 104; var10000 = var2) {
                    int var3;
                    for (var10000 = var3 = 0; var10000 < 104; var10000 = var3) {
                        if (var2 == 0 || var2 >= 99 || var3 == 0 || var3 >= 99) {
                            var0[var1][var2][var3] = -1;
                        }
                        ++var3;
                    }
                    ++var2;
                }
                ++var1;
            }
        }

        private static void method16851(int[][][] var0, int var1, int var2, int var3) {
            method16858(var0, var1, var2, var3, 262144);
        }

        private static void method16856(int[][][] var0, int var1, int var2, int var3, int var4, int var5, boolean var6, boolean var7) {
            int var8 = 256;
            if (var6) {
                var8 |= 131072;
            }

            if (var7) {
                var8 |= 1073741824;
            }

            int var9;
            for (int var10000 = var9 = var2; var10000 < var2 + var4; var10000 = var9) {
                int var10;
                if (var9 >= 0 && var9 < var0[var1].length) {
                    for (var10000 = var10 = var3; var10000 < var3 + var5; var10000 = var10) {
                        if (var10 >= 0 && var10 < var0[var1][var9].length) {
                            method16858(var0, var1, var9, var10, var8);
                        }

                        ++var10;
                    }
                }

                ++var9;
            }

        }

        private static void method16860(int[][][] var0, int var1, int var2, int var3, int var4, int direction, boolean var6, boolean var7) {
            int var8 = direction;
            int var9;
            if (var4 == 0) {
                var9 = (var8 << 1) - 1 & 7;
                method16858(var0, var1, var2, var3, 1 << var9);
                method16858(var0, var1, var2 + method16855(var9), var3 + method16857(var9), 1 << method16863(var9));
            }

            if (var4 == 1 || var4 == 3) {
                var9 = var8 << 1 & 7;
                method16858(var0, var1, var2, var3, 1 << var9);
                method16858(var0, var1, var2 + method16855(var9), var3 + method16857(var9), 1 << method16863(var9));
            }

            if (var4 == 2) {
                var9 = (var8 << 1) + 1 & 7;
                int var10 = (var8 << 1) - 1 & 7;
                method16858(var0, var1, var2, var3, 1 << (var9 | var10));
                method16858(var0, var1, var2 + method16855(var9), var3 + method16857(var9), 1 << method16863(var9));
                method16858(var0, var1, var2 + method16855(var10), var3 + method16857(var10), 1 << method16863(var10));
            }

            if (var6) {
                if (var4 == 0) {
                    if (var8 == 0) {
                        method16858(var0, var1, var2, var3, 65536);
                        method16858(var0, var1, var2 - 1, var3, 4096);
                    }

                    if (var8 == 1) {
                        method16858(var0, var1, var2, var3, 1024);
                        method16858(var0, var1, var2, var3 + 1, 16384);
                    }

                    if (var8 == 2) {
                        method16858(var0, var1, var2, var3, 4096);
                        method16858(var0, var1, var2 + 1, var3, 65536);
                    }

                    if (var8 == 3) {
                        method16858(var0, var1, var2, var3, 16384);
                        method16858(var0, var1, var2, var3 - 1, 1024);
                    }
                }

                if (var4 == 1 || var4 == 3) {
                    if (var8 == 0) {
                        method16858(var0, var1, var2, var3, 512);
                        method16858(var0, var1, var2 - 1, var3 + 1, 8192);
                    }

                    if (var8 == 1) {
                        method16858(var0, var1, var2, var3, 2048);
                        method16858(var0, var1, 1 + var2, 1 + var3, '耀');
                    }

                    if (var8 == 2) {
                        method16858(var0, var1, var2, var3, 8192);
                        method16858(var0, var1, var2 + 1, var3 - 1, 512);
                    }

                    if (var8 == 3) {
                        method16858(var0, var1, var2, var3, '耀');
                        method16858(var0, var1, var2 - 1, var3 - 1, 2048);
                    }
                }

                if (var4 == 2) {
                    if (var8 == 0) {
                        method16858(var0, var1, var2, var3, 66560);
                        method16858(var0, var1, var2 - 1, var3, 4096);
                        method16858(var0, var1, var2, 1 + var3, 16384);
                    }

                    if (var8 == 1) {
                        method16858(var0, var1, var2, var3, 5120);
                        method16858(var0, var1, var2, 1 + var3, 16384);
                        method16858(var0, var1, 1 + var2, var3, 65536);
                    }

                    if (var8 == 2) {
                        method16858(var0, var1, var2, var3, 20480);
                        method16858(var0, var1, var2 + 1, var3, 65536);
                        method16858(var0, var1, var2, var3 - 1, 1024);
                    }

                    if (var8 == 3) {
                        method16858(var0, var1, var2, var3, 81920);
                        method16858(var0, var1, var2, var3 - 1, 1024);
                        method16858(var0, var1, var2 - 1, var3, 4096);
                    }
                }
            }

            if (var7) {
                if (var4 == 0) {
                    if (var8 == 0) {
                        method16858(var0, var1, var2, var3, 536870912);
                        method16858(var0, var1, var2 - 1, var3, 33554432);
                    }

                    if (var8 == 1) {
                        method16858(var0, var1, var2, var3, 8388608);
                        method16858(var0, var1, var2, var3 + 1, 134217728);
                    }

                    if (var8 == 2) {
                        method16858(var0, var1, var2, var3, 33554432);
                        method16858(var0, var1, var2 + 1, var3, 536870912);
                    }

                    if (var8 == 3) {
                        method16858(var0, var1, var2, var3, 134217728);
                        method16858(var0, var1, var2, var3 - 1, 8388608);
                    }
                }

                if (var4 == 1 || var4 == 3) {
                    if (var8 == 0) {
                        method16858(var0, var1, var2, var3, 4194304);
                        method16858(var0, var1, var2 - 1, var3 + 1, 67108864);
                    }

                    if (var8 == 1) {
                        method16858(var0, var1, var2, var3, 16777216);
                        method16858(var0, var1, var2 + 1, var3 + 1, 268435456);
                    }

                    if (var8 == 2) {
                        method16858(var0, var1, var2, var3, 67108864);
                        method16858(var0, var1, var2 + 1, var3 - 1, 4194304);
                    }

                    if (var8 == 3) {
                        method16858(var0, var1, var2, var3, 268435456);
                        method16858(var0, var1, var2 - 1, var3 - 1, 16777216);
                    }
                }

                if (var4 == 2) {
                    if (var8 == 0) {
                        method16858(var0, var1, var2, var3, 545259520);
                        method16858(var0, var1, var2 - 1, var3, 33554432);
                        method16858(var0, var1, var2, var3 + 1, 134217728);
                    }

                    if (var8 == 1) {
                        method16858(var0, var1, var2, var3, 41943040);
                        method16858(var0, var1, var2, var3 + 1, 134217728);
                        method16858(var0, var1, var2 + 1, var3, 536870912);
                    }

                    if (var8 == 2) {
                        method16858(var0, var1, var2, var3, 167772160);
                        method16858(var0, var1, var2 + 1, var3, 536870912);
                        method16858(var0, var1, var2, var3 - 1, 8388608);
                    }

                    if (var8 == 3) {
                        method16858(var0, var1, var2, var3, 671088640);
                        method16858(var0, var1, var2, var3 - 1, 8388608);
                        method16858(var0, var1, var2 - 1, var3, 33554432);
                    }
                }
            }
        }

        private static void method16858(int[][][] var0, int var1, int var2, int var3, int var4) {
            if (var2 >= 0 && var2 < 104 && var3 >= 0 && var3 < 104) {
                var0[var1][var2][var3] |= var4;
            }
        }

        private static int method16855(int var0) {
            switch (var0) {
                case 0:
                case 6:
                case 7:
                    return -1;
                case 1:
                case 5:
                    return 0;
                case 2:
                case 3:
                case 4:
                    return 1;
                default:
                    return 0;
            }
        }

        private static int method16857(int var0) {
            switch (var0) {
                case 0:
                case 1:
                case 2:
                    return 1;
                case 3:
                case 7:
                    return 0;
                case 4:
                case 5:
                case 6:
                    return -1;
                default:
                    return 0;
            }
        }

        private static int method16863(int var0) {
            return var0 + 4 & 7;
        }

        private int[][][] build() {
            int[][][] map = new int[DEFAULT_DEPTH][dimension.width][dimension.height];
            padMap(map);
            byte[][][] renderFlags = Game.getClient().getScene().getSettings().getRenderRules();

            PositionableQueryResults<SceneObject> objects = SceneObjects.getLoaded();

            for (SceneObject object : objects) {
                RSObjectDefinition definition = object.getDefinition();
                int type = object.getType();
                if (type >= 4 && type <= 8) {
                    continue;
                }
                Area area = object.getArea();
                Position pos;
                if (area instanceof Area.Rectangular) {
                    pos = ((Area.Rectangular) area).getBottomLeft();
                } else if (area instanceof Area.Singular) {
                    pos = area.getCenter();
                } else {
                    throw new RuntimeException("Shouldnt be possible!");
                }
                int x = pos.getScenePosition().getX();
                int y = pos.getScenePosition().getY();
                int plane = pos.getFloorLevel();

                if (x < 0 || x >= dimension.width || y < 0 || y >= dimension.height) {
                    continue;
                }

                if (plane < 4) {
                    if (renderFlags != null && (renderFlags[1][x][y] & 2) == 2) {
                        plane--;
                    }
                    if (plane >= 0) {
                        if (type >= 0 && type <= 3) {
                            if (definition.getClippingType() != 0) {
                                method16860(map, plane, x, y, type, object.getOrientation(), !definition.isImpassable(), !definition.isImpenetrable());
                            }
                            continue;
                        }
                        if (type == 22) {
                            if (definition.getClippingType() == 1) {
                                method16851(map, plane, x, y);
                            }
                        } else if (type >= 9) {
                            if (definition.getClippingType() != 0) {
                                int direction = object.getOrientation();
                                if (direction != 1 && direction != 3) {
                                    method16856(map, plane, x, y, definition.getWidth(), definition.getHeight(), !definition.isImpassable(), !definition.isImpenetrable());
                                } else {
                                    method16856(map, plane, x, y, definition.getHeight(), definition.getWidth(), !definition.isImpassable(), !definition.isImpenetrable());
                                }
                            }
                        }
                    }
                }
            }

            if (renderFlags != null) {
                IIiiiiiiIiiII(renderFlags, map);
            }

            return map;
        }

        public Dimension getDimension() {
            return dimension;
        }

        public int[][][] getFlags() {
            return flags;
        }

        public int getFlag(Position position) {
            position = position.getScenePosition();
            try {
                return flags[position.getFloorLevel()][position.getX()][position.getY()];
            } catch (Exception e) {
                return -1;
            }
        }

        public boolean isReachable(Positionable dest) {
            return getReachableFrom(Position.local()).contains(dest.getPosition());
        }

        //useful for passing SceneObjects which make the base position unwalkable
        public boolean isSurroundingsReachable(Positionable dest) {
            Position pos = dest.getPosition();
            List<Position> reachable = getReachableFrom(Position.local());
            for (int x = -1; x < 1; x++) {
                for (int y = -1; y < 1; y++) {
                    if (reachable.contains(pos.translate(x, y))) {
                        return true;
                    }
                }
            }
            return false;
        }

        public Position getReachableSurrounding(Positionable dest) {
            Position pos = dest.getPosition();
            List<Position> reachable = getReachableFrom(Position.local());
            for (int x = -1; x < 1; x++) {
                for (int y = -1; y < 1; y++) {
                    Position translated = pos.translate(x, y);
                    if (reachable.contains(translated)) {
                        return translated;
                    }
                }
            }
            return null;
        }

        public Position getReachableWithin(Positionable dest, int dist) {
            Position pos = dest.getPosition();
            List<Position> reachable = getReachableFrom(Position.local());
            for (int x = -dist; x < dist; x++) {
                for (int y = -dist; y < dist; y++) {
                    Position translated = pos.translate(x, y);
                    if (reachable.contains(translated)) {
                        return translated;
                    }
                }
            }
            return null;
        }

        public List<Position> getReachableFrom(Positionable src) {
            Deque<Position> pending = new LinkedList<>(Collections.singleton(src.getPosition()));
            List<Position> reachable = new ArrayList<>();
            List<Position> traversed = new ArrayList<>();

            while (!pending.isEmpty() && pending.size() < 10000) {
                Position current = pending.pop().getPosition();
                if (traversed.contains(current) || reachable.contains(current)) continue;
                traversed.add(current);
                if (!current.isInScene()) continue;

                int flag;
                if (isBlocked(flag = getFlag(current), Offsets.O)) {
                    continue;
                }

                if (!reachable.contains(current)) {
                    reachable.add(current);
                    if (!isBlocked(flag, Offsets.N)) {
                        Position position = current.translate(0, 1);
                        if (!traversed.contains(position) && !pending.contains(position) && !reachable.contains(position)) {
                            pending.offer(position);
                        }
                    }
                    if (!isBlocked(flag, Offsets.E)) {
                        Position position = current.translate(1, 0);
                        if (!traversed.contains(position) && !pending.contains(position) && !reachable.contains(position)) {
                            pending.offer(position);
                        }
                    }
                    if (!isBlocked(flag, Offsets.S)) {
                        Position position = current.translate(0, -1);
                        if (!traversed.contains(position) && !pending.contains(position) && !reachable.contains(position)) {
                            pending.offer(position);
                        }
                    }
                    if (!isBlocked(flag, Offsets.W)) {
                        Position position = current.translate(-1, 0);
                        if (!traversed.contains(position) && !pending.contains(position) && !reachable.contains(position)) {
                            pending.offer(position);
                        }
                    }
                }
            }

            return reachable;
        }


        private interface Offsets {
            /**
             * All north boundary flag offsets
             */
            int[] N = {1, 10, 23};

            /**
             * All east boundary flag offsets
             */
            int[] E = {3, 12, 25};

            /**
             * All south boundary flag offsets
             */
            int[] S = {5, 14, 27};

            /**
             * All west boundary flag offsets
             */
            int[] W = {7, 16, 29};

            /**
             * All north-east boundary flag offsets
             */
            int[] NE = {2, 11, 24};

            /**
             * All north-west boundary flag offsets
             */
            int[] NW = {0, 9, 22};

            /**
             * All south-east boundary flag offsets
             */
            int[] SE = {4, 13, 26};

            /**
             * All south-west boundary flag offsets
             */
            int[] SW = {6, 15, 28};

            /**
             * All object boundary flag offsets
             */
            int[] O = {8, 17, 18, 21};
        }
    }
}
