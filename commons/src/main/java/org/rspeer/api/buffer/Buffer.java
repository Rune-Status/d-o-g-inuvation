package org.rspeer.api.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by mdawg on 5/11/2017.
 */
public class Buffer {

    private byte[] payload;
    private int idx;

    public Buffer(byte[] payload) {
        this.payload = payload;
    }

    public Buffer() {
        this(new byte[0]);
    }

    public static Buffer create(InputStream inputStream) {
        try {
            int availiable = inputStream.available();
            if (availiable <= 0) return null;
            byte[] bytes = new byte[availiable];
            inputStream.read(bytes);
            return new Buffer(bytes);
        } catch (IOException e) {
            return null;
        }
    }

    private void extendArray(int len) {
        payload = Arrays.copyOf(payload, payload.length + len);
    }

    public byte getByte() {
        return payload[idx++];
    }

    public boolean getBoolean() {
        return getByte() == 1;
    }

    public int getInt() {
        return ((getByte() & 0xFF) << 24) |
                ((getByte() & 0xFF) << 16) |
                ((getByte() & 0xFF) << 8) |
                (getByte() & 0xFF);
    }

    public String getString() {
        String str = "";
        byte b;
        while ((b = getByte()) != (byte) 0x10) {
            str += (char) b;
        }
        return str;
    }

    public void putByte(byte b) {
        if (payload.length <= idx)
            extendArray(1);
        payload[idx++] = b;
    }

    public void putBoolean(boolean b) {
        putByte((byte) (b ? 1 : 0));
    }

    public void putInt(int i) {
        putByte((byte) ((i >> 24) & 0xFF));
        putByte((byte) ((i >> 16) & 0xFF));
        putByte((byte) ((i >> 8) & 0xFF));
        putByte((byte) (i & 0xFF));
    }

    public long getLong() {
        return (((long) getByte() & 0xFF) << 56) |
                (((long) getByte() & 0xFF) << 48) |
                (((long) getByte() & 0xFF) << 40) |
                (((long) getByte() & 0xFF) << 32) |
                (((long) getByte() & 0xFF) << 24) |
                (((long) getByte() & 0xFF) << 16) |
                (((long) getByte() & 0xFF) << 8) |
                ((long) getByte() & 0xFF);
    }

    public void putDouble(double d) {
        putLong(Double.doubleToRawLongBits(d));
    }

    public double getDouble() {
        return Double.longBitsToDouble(getLong());
    }

    public void read(InputStream stream) {
        try {
            payload = new byte[stream.available()];
            stream.read(payload);
        } catch (Exception e) {
        }
    }

    public void putLong(long l) {
        putInt((int) (l >> 32) & 0xFFFFFFFF);
        putInt((int) (l) & 0xFFFFFFFF);
    }

    public void putString(String str) {
        extendArray(str.getBytes().length + 1);
        for (byte c : str.getBytes()) {
            putByte(c);
        }
        putByte((byte) 0x10);
    }

    public byte[] getPayload() {
        return payload;
    }

    public byte[] getPayloadFromLen() {
        return getBytes(payload.length - idx);
    }

    public byte[] getBytes(int len) {
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[i] = getByte();
        }
        return bytes;
    }

    public void putBytes(byte[] random) {
        for (byte b : random) {
            putByte(b);
        }
    }

    public int length() {
        return payload.length;
    }

    public int getLocation() {
        return idx;
    }

    public void setLocation(int location) {
        this.idx = location;
    }

    public void reset() {
        idx = 0;
    }
}

