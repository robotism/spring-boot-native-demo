package com.gankcode.springboot.crypto.algorihtm;


import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class Base64 {

    private final Charset charset;

    public static Base64 getDefault() {
        return new Base64();
    }

    public Base64() {
        this.charset = StandardCharsets.UTF_8;
    }

    public Base64(Charset charset) {
        this.charset = charset;
    }

    public byte[] decode(String data) {
        if (data == null) {
            return null;
        }
        return decode(data.getBytes(charset));
    }

    public byte[] decode(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        try {
            return java.util.Base64.getDecoder().decode(data);
        } catch (Exception ignored) {
            return data;
        }
    }

    public String decodeToString(byte[] data) {
        final byte[] bytes = decode(data);
        if (bytes == null) {
            return null;
        }
        return new String(bytes, charset);
    }

    public String decodeToString(String data) {
        if (data == null || data.length() == 0) {
            return null;
        }
        final byte[] bytes = decode(data.getBytes(charset));
        if (bytes == null) {
            return null;
        }
        return new String(bytes, charset);
    }

    public byte[] encode(String data) {
        if (data == null) {
            return null;
        }
        return encode(data.getBytes(charset));
    }

    public byte[] encode(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        try {
            return java.util.Base64.getEncoder().encode(data);
        } catch (Exception ignored) {
            return data;
        }
    }

    public String encodeToString(byte[] data) {
        final byte[] bytes = encode(data);
        if (bytes == null) {
            return null;
        }
        return new String(bytes, charset);
    }

    public String encodeToString(String data) {
        if (data == null || data.length() == 0) {
            return null;
        }
        final byte[] bytes = encode(data.getBytes(charset));
        if (bytes == null) {
            return null;
        }
        return new String(bytes, charset);
    }

}
