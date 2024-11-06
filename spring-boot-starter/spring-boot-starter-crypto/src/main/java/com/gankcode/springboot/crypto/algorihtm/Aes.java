package com.gankcode.springboot.crypto.algorihtm;

import com.gankcode.springboot.crypto.base.BaseCipher;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;


@Slf4j
public class Aes extends BaseCipher {

    private final Charset charset;
    private final Base64 base64;

    public Aes(String transformation) {
        this(transformation, StandardCharsets.UTF_8);
    }

    public Aes(String transformation, Charset charset) {
        super(transformation);
        this.charset = charset;
        this.base64 = new Base64(charset);
    }

    @Override
    protected Key getKey(int type, byte[] keyBytes) throws Exception {
        if (type == Cipher.SECRET_KEY) {
            return new SecretKeySpec(keyBytes, algorithm);
        }

        throw new Exception("Aes Key type must be one of [Cipher.SECRET_KEY]");
    }

    @Override
    protected AlgorithmParameterSpec getAlgorithmParameterSpec() throws Exception {
        return null;
    }

    public String encrypt(final String plainText, final String key) {
        if (plainText == null || key == null) {
            return null;
        }
        try {
            final byte[] cipherText = encrypt(plainText.getBytes(charset), key.getBytes(charset));
            final byte[] base64Encode = base64.encode(cipherText);
            return new String(base64Encode, charset);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    public String decrypt(final String cipherText, final String key) {
        if (cipherText == null || key == null) {
            return null;
        }
        try {
            final byte[] base64Decode = base64.decode(cipherText.getBytes(charset));
            final byte[] plainText = decrypt(base64Decode, key.getBytes(charset));
            return new String(plainText, charset);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

}
