package com.gankcode.springboot.crypto.base;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.regex.Pattern;


@Slf4j
@RequiredArgsConstructor
public abstract class BaseDigest {

    private final String algorithm;

    private final int length;

    public boolean isLike(final String data) {
        if (data != null && data.length() == length) {
            final String regex = "^[a-fA-F0-9]{" + length + "}$";
            final Pattern pattern = Pattern.compile(regex);
            return pattern.matcher(data).find();
        }
        return false;
    }


    private MessageDigest getDigest() {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            log.error("", e);
        }
        return null;
    }

    private String digest(final MessageDigest digest) {
        final BigInteger bigInt = new BigInteger(1, digest.digest());
        final String sha256 = bigInt.toString(16).toLowerCase(Locale.US);
        if (sha256.length() == length) {
            return sha256;
        } else {
            return String.format("%0" + (length - sha256.length()) + "d", 0) + sha256;
        }
    }


    public String compute(final byte[] bytes) {
        final MessageDigest messagedigest = getDigest();
        if (messagedigest == null || bytes == null) {
            return null;
        }
        messagedigest.update(bytes);
        return digest(messagedigest);
    }

    public String compute(final String s) {
        if (s == null) {
            return null;
        }
        return compute(s.getBytes(StandardCharsets.UTF_8));
    }


    public String compute(final File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        final MessageDigest messagedigest = getDigest();
        if (messagedigest == null) {
            return null;
        }
        try (FileInputStream is = new FileInputStream(file)) {
            final byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                messagedigest.update(buffer, 0, len);
            }
            return digest(messagedigest);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }


}
