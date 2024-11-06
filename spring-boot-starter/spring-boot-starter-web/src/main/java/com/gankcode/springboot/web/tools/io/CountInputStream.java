package com.gankcode.springboot.web.tools.io;

import com.gankcode.springboot.utils.Utils;
import com.gankcode.springboot.web.bean.UnitSize;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;



@Slf4j
public class CountInputStream extends FilterInputStream {

    @Getter
    private long counted;
    @Getter
    private long skipped;

    private boolean closed;

    public CountInputStream(InputStream in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        final int size = super.read();
        if (size > 0) {
            counted += size;
        }
        return size;
    }


    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        final int size = super.read(b, off, len);
        if (size > 0) {
            counted += size;
        }
        return size;
    }

    @Override
    public long skip(long n) throws IOException {
        final long size = super.skip(n);
        if (size > 0) {
            skipped += size;
        }
        return size;
    }

    @Override
    public synchronized void reset() throws IOException {
        super.reset();
        counted = 0;
        skipped = 0;
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (closed) {
            return;
        }
        closed = true;
        if (Utils.isTest()) {
            log.info("Count InputStream : count = {}, skip = {}", new UnitSize(counted), new UnitSize(skipped));
        }
    }
}
