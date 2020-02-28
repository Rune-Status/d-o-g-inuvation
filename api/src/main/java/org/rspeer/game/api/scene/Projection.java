package org.rspeer.game.api.scene;

import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.component.InterfaceAddress;
import org.rspeer.game.api.component.Interfaces;
import org.rspeer.game.api.position.Position;
import org.rspeer.game.providers.RSRenderConfiguration;

import java.awt.*;

public final class Projection {

    public static final int JAGEX_CIRCULAR_ANGLE = 0x4000;
    public static final double ANGULAR_RATIO = 360 / (double) JAGEX_CIRCULAR_ANGLE;
    public static final double JAGEX_RADIAN = Math.toRadians(ANGULAR_RATIO);

    public static final int[] SIN_TABLE = new int[JAGEX_CIRCULAR_ANGLE];
    public static final int[] COS_TABLE = new int[JAGEX_CIRCULAR_ANGLE];

    public static final int DEFAULT_ENGINE_TICK_DELAY = 120;

    private static final InterfaceAddress MINIMAP_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(x -> x.getContentType() == 1338)
    );

    private static boolean renderingDisabled;
    private static boolean engineTickDelayEnabled;
    private static int engineTickDelay = -1;

    static {
        for (int i = 0; i < JAGEX_CIRCULAR_ANGLE; i++) {
            SIN_TABLE[i] = (int) (32768 * Math.sin(i * JAGEX_RADIAN));
            COS_TABLE[i] = (int) (32768 * Math.cos(i * JAGEX_RADIAN));
        }
    }

    private Projection() {
        throw new IllegalAccessError();
    }

    //TODO change some Position params to Positionable after adding heightOffset to positionables

    public static boolean isEngineTickDelayEnabled() {
        return engineTickDelayEnabled;
    }

    public static void setEngineTickDelayEnabled(boolean engineTickDelayEnabled) {
        Projection.engineTickDelayEnabled = engineTickDelayEnabled;
        setEngineTickDelay(DEFAULT_ENGINE_TICK_DELAY);
    }

    public static boolean isRenderingDisabled() {
        return renderingDisabled;
    }

    public static void setRenderingDisabled(boolean renderingDisabled) {
        Projection.renderingDisabled = renderingDisabled;
    }

    public static int getEngineTickDelay() {
        return engineTickDelay;
    }

    public static void setEngineTickDelay(int engineTickDelay) {
        Projection.engineTickDelay = engineTickDelay;
    }

    public static RSRenderConfiguration getToolkit() {
        return Game.getClient().getActiveRenderConfiguration();
    }

    public static Point toViewport(Position position, float heightOffset) {
        position = position.getAbsolutePosition();
        heightOffset += Scene.getBaseTileHeight(position);
        int fineX = position.getX();
        int fineY = position.getY();

        RSRenderConfiguration config = getToolkit();
        if (config == null) {
            return new Point(0, 0);
        }

        float[] matrix = config.getMatrix4f().getMatrix();

        float dz = heightOffset * matrix[7] + (fineX * matrix[3] + matrix[15]) + fineY * matrix[11];
        float dx = matrix[0] * fineX + matrix[12] + heightOffset * matrix[4] + matrix[8] * fineY;
        float dy = fineY * matrix[9] + (matrix[13] + matrix[1] * fineX + matrix[5] * heightOffset);

        float x = config.getMultiplierX() + dx * config.getAbsoluteX() / dz;
        float y = config.getMultiplierY() + dy * config.getAbsoluteY() / dz;

        return new Point((int) x, (int) y);
    }

    public static Point toViewport(Position position) {
        return toViewport(position, 0);
    }

    public static Polygon getTileShape(Position position) {
        position = position.getAbsolutePosition();

        Point p1 = toViewport(position);
        Point p2 = toViewport(position.translate(Scene.TILE_UNITS, 0));
        Point p3 = toViewport(position.translate(Scene.TILE_UNITS, Scene.TILE_UNITS));
        Point p4 = toViewport(position.translate(0, Scene.TILE_UNITS));

        Polygon poly = new Polygon();
        poly.addPoint(p1.x, p1.y);
        poly.addPoint(p2.x, p2.y);
        poly.addPoint(p3.x, p3.y);
        poly.addPoint(p4.x, p4.y);

        return poly;
    }

    public static Point toMinimap(Position position) {
        position = position.getAbsolutePosition();

        Position local = Position.local().getAbsolutePosition();

        int calculatedX = (position.getX() >> 9) * 4 + 2 - (local.getX()) / 128;
        int calculatedY = (position.getY() >> 9) * 4 + 2 - (local.getY()) / 128;

        InterfaceComponent mm = MINIMAP_ADDRESS.resolve();
        if (mm == null) {
            return new Point(0, 0);
        }

        int dist = calculatedX * calculatedX + calculatedY * calculatedY;
        int mmdist = Math.max(mm.getWidth() / 2, mm.getHeight() / 2) - 6;

        if (mmdist * mmdist >= dist) {
            int angle = 0x3fff & (int) Game.getClient().getMapAngle();
            boolean state = Game.getClient().getMapState() == 4;

            if (!state) {
                angle = 0x3fff & Game.getClient().getMapOffset() + (int) Game.getClient().getMapAngle();
            }

            int sin = SIN_TABLE[angle];
            int cos = COS_TABLE[angle];

            if (!state) {
                int fact = 0x100 + Game.getClient().getMapScale();
                sin = 0x100 * sin / fact;
                cos = 0x100 * cos / fact;
            }

            int calcCenterX = cos * calculatedX + sin * calculatedY >> 0xf;
            int calcCenterY = cos * calculatedY - sin * calculatedX >> 0xf;
            int screenX = calcCenterX + mm.getX() + mm.getWidth() / 2;
            int screenY = -calcCenterY + mm.getY() + mm.getHeight() / 2;
            return new Point(screenX, screenY);
        }

        return new Point(-1, -1);
    }
}
