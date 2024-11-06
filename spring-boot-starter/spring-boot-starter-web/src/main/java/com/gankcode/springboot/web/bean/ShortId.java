package com.gankcode.springboot.web.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;


@Getter
@AllArgsConstructor
public class ShortId {

    private static final String SALT_NUM = "0123456789";
    private static final String SALT_ABC_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String SALT_ABC_LOWER = "abcdefghijklmnopqrstuvwxyz";
    /**
     * 数字+大写字母
     */
    public static final String SALT_36 = SALT_NUM + SALT_ABC_UPPER;
    /**
     * 大小写字母
     */
    public static final String SALT_52 = SALT_ABC_UPPER + SALT_ABC_LOWER;
    /**
     * 数字+ 大小写字母
     */
    public static final String SALT_62 = SALT_NUM + SALT_ABC_UPPER + SALT_ABC_LOWER;

    /**
     * 十进制无符号整数
     */
    private final long id;

    private final String short36;
    private final String short52;
    private final String short62;


    public ShortId(long id) {
        this.id = id;
        this.short36 = encode(id, SALT_36);
        this.short52 = encode(id, SALT_52);
        this.short62 = encode(id, SALT_62);
    }

    public static String encode(Long id, String salt) {
        if (id == null || id < 1 || !StringUtils.hasText(salt)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (; id > 0; id /= salt.length()) {
            sb.append(salt.charAt((int) (id % salt.length())));
        }
        return sb.reverse().toString();
    }

    public static Long decode(String shortId, String salt) {
        if (!StringUtils.hasText(shortId) || !StringUtils.hasText(salt)) {
            return null;
        }
        shortId = new StringBuilder(shortId).reverse().toString();
        long id = 0;
        for (int i = 0; i < shortId.length(); i++) {
            final int pos = salt.indexOf(shortId.charAt(i));
            if (pos < 0) {
                return null;
            }
            id += (long) (pos * Math.pow(salt.length(), i));
        }

        return id;
    }

}
