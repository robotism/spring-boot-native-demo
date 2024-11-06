package com.gankcode.springboot.web.bean;

import lombok.Data;

import java.text.DecimalFormat;


@Data
public final class UnitSize {

    private static final String[] UNITS = new String[]{
            "B",
            "KB",
            "MB",
            "GB",
            "TB",
            "PB",
            "EB",
            "ZB",
            "YB",
            "BB",
            "NB",
            "DB",
            "CB",
            "XB"
    };

    private final long size;

    @Override
    public String toString() {
        final DecimalFormat df = new DecimalFormat("###.000");
        final String flag = size < 0 ? "-" : "";
        final long abs = Math.abs(size);
        String last = size + UNITS[0];
        for (int i = 0; ; i++) {
            final double next = 1.0 * abs / Math.pow(1000, i);
            if (next < 1) {
                break;
            }
            if (i == UNITS.length - 1) {
                break;
            }
            last = df.format(next) + UNITS[i];
        }
        return flag + last;
    }
}