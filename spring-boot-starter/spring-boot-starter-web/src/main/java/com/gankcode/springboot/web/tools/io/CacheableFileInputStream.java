package com.gankcode.springboot.web.tools.io;

import lombok.Getter;

import java.io.*;
import java.nio.file.Files;


public class CacheableFileInputStream extends InputStream {

    private final InputStream is;

    private final OutputStream os;

    @Getter
    private final File cache;

    public CacheableFileInputStream(InputStream is, File cached) throws IOException {
        this.is = is;
        this.cache = cached != null ? cached : Files.createTempFile(getClass().getName(), "").toFile();
        this.os = new FileOutputStream(cache);

        this.cache.deleteOnExit();
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
        final int size = is == null ? -1 : is.read(b, off, len);
        if (size > 0) {
            os.write(b, off, size);
        }
        return size;
    }

    @Override
    public synchronized void reset() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {
        if (is != null) {
            try {
                is.close();
            } catch (Exception ignored) {

            }
        }
        if (os != null) {
            try {
                os.flush();
                os.close();
            } catch (Exception ignored) {

            }
        }
    }
}
