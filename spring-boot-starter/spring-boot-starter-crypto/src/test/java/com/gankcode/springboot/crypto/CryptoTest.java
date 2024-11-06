package com.gankcode.springboot.crypto;

import com.gankcode.springboot.crypto.algorihtm.Base64;
import com.gankcode.springboot.test.BaseJunitTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


@Slf4j
@ExtendWith(SpringExtension.class)
public class CryptoTest extends BaseJunitTest {

    @Test
    @DisplayName("AES 加密")
    public void base64() {
        final String data = "123";
        final String base64 = "MTIz";

        assert base64.equals(Base64.getDefault().encodeToString(data));
        assert data.equals(Base64.getDefault().decodeToString(base64));
    }


    @Test
    @DisplayName("AES 加密")
    public void aes() {

        final Map<String, String> kv = new HashMap<>();
        kv.put("1234567812345678", "abc"); // AES-128 , key len = 128/8 = 16
        kv.put("12345678123456781234567812345678", "abc"); // AES-256 , key len = 256/8 = 32


        kv.forEach((key, data) -> {
            final String encode = Crypto.aes().encrypt(data, key);
            final String decode = Crypto.aes().decrypt(encode, key);

            log.debug(
                    """
                    AES :
                    encode  '{}' => '{}' => '{}'
                    decode  '{}' => '{}' => '{}'
                    compare '{}' <=> '{}'
                    """,
                    data, key, encode,
                    encode, key, decode,
                    data, decode);

            assert StringUtils.hasText(encode);
            assert StringUtils.hasText(decode);
            assert data.equals(decode);
        });
    }

    /**
     * 生成密钥对
     *
     * @param bit 位数: 768, 1024, 2048, 4096 ...
     * @return 私钥和公钥: [private-key, public-key]
     */
    private String[] getKeys(int bit) {
        try {
            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(bit);
            final KeyPair keyPair = keyPairGenerator.generateKeyPair();

            final PrivateKey privateKey = keyPair.getPrivate();
            final String privateKeyBase64 = Base64.getDefault().encodeToString(privateKey.getEncoded());

            final PublicKey publicKey = keyPair.getPublic();
            final String publicKeyBase64 = Base64.getDefault().encodeToString(publicKey.getEncoded());
            return new String[]{privateKeyBase64, publicKeyBase64};
        } catch (Exception e) {
            log.error("", e);
        }
        return new String[0];
    }

    @Test
    @DisplayName("RSA 加密")
    public void rsa() {
        final char[] salt = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        final int[] bits = new int[]{768, 1024, 2048, 4096};
        for (int bit : bits) {
            final String[] keys = getKeys(bit);
            final String privateKey = keys[0];
            final String publicKey = keys[1];
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                sb.append(salt[(int) (System.nanoTime() % salt.length)]);
            }
            final String data = sb.toString();

            final String encrypt = Crypto.rsa().encrypt(data, publicKey);
            final String decrypt = Crypto.rsa().decrypt(encrypt, privateKey);

            log.debug("RSA (" + bit + "): " +
                    "\n" + data +
                    "\n↓↓↓" +
                    "\n" + encrypt +
                    "\n↑↑↑" +
                    "\n " + decrypt);
            assert encrypt != null;
            assert decrypt != null;
            assert decrypt.equals(data);

        }
    }

    @Test
    @DisplayName("MD5 加密")
    public void md5() throws Exception {
        final Map<String, String> mapStr = new LinkedHashMap<>();
        mapStr.put("123456", "E10ADC3949BA59ABBE56E057F20F883E");
        mapStr.put("wodemima", "fb2744d1e419813ade40ef0a97c5261f");
        mapStr.put("admin", "21232f297a57a5a743894a0e4a801fc3");
        for (Map.Entry<String, String> entry : mapStr.entrySet()) {
            final String str = entry.getKey();
            final String md5 = entry.getValue();
            final String tmp = Crypto.md5().compute(str);
            log.debug("MD5 : {}\n{}\n{}", str, md5, tmp);
            assert tmp.equalsIgnoreCase(md5);
        }

        final Map<String, String> mapFile = new LinkedHashMap<>();
        mapFile.put("123456", "E10ADC3949BA59ABBE56E057F20F883E");

        for (Map.Entry<String, String> entry : mapFile.entrySet()) {
            final String str = entry.getKey();
            final String md5 = entry.getValue();

            final File file = File.createTempFile(getClass().getName(), null);
            file.deleteOnExit();
            final BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(str);
            out.flush();
            out.close();

            final String tmp = Crypto.md5().compute(file);
            log.debug("MD5 : {}\n{}\n{}", str, md5, tmp);
            assert md5.equalsIgnoreCase(tmp);
        }

        final String md5 = Crypto.md5().compute("");

        assert Crypto.md5().compute((byte[]) null) == null;
        assert Crypto.md5().compute((String) null) == null;
        assert Crypto.md5().compute((File) null) == null;

        assert Crypto.md5().isLike(md5);
        assert !Crypto.md5().isLike("");
        assert !Crypto.md5().isLike(null);
        assert !Crypto.md5().isLike("qwertyuiopasdfghjklZxcvbnm1234");

    }

    @Test
    @DisplayName("签名校验")
    public void signer() {
        final String[] keys = getKeys(2048);
        final String privateKey = keys[0];
        final String publicKey = keys[1];
        String data = "123";
        final String sign = Crypto.signer().signature(data, privateKey);
        log.debug("sign= {} ", sign);
        assert sign != null;
        assert Crypto.signer().verify(data, sign, publicKey);
    }
}
