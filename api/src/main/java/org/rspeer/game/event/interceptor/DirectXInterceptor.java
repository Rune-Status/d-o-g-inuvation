package org.rspeer.game.event.interceptor;

import org.rspeer.game.api.Game;
import org.rspeer.game.event.interceptor.impl.Intercept;
import org.rspeer.game.event.interceptor.impl.InterceptType;
import org.rspeer.game.providers.RSDirect3D;
import org.rspeer.game.providers.RSRenderConfiguration;
import org.rspeer.game.providers.proxies.RSDirect3DDevice;
import org.rspeer.game.providers.proxies.RSDirect3DSurface;
import org.rspeer.game.providers.proxies.RSDirect3DSwapChain;
import org.rspeer.game.providers.proxies.RSDirectBufferHelper;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Consumer;

public class DirectXInterceptor extends Interceptor {

    public static RSDirect3D dx;
    public static RSDirect3DSurface surface;
    public static RSDirect3DDevice device;
    public static RSDirect3DSwapChain swapChain;
    public static RSDirectBufferHelper bufferHelper;

    public static long D3DFVF_CUSTOM = 0;

    @Intercept(owner = "jagdx/IDirect3D", type = InterceptType.CALLBACK)
    public static long Direct3DCreate() {
        if (dx == null)
            dx = getProxyFor(RSDirect3D.class);
        if (surface == null)
            surface = getProxyFor(RSDirect3DSurface.class);
        if (device == null)
            device = getProxyFor(RSDirect3DDevice.class);
        if (swapChain == null)
            swapChain = getProxyFor(RSDirect3DSwapChain.class);
        if (bufferHelper == null)
            bufferHelper = getProxyFor(RSDirectBufferHelper.class);
        return dx.Direct3DCreate();
    }

    /*@Intercept(owner = "jagdx/IDirect3DDevice", type = InterceptType.CALLBACK)
    public static long CreateVertexShader(long var0, byte[] var2) {
        System.out.println(var0 + " " + new String(var2));
        return 0L;
    }*/

    @Intercept(owner = "jagdx/IDirect3DDevice", type = InterceptType.CALLBACK)
    public static int DrawPrimitive(long var0, int var2, int var3, int var4) {
//        System.out.println("DrawPrimitive: " + var0 + " " + var2 + " " + var3 + " " + var4);
        try {
            Class<?> clazz = Game.getClient().getClass().getClassLoader().loadClass("jagdx.IDirect3DDevice");
            Method method = clazz.getDeclaredMethod("DrawPrimitive", long.class, int.class, int.class, int.class);
            method.setAccessible(true);
            return (int) method.invoke(null, var0, var2, var3, var4);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Intercept(owner = "jagdx/IDirect3DDevice", type = InterceptType.CALLBACK)
    public static int SetStreamSource(long var0, int var2, long var3, int var5, int var6) {
//        System.out.println("SetStreamSource: " + var0 + " " + var2 + " " + var3 + " " + var5 + " " + var6);
        try {
            Class<?> clazz = Game.getClient().getClass().getClassLoader().loadClass("jagdx.IDirect3DDevice");
            Method method = clazz.getDeclaredMethod("SetStreamSource", long.class, int.class, long.class, int.class, int.class);
            method.setAccessible(true);
            return (int) method.invoke(null, var0, var2, var3, var5, var6);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Intercept(owner = "jagdx/IDirect3DDevice", type = InterceptType.CALLBACK)
    public static int SetVertexDeclaration(long var0, long var2) {
        try {
            Class<?> clazz = Game.getClient().getClass().getClassLoader().loadClass("jagdx.IDirect3DDevice");
            Method method = clazz.getDeclaredMethod("SetVertexDeclaration", long.class, long.class);
            method.setAccessible(true);
            return (int) method.invoke(null, var0, var2);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Intercept(owner = "jagdx/IDirect3DDevice", type = InterceptType.CALLBACK)
    public static int EndScene(long var0) {
//        System.out.println("end scene");

        if (Game.isLoggedIn()) {
            if (D3DFVF_CUSTOM == 0) {
//            WORD Stream;
//            WORD Offset;
//            BYTE Type;
//            BYTE Method;
//            BYTE Usage;
//            BYTE UsageIndex;
                if (FVF_BUFFER.hasRemaining()) {
                    FVF_BUFFER.order(ByteOrder.nativeOrder());
                    FVF_BUFFER.putShort((short) 0);
                    FVF_BUFFER.putShort((short) 0);
                    FVF_BUFFER.put((byte) 1);
                    FVF_BUFFER.put((byte) 0);
                    FVF_BUFFER.put((byte) 0);
                    FVF_BUFFER.put((byte) 0);

//                FVF_BUFFER.putShort((short) 0);
//                FVF_BUFFER.putShort((short) 12);
//                FVF_BUFFER.put((byte) 4);
//                FVF_BUFFER.put((byte) 0);
//                FVF_BUFFER.put((byte) 10);
//                FVF_BUFFER.put((byte) 0);

                    FVF_BUFFER.putShort((short) 255);
                    FVF_BUFFER.putShort((short) 0);
                    FVF_BUFFER.put((byte) 17);
                    FVF_BUFFER.put((byte) 0);
                    FVF_BUFFER.put((byte) 0);
                    FVF_BUFFER.put((byte) 0);
                }
                long bufferLoc = bufferHelper.getDirectBufferAddress(FVF_BUFFER);
                try {
                    Class<?> clazz = Game.getClient().getClass().getClassLoader().loadClass("jagdx.IDirect3DDevice");
                    Method method = clazz.getDeclaredMethod("CreateVertexDeclaration", long.class, long.class);
                    method.setAccessible(true);
                    D3DFVF_CUSTOM = (long) method.invoke(null, var0, bufferLoc);
                } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            if (VER_BUFFER.hasRemaining()) {
                VER_BUFFER.order(ByteOrder.nativeOrder());
                VER_BUFFER.putFloat(1.5F);
                VER_BUFFER.putFloat(1.5F);
//            VER_BUFFER.putFloat(0F);
//            VER_BUFFER.putInt(Color.WHITE.getRGB());

                VER_BUFFER.putFloat(1.5F);
                VER_BUFFER.putFloat(.5F);
//            VER_BUFFER.putFloat(0F);
//            VER_BUFFER.putInt(Color.WHITE.getRGB());

                VER_BUFFER.putFloat(.5F);
                VER_BUFFER.putFloat(.5F);
//            VER_BUFFER.putFloat(0F);
//            VER_BUFFER.putInt(Color.WHITE.getRGB());
            }

//        System.out.println(D3DFVF_CUSTOM);

//            long vertBuffer = bufferHelper.getDirectBufferAddress(VER_BUFFER);
//            System.out.println(SetStreamSource(var0, 0, vertBuffer, 0, 8));
//            System.out.println(SetVertexDeclaration(var0, D3DFVF_CUSTOM));
//            System.out.println(DrawPrimitive(var0, 4, 0, 1));
        }

        try {
            Class<?> clazz = Game.getClient().getClass().getClassLoader().loadClass("jagdx.IDirect3DDevice");
            Method method = clazz.getDeclaredMethod("EndScene", long.class);
            method.setAccessible(true);
            return (int) method.invoke(null, var0);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static final ByteBuffer FVF_BUFFER = ByteBuffer.allocateDirect(16);
    private static final ByteBuffer VER_BUFFER = ByteBuffer.allocateDirect(24);

    private static long createBuffer(int bytes, Consumer<ByteBuffer> bufferConsumer) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(bytes);
        buffer.order(ByteOrder.nativeOrder());
        long bufferAddress = bufferHelper.getDirectBufferAddress(buffer);

        bufferConsumer.accept(buffer);

        return bufferAddress;
    }
}