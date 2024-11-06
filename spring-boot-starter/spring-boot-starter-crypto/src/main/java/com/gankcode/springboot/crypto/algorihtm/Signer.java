package com.gankcode.springboot.crypto.algorihtm;


import lombok.extern.slf4j.Slf4j;
import org.springframework.util.FileCopyUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Locale;


@Slf4j
public class Signer {

    private final String encryptAlgorithm;
    private final String signatureAlgorithm;
    private final Charset charset;
    private final Base64 base64;

    public Signer(String algorithm) {
        this(algorithm, StandardCharsets.UTF_8);
    }

    public Signer(String algorithm, Charset charset) {
        this.encryptAlgorithm = algorithm.trim()
                .toLowerCase(Locale.SIMPLIFIED_CHINESE)
                .split("with")[1]
                .toUpperCase(Locale.SIMPLIFIED_CHINESE);
        this.signatureAlgorithm = algorithm.trim();
        this.charset = charset;
        this.base64 = new Base64(charset);
    }


    public String signature(String content, String privateKey) {
        return signature(content, privateKey, charset);
    }

    public String signature(String content, String privateKey, Charset charset) {
        if (content == null || privateKey == null || charset == null) {
            return null;
        }
        try {
            PrivateKey priKey = getPrivateKeyFromPkcs8(new ByteArrayInputStream(privateKey.getBytes(charset)));
            Signature signature = Signature.getInstance(signatureAlgorithm);
            signature.initSign(priKey);
            signature.update(content.getBytes(charset));
            return base64.encodeToString(signature.sign());
        } catch (Exception ex) {
            log.error("", ex);
        }
        return null;
    }


    public boolean verify(String content, String sign, String publicKey) {
        return verify(content, sign, publicKey, charset);
    }

    public boolean verify(String content, String sign, String publicKey, Charset charset) {
        if (content == null || sign == null || publicKey == null || charset == null) {
            return false;
        }
        try {
            PublicKey pubKey = getPublicKeyFromX509(new ByteArrayInputStream(publicKey.getBytes(charset)));
            Signature signature = Signature.getInstance(signatureAlgorithm);
            signature.initVerify(pubKey);
            signature.update(content.getBytes(charset));
            return signature.verify(base64.decode(sign.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            log.error("", ex);
        }
        return false;
    }


    private PrivateKey getPrivateKeyFromPkcs8(InputStream is) {
        try {
            final byte[] key = FileCopyUtils.copyToByteArray(is);
            final byte[] bytes = base64.decode(key);
            final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
            final KeyFactory keyFactory = KeyFactory.getInstance(encryptAlgorithm);
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }


    private PublicKey getPublicKeyFromX509(InputStream is) {
        try {
            final byte[] key = FileCopyUtils.copyToByteArray(is);
            final byte[] encodedKey = base64.decode(key);
            final KeyFactory keyFactory = KeyFactory.getInstance(encryptAlgorithm);
            return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }


}
