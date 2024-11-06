package com.gankcode.springboot.web.tools.io;

import lombok.Data;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;


public class VerifiableFileInputStream extends InputStream {

    private final InputStream is;

    private final FileDigest digest;

    private String md5;
    private String sha1;
    private String sha256;
    private String sha512;

    public VerifiableFileInputStream(InputStream is) {
        this.is = is;
        this.digest = new FileDigest();
    }

    public VerifiableFileInputStream(InputStream is, FileDigestState state) {
        this.is = is;
        this.digest = new FileDigest(state);
    }

    public void compute() {
        this.md5 = digest.getMd5();
        this.sha1 = digest.getSha1();
        this.sha256 = digest.getSha256();
        this.sha512 = digest.getSha512();
    }

    public String getMd5() {
        if (!StringUtils.hasText(md5)) {
            this.md5 = digest.getMd5();
        }
        return md5;
    }

    public String getSha1() {
        if (!StringUtils.hasText(sha1)) {
            this.sha1 = digest.getSha1();
        }
        return sha1;
    }

    public String getSha256() {
        if (!StringUtils.hasText(sha256)) {
            this.sha256 = digest.getSha256();
        }
        return sha256;
    }

    public String getSha512() {
        if (!StringUtils.hasText(sha512)) {
            this.sha512 = digest.getSha512();
        }
        return sha512;
    }

    public Map<String, String> check(String md5, String sha1, String sha256, String sha512) {
        final Map<String, String> map = new LinkedHashMap<>();
        if (!StringUtils.hasText(md5) || !md5.equalsIgnoreCase(getMd5())) {
            map.put("MD5", md5);
        }
        if (!StringUtils.hasText(sha1) || !sha1.equalsIgnoreCase(getSha1())) {
            map.put("SHA1", sha1);
        }
        if (!StringUtils.hasText(sha256) || !sha256.equalsIgnoreCase(getSha256())) {
            map.put("SHA256", sha256);
        }
        if (!StringUtils.hasText(sha256) || !sha512.equalsIgnoreCase(getSha512())) {
            map.put("SHA512", sha256);
        }
        return map;
    }

    @Override
    public int read() throws IOException {
        if (is != null) {
            return is.read();
        }
        return 0;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (is == null) {
            return -1;
        }
        if (len > 0) {
            final int size = is.read(b, off, len);
            digest.update(b, off, size);
            return size;
        } else {
            return 0;
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        if (is != null) {
            is.reset();
        }
        digest.reset();
    }

    @Override
    public void close() throws IOException {
        if (is != null) {
            is.close();
        }
    }

    @Data
    public static final class FileDigest {

        private final MD5Digest md5;
        private final SHA1Digest sha1;
        private final SHA256Digest sha256;
        private final SHA512Digest sha512;

        public FileDigest() {
            this(null);
        }

        public FileDigest(FileDigestState state) {
            if (state == null) {
                this.md5 = new MD5Digest();
                this.sha1 = new SHA1Digest();
                this.sha256 = new SHA256Digest();
                this.sha512 = new SHA512Digest();
            } else {
                this.md5 = new MD5Digest(state.md5);
                this.sha1 = new SHA1Digest(state.sha1);
                this.sha256 = new SHA256Digest(state.sha256);
                this.sha512 = new SHA512Digest(state.sha512);
            }
        }

        public void reset() {
            this.md5.reset();
            this.sha1.reset();
            this.sha256.reset();
            this.sha512.reset();
        }

        public void update(byte[] b, int off, int size) {
            if (size > 0) {
                this.md5.update(b, off, size);
                this.sha1.update(b, off, size);
                this.sha256.update(b, off, size);
                this.sha512.update(b, off, size);
            }
        }

        private String fixedBits(String data, int len) {
            if (data == null) {
                return null;
            }
            final int fixed = len - data.length();
            if (fixed == 0) {
                return data;
            } else {
                return String.format("%0" + fixed + "d%s", 0, data);
            }
        }

        private String compute(Digest digest, int len) {
            final byte[] buffer = new byte[digest.getDigestSize()];
            digest.doFinal(buffer, 0);
            final String value = Hex.toHexString(buffer).toUpperCase();
            return fixedBits(value, len);
        }

        public String getMd5() {
            return compute(md5, 32);
        }

        public String getSha1() {
            return compute(sha1, 40);
        }

        public String getSha256() {
            return compute(sha256, 64);
        }

        public String getSha512() {
            return compute(sha512, 128);
        }

        public FileDigestState getState() {
            final FileDigestState state = new FileDigestState();
            state.md5 = md5.getEncodedState();
            state.sha1 = sha1.getEncodedState();
            state.sha256 = sha256.getEncodedState();
            state.sha512 = sha512.getEncodedState();
            return state;
        }

    }

    @Data
    public static final class FileDigestState {
        private byte[] md5;
        private byte[] sha1;
        private byte[] sha256;
        private byte[] sha512;
    }
}
