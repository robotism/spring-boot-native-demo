package com.gankcode.springboot.crypto;


import com.gankcode.springboot.crypto.algorihtm.*;

import java.nio.charset.StandardCharsets;


public final class Crypto {
    private static final Crc32 CRYPTO_CRC32 = new Crc32();

    private static final Base64 CRYPTO_BASE64 = new Base64(StandardCharsets.UTF_8);

    private static final Md5 CRYPTO_MD5 = new Md5();
    private static final Sha1 CRYPTO_SHA1 = new Sha1();
    private static final Sha256 CRYPTO_SHA256 = new Sha256();
    private static final Sha512 CRYPTO_SHA512 = new Sha512();

    private static final Aes CRYPTO_AES = new Aes("AES/ECB/PKCS5Padding");
    private static final Rsa CRYPTO_RSA = new Rsa("RSA/ECB/PKCS1Padding");

    private static final Signer CRYPTO_SIGNER = new Signer("SHA1WithRSA");

    private Crypto() {

    }

    public static Aes aes() {
        return CRYPTO_AES;
    }

    public static Base64 base64() {
        return CRYPTO_BASE64;
    }

    public static Crc32 crc32() {
        return CRYPTO_CRC32;
    }

    public static Md5 md5() {
        return CRYPTO_MD5;
    }

    public static Sha1 sha1() {
        return CRYPTO_SHA1;
    }

    public static Sha256 sha256() {
        return CRYPTO_SHA256;
    }

    public static Sha512 sha512() {
        return CRYPTO_SHA512;
    }

    public static Rsa rsa() {
        return CRYPTO_RSA;
    }

    public static Signer signer() {
        return CRYPTO_SIGNER;
    }
}
