package com.gankcode.springboot.crypto.algorihtm;


import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;


@Slf4j
public class Crc32 {

    public Long compute(final byte[] bytes) {
        final CRC32 crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        return crc32.getValue();
    }

    public Long compute(final String s) {
        if (s == null) {
            return null;
        }
        return compute(s.getBytes(StandardCharsets.UTF_8));
    }


    public Long compute(final File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        final CRC32 crc32 = new CRC32();
        try (FileInputStream is = new FileInputStream(file)) {
            final byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                crc32.update(buffer, 0, len);
            }
            return crc32.getValue();
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }


}
