package org.rspeer.game.api.position;

public enum Direction {

    NORTH(2, 0, 1, true),
    NORTH_EAST(0, 1, 1, false),
    EAST(1, 1, 0, true),
    SOUTH_EAST(0, 1, -1, false),
    SOUTH(8, 0, -1, true),
    SOUTH_WEST(0, -1, -1, false),
    WEST(4, -1, 0, true),
    NORTH_WEST(0, -1, 1, false);

    private final int orientation;
    private final int xOffset;
    private final int yOffset;
    private final boolean cardinal;

    Direction(int orientation, int xOffset, int yOffset, boolean cardinal) {
        this.orientation = orientation;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.cardinal = cardinal;
    }

    public static Direction of(int angle) {
        if (angle < 45) {
            return NORTH;
        } else if (angle < 90) {
            return NORTH_EAST;
        } else if (angle < 135) {
            return EAST;
        } else if (angle < 180) {
            return SOUTH_EAST;
        } else if (angle < 225) {
            return SOUTH;
        } else if (angle < 270) {
            return SOUTH_WEST;
        } else if (angle < 315) {
            return WEST;
        } else {
            return NORTH_WEST;
        }
    }

    public static Direction of(Positionable src, Positionable dst) {
        int angle = 90 - ((int) Math.toDegrees(Math.atan2(dst.getY() - src.getY(), dst.getX() - src.getX())));
        if (angle < 0) {
            angle += 360;
        }
        return of(angle % 360);
    }

    public Position translate(Position src) {
        return translate(src, 1);
    }

    public Position translate(Position src, int amt) {
        int x = 0;
        int y = 0;
        if (this == NORTH) {
            y -= amt;
        } else if (this == NORTH_EAST) {
            x -= amt;
            y -= amt;
        } else if (this == EAST) {
            x -= amt;
        } else if (this == SOUTH_EAST) {
            x -= amt;
            y += amt;
        } else if (this == SOUTH) {
            y += amt;
        } else if (this == SOUTH_WEST) {
            x += amt;
            y += amt;
        } else if (this == WEST) {
            x += amt;
        } else if (this == NORTH_WEST) {
            x += amt;
            y -= amt;
        }
        return src.translate(x, y);
    }

    public Direction inverse() {
        if (this == NORTH) return SOUTH;
        if (this == NORTH_EAST) return SOUTH_WEST;
        if (this == EAST) return WEST;
        if (this == SOUTH_EAST) return NORTH_WEST;
        if (this == SOUTH) return NORTH;
        if (this == SOUTH_WEST) return NORTH_EAST;
        if (this == WEST) return EAST;
        if (this == NORTH_WEST) return SOUTH_EAST;
        throw new IllegalStateException();
    }

    public boolean isSameAxis(int orientation) {
        if (orientation == this.orientation) {
            return true;
        }

        if (this == NORTH && SOUTH.orientation == orientation) {
            return true;
        }

        if (this == SOUTH && NORTH.orientation == orientation) {
            return true;
        }

        if (this == EAST && WEST.orientation == orientation) {
            return true;
        }

        return this == WEST && EAST.orientation == orientation;

    }

    public boolean isCardinal() {
        return cardinal;
    }

    public int getXOffset() {
        return xOffset;
    }

    public int getYOffset() {
        return yOffset;
    }
}
