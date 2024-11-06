package com.gankcode.springboot.test;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ReporterOutputProxy {

    private final PrintStream OUT = System.out;
    private final PrintStream ERR = System.err;

    private final ByteArrayOutputStream BUFFER = new ByteArrayOutputStream();
    private final PrintStream WRITER = new PrintStream(BUFFER) {

        @Override
        public void write(int b) {
            try {
                OUT.write(b);
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.write(b);
        }

        @Override
        public void write(@NotNull byte[] buf, int off, int len) {
            try {
                OUT.write(buf, off, len);
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.write(buf, off, len);
        }
    };


    public synchronized void start() {
        BUFFER.reset();
        System.setOut(WRITER);
        System.setErr(WRITER);
    }

    public synchronized void stop() {
        System.setOut(OUT);
        System.setErr(ERR);
        BUFFER.reset();
    }

    public synchronized void reset() {
        BUFFER.reset();
    }

    @Override
    public String toString() {
        return BUFFER.toString();
    }
}
