package com.gankcode.springboot.web.tools.io;

import java.io.IOException;
import java.io.InputStream;


public class BandWidthInputStream extends InputStream {

    private static final long BAND_LIMIT_INTERVAL = 1000;

    private final InputStream is;
    private final int bandWidth;

    private long lastReadTime;

    public BandWidthInputStream(InputStream is, int bandWidth) {
        this.is = is;
        this.bandWidth = bandWidth;
    }

    @Override
    public int read() throws IOException {
        if (is != null) {
            return is.read();
        }
        return -1;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (bandWidth <= 0 || bandWidth >= len) {
            return is.read(b, off, len);
        }
        final long now = System.currentTimeMillis();
        try {
            final long past = now - lastReadTime;
            if (past > 0 && past < BAND_LIMIT_INTERVAL) {
                Thread.sleep(past);
            }
            return is.read(b, off, bandWidth);
        } catch (InterruptedException e) {
            throw new IOException(e);
        } finally {
            lastReadTime = now;
        }
    }

}
