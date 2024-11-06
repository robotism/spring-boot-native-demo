package com.gankcode.springboot.crypto.algorihtm;

import com.gankcode.springboot.crypto.base.BaseCipher;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 1、私钥用于签名、公钥用于验签
 * 2、公钥用于加密、私钥用于解密，这才能起到加密作用
 */
@Slf4j
public class Rsa extends BaseCipher {

    private final Charset charset;
    private final Base64 base64;

    public Rsa(String transformation) {
        this(transformation, StandardCharsets.UTF_8);
    }

    public Rsa(String transformation, final Charset charset) {
        super(transformation);
        this.charset = charset;
        this.base64 = new Base64(charset);
    }

    @Override
    protected Key getKey(int type, byte[] keyBytes) throws Exception {

        final KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        if (type == Cipher.PRIVATE_KEY) {
            final KeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            return keyFactory.generatePrivate(keySpec);
        }

        if (type == Cipher.PUBLIC_KEY) {
            final KeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            return keyFactory.generatePublic(keySpec);
        }

        throw new Exception("Rsa Key type must be one of [Cipher.PRIVATE_KEY,Cipher.PUBLIC_KEY]");

    }

    @Override
    protected AlgorithmParameterSpec getAlgorithmParameterSpec() throws Exception {
        return null;
    }


    public String encrypt(final String data, final String publicKey) {
        try {
            return encrypt(data, publicKey, Cipher.PUBLIC_KEY);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }


    public String encrypt(final String data, final String key, final int type) throws Exception {
        final byte[] keyBytes = base64.decode(key);
        final Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, type, keyBytes);

        final byte[] dataBytes = data.getBytes(charset);
        final byte[] cipherBytes = compute(cipher, dataBytes);
        final byte[] base64Encode = base64.encode(cipherBytes);
        return new String(base64Encode, charset);
    }

    public String decrypt(final String data, final String privateKey) {
        try {
            return decrypt(data, privateKey, Cipher.PRIVATE_KEY);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    public String decrypt(final String data, final String key, final int type) throws Exception {
        final byte[] keyBytes = base64.decode(key);
        final Cipher cipher = getCipher(Cipher.DECRYPT_MODE, type, keyBytes);

        final byte[] dataBytes = data.getBytes(charset);
        final byte[] base64Decode = base64.decode(dataBytes);
        final byte[] cipherBytes = compute(cipher, base64Decode);
        return new String(cipherBytes, charset);
    }


}
