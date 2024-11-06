package com.gankcode.springboot.web.tools.io;

import lombok.extern.slf4j.Slf4j;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;



@Slf4j
public class ProgressInputStream extends FilterInputStream {

    private final long total;

    private long counted;

    private long progress;

    public ProgressInputStream(InputStream in, long total) {
        super(in);
        this.total = total;
    }


    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        final int size = super.read(b, off, len);
        if (size > 0) {
            counted += size;

            final long p = counted * 100 / total;
            if (progress != p) {
                log.info("progress {} / {} --- {}", counted, total, p);
                progress = p;
            }
        }
        return size;
    }
}
