package org.rspeer.game.api.position;

import org.rspeer.game.api.scene.Scene;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

public abstract class Area {

    private final int floorLevel;
    protected boolean ignoreFloorLevel = false;

    protected Area(int floorLevel) {
        this.floorLevel = floorLevel;
    }

    public static Area rectangular(Position start, Position end, int floorLevel) {
        return new Rectangular(start, end, floorLevel);
    }

    public static Area rectangular(Position start, Position end) {
        return rectangular(start, end, start.getFloorLevel());
    }

    public static Area surrounding(Position origin, int distance, int floorLevel) {
        return rectangular(origin.translate(-distance, -distance), origin.translate(distance, distance), floorLevel);
    }

    public static Area surrounding(Position origin, int distance) {
        return surrounding(origin, distance, origin.getFloorLevel());
    }

    public static Area singular(Position tile) {
        return new Singular(tile);
    }

    public static Area polygonal(int floorLevel, Position... vertices) {
        return new Polygonal(floorLevel, vertices);
    }

    public static Area absolute(int floorLevel, Position... positions) {
        return new Absolute(floorLevel, positions);
    }

    public static Area absolute(Position... positions) {
        return absolute(Scene.getLevel(), positions);
    }

    public abstract List<Position> getTiles();

    public Position getTile(Predicate<Position> predicate) {
        for (Position position : getTiles()) {
            if (predicate.test(position)) {
                return position;
            }
        }
        return null;
    }

    public Area translate(int x, int y) {
        throw new UnsupportedOperationException("Override");
    }

    public List<Position> getSurroundingCoordinates() {
        List<Position> list = new ArrayList<>();
        for (Position tile : getTiles()) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    list.add(tile.translate(i, j));
                }
            }
        }
        return list;
    }

    public abstract boolean contains(Positionable positionable);

    public Position getCenter() {
        List<Position> tiles = getTiles();
        int x = 0, y = 0;
        for (Position t : tiles) {
            x += t.getX();
            y += t.getY();
        }
        x /= tiles.size();
        y /= tiles.size();
        return Position.global(x, y, floorLevel);
    }

    public void outline(Graphics g) {
        for (Position position : getTiles()) {
            g.drawPolygon(position.getShape());
        }
    }

    public int getFloorLevel() {
        return floorLevel;
    }

    public final Area setIgnoreFloorLevel(boolean ignoreFloorLevel) {
        this.ignoreFloorLevel = ignoreFloorLevel;
        return this;
    }

    public boolean isIgnoringFloorLevel() {
        return ignoreFloorLevel;
    }

    public static class Absolute extends Area {

        private final List<Position> positions;

        private Absolute(int floorLevel, Position... positions) {
            super(floorLevel);
            this.positions = Arrays.asList(positions);
        }

        @Override
        public List<Position> getTiles() {
            return positions;
        }

        @Override
        public boolean contains(Positionable positionable) {
            for (Position pos : positions) {
                if (positionable.getPosition().equals(pos)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class Rectangular extends Area {

        private final List<Position> tiles;
        private final int minX, maxX, minY, maxY;

        private final Position bottomLeft;
        private final Position bottomRight;
        private final Position topLeft;
        private final Position topRight;

        private final int width, height;

        private Rectangular(Position start, Position end, int floorLevel) {
            super(floorLevel);
            tiles = new ArrayList<>();

            int startX = start.getX(), startY = start.getY();
            int endX = end.getX(), endY = end.getY();
            for (int x = Math.min(startX, endX); x <= Math.max(startX, endX); x++) {
                for (int y = Math.max(startY, endY); y >= Math.min(startY, endY); y--) {
                    tiles.add(Position.global(x, y, floorLevel));
                }
            }
            minX = Math.min(startX, endX);
            minY = Math.min(startY, endY);
            maxX = Math.max(startX, endX);
            maxY = Math.max(startY, endY);

            bottomLeft = Position.global(minX, minY);
            bottomRight = Position.global(maxX, minY);
            topLeft = Position.global(minX, maxY);
            topRight = Position.global(maxX, maxY);

            this.width = maxX - minX;
            this.height = maxY - minY;
        }

        @Override
        public List<Position> getTiles() {
            return tiles;
        }

        @Override
        public boolean contains(Positionable p) {
            if (p == null) return false;
            return (super.ignoreFloorLevel || p.getFloorLevel() == getFloorLevel())
                    && p.getX() >= minX && p.getY() >= minY
                    && p.getX() <= maxX && p.getY() <= maxY;
        }

        @Override
        public Area translate(int x, int y) {
            Position start = Position.global(minX, minY, getFloorLevel()).translate(x, y);
            Position end = Position.global(maxX, maxY, getFloorLevel()).translate(x, y);
            return new Rectangular(start, end, getFloorLevel())
                    .setIgnoreFloorLevel(super.ignoreFloorLevel);
        }

        public Position getBottomLeft() {
            return bottomLeft;
        }

        public Position getBottomRight() {
            return bottomRight;
        }

        public Position getTopLeft() {
            return topLeft;
        }

        public Position getTopRight() {
            return topRight;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    public static class Singular extends Area {

        private final Position src;

        private Singular(Position src) {
            super(src.getFloorLevel());
            this.src = src;
        }

        @Override
        public List<Position> getTiles() {
            return Collections.singletonList(src);
        }

        @Override
        public boolean contains(Positionable positionable) {
            return positionable.getPosition().equals(src);
        }
    }

    public static class Polygonal extends Area {

        private final List<Position> tiles;
        private Polygon polygon;

        private volatile List<Position> area;

        private Polygonal(int floorLevel, Position... vertices) {
            super(floorLevel);
            tiles = new ArrayList<>();

            if (vertices.length > 0) {
                int[] xs = new int[vertices.length];
                int[] ys = new int[vertices.length];

                for (int i = 0; i < vertices.length; i++) {
                    Position tile = vertices[i];
                    xs[i] = tile.getX();
                    ys[i] = tile.getY();
                }

                polygon = new Polygon(xs, ys, vertices.length);
            }
        }

        @Override
        public List<Position> getTiles() {
            if (area == null) {
                computeArea();
            }

            return area;
        }

        private synchronized void computeArea() {
            area = new ArrayList<>(tiles);
            if (tiles.size() == 0) {
                return;
            }

            Queue<Position> open = new LinkedList<>(tiles);
            Set<Position> closed = new TreeSet<>();

            while (!open.isEmpty()) {
                Position current = open.poll();
                closed.add(current);

                for (Position neighbour : current.getNeighbors(true)) {
                    if (closed.contains(neighbour)) {
                        continue;
                    }

                    if (!contains(neighbour)) {
                        closed.add(neighbour);
                    } else {
                        if (!area.contains(neighbour)) {
                            area.add(neighbour);
                        }
                        open.add(neighbour);
                    }
                }
            }
        }

        @Override
        public final boolean contains(Positionable positionable) {
            return positionable != null && positionable.getFloorLevel() == getFloorLevel() && polygon.contains(positionable.getX(), positionable.getY());
        }
    }
}