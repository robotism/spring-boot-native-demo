package com.gankcode.springboot.crypto.base;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.regex.Pattern;

@Slf4j
public abstract class BaseCipher {

    protected final String algorithm;
    protected final String transformation;


    public BaseCipher(String transformation) {
        this.algorithm = transformation.split(Pattern.quote("/"))[0];
        this.transformation = transformation;
    }

    /**
     * 获取key
     *
     * @param type     类型
     * @param keyBytes 值
     * @return Key对象
     * @throws Exception 错误
     */
    protected abstract Key getKey(int type, byte[] keyBytes) throws Exception;

    /**
     * 获取算法参数定义
     *
     * @return AlgorithmParameterSpec对象
     * @throws Exception 错误
     */
    protected abstract AlgorithmParameterSpec getAlgorithmParameterSpec() throws Exception;

    protected Cipher getCipher(int mode, int type, byte[] keyBytes) throws Exception {
        final Cipher cipher = Cipher.getInstance(transformation);
        final Key key = getKey(type, keyBytes);
        final AlgorithmParameterSpec algorithmParameterSpec = getAlgorithmParameterSpec();
        if (algorithmParameterSpec == null) {
            cipher.init(mode, key);
        } else {
            cipher.init(mode, key, algorithmParameterSpec);
        }
        return cipher;
    }


    protected byte[] compute(Cipher cipher, byte[] bytes) throws Exception {
        final int totalSize = bytes.length;

        // final int blockSize = cipher.getBlockSize(); // 可能为0, 指定其他值会报错
        final int blockSize = cipher.getOutputSize(totalSize);

        final int blocks = (int) Math.ceil(1.0F * totalSize / blockSize);

        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            for (int i = 0; i < blocks; i++) {
                final int from = i * blockSize;
                final int to = from + blockSize;
                final int inputSize = Math.min(to, totalSize) - from;
                final byte[] seg = cipher.doFinal(bytes, from, inputSize);
                buffer.write(seg);
            }
            return buffer.toByteArray();
        }
    }

    public byte[] encrypt(byte[] plainText, byte[] key) throws Exception {
        final Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, Cipher.SECRET_KEY, key);
        return compute(cipher, plainText);
    }

    public byte[] decrypt(byte[] cipherText, byte[] key) throws Exception {
        final Cipher cipher = getCipher(Cipher.DECRYPT_MODE, Cipher.SECRET_KEY, key);
        return compute(cipher, cipherText);
    }
}
