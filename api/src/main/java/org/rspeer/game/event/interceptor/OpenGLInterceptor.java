package org.rspeer.game.event.interceptor;

import org.rspeer.OperatingSystem;
import org.rspeer.api.reflection.Reflection;
import org.rspeer.game.api.Game;
import org.rspeer.game.event.interceptor.impl.Intercept;
import org.rspeer.game.event.interceptor.impl.InterceptType;
import org.rspeer.game.event.types.RenderEvent;
import org.rspeer.game.providers.RSOpenGL;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.lang.invoke.MethodHandle;
import java.util.Hashtable;

public class OpenGLInterceptor extends Interceptor {

    private static int overlayTexture;
    public static int width = 500, height = 500;

    private static byte[] VBLANK;
    private static BufferedImage overlayBuffer;
    static Graphics2D overlayGraphics;

    private static RSOpenGL gl;

    private static MethodHandle swapBuffers;
    private static MethodHandle surfaceResized;
    private static MethodHandle init;
    private static MethodHandle releaseSurface;
    private static int thisCanvas;

    @Intercept(owner = "jagdx/OpenGL", type = InterceptType.INSTANCE)
    public static long init(Object _this, Canvas paramCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
        if (gl == null) {
            gl = getProxyFor(RSOpenGL.class);
        }

        if (OperatingSystem.get() == OperatingSystem.WINDOWS)
            resizeSurface();

        try {
            bufferHook(_this);
            return (Long) init.invoke(_this, paramCanvas, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return 0;
    }

    @Intercept(owner = "jagdx/OpenGL", type = InterceptType.INSTANCE)
    public static void surfaceResized(Object _this, long paramLong) {
        resizeSurface();
        bufferHook(_this);
        try {
            surfaceResized.invoke(_this, paramLong);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static void resizeSurface() {
        int[] viewport = new int[4];
        gl.glGetIntegerv(RSOpenGL.GL_VIEWPORT, viewport, 0);

        width = viewport[2];
        height = viewport[3];
        VBLANK = new byte[width * height * 4];
    }

    private static void bufferHook(Object _this) {
        if (!(_this.hashCode() != thisCanvas || swapBuffers == null || surfaceResized == null || init == null || releaseSurface == null))
            return;
        thisCanvas = _this.hashCode();
        swapBuffers = Reflection.declaredMethod("swapBuffers").in(_this).withParameters(long.class).handle();
        releaseSurface = Reflection.declaredMethod("releaseSurface").in(_this).withParameters(Canvas.class, long.class).handle();
        surfaceResized = Reflection.declaredMethod("surfaceResized").in(_this).withParameters(long.class).handle();
        init = Reflection.declaredMethod("init").in(_this).withReturnType(Long.class).withParameters(Canvas.class, int.class, int.class, int.class, int.class, int.class, int.class).handle();
    }


    @Intercept(owner = "jagdx/OpenGL", type = InterceptType.INSTANCE)
    public static void swapBuffers(Object _this, long paramLong) {
        gl.glEnable(RSOpenGL.GL_TEXTURE_2D);
        if (overlayBuffer == null) {
            int[] t = new int[1];
            gl.glGenTextures(1, t, 0);
            overlayTexture = t[0];
            gl.glBindTexture(RSOpenGL.GL_TEXTURE_2D, overlayTexture);
            gl.glTexParameteri(RSOpenGL.GL_TEXTURE_2D, RSOpenGL.GL_TEXTURE_MAG_FILTER, RSOpenGL.GL_NEAREST);
            gl.glTexParameteri(RSOpenGL.GL_TEXTURE_2D, RSOpenGL.GL_TEXTURE_MIN_FILTER, RSOpenGL.GL_NEAREST);
        } else {
            gl.glBindTexture(RSOpenGL.GL_TEXTURE_2D, overlayTexture);
        }
        if ((overlayBuffer == null || overlayBuffer.getWidth() != width || overlayBuffer.getHeight() != height) && width > 0 && height > 0) {
            WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width, height, 4, null);
            overlayBuffer = new BufferedImage(
                    new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8, 8}, true, false, ComponentColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE),
                    raster,
                    false,
                    new Hashtable<>());
            overlayGraphics = overlayBuffer.createGraphics();
            overlayGraphics.setClip(0, 0, width, height);
            overlayGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            overlayGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        try {
            byte[] buffer = ((DataBufferByte) overlayBuffer.getRaster().getDataBuffer()).getData();
            System.arraycopy(VBLANK, 0, buffer, 0, VBLANK.length);
            try {
                overlayGraphics.drawString("OpenGL", 10, 15);

                Game.getEventDispatcher().immediate(new RenderEvent(overlayGraphics, "OGL"));
            } catch (Throwable error) {
                System.err.println("An error occurred while painting:");
                error.printStackTrace();
            }
            gl.glTexImage2Dub(RSOpenGL.GL_TEXTURE_2D, 0, RSOpenGL.GL_RGBA, width, height, 0, RSOpenGL.GL_RGBA, RSOpenGL.GL_UNSIGNED_BYTE, buffer, 0);
            gl.glColor4f(1, 1, 1, 1);
            gl.glBegin(RSOpenGL.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex2f(0, 0);
            gl.glTexCoord2f(0, 1);
            gl.glVertex2f(0, height);
            gl.glTexCoord2f(1, 1);
            gl.glVertex2f(width, height);
            gl.glTexCoord2f(1, 0);
            gl.glVertex2f(width, 0);
            gl.glEnd();
            bufferHook(_this);
        } catch (Exception e) {
            System.err.println("An error occurred while painting:");
            e.printStackTrace();
        }
        try {
            swapBuffers.invoke(_this, paramLong);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
