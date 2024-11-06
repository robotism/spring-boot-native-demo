package com.gankcode.springboot.web.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class SqlUtils {

    private SqlUtils() {
    }

    /**
     * MySQL需要转义的字段：\ % _
     */
    private static final Pattern PATTERN_MYSQL_ESCAPE_CHARS = Pattern.compile("(['_%\\\\]{1})");

    private static final Pattern PATTERN_FIND_DUPLICATE_ENTRY = Pattern.compile("Duplicate entry '(.*)' for");

    /**
     * 在SQL进行like时使用 ，mysql like时，参数使用传值 SqlUtils.convertToSQLSafeValue(String)； 禁止与escape 同时使用。
     * <p>
     * 转义mysql的特殊字符 包括 '\', '%', '_', ''',
     *
     * @param obj
     * @return 返回可能为null eg:
     * 1'2_3%4\ 5 ?\ 转义后  1\'2\_3\%4\\\\ 5 ?\\\\
     * null >> null
     * """ >> ""
     * "%" >> "\%"
     * "\" >> "\\\\\"
     * "_" >> "\_"
     * "_%" >> "\_\%"
     */
    public static Object escape(Object obj) {
        if (!(obj instanceof String)) {
            return obj;
        }
        final Matcher matcher = PATTERN_MYSQL_ESCAPE_CHARS.matcher((String) obj);
        int charSplitStart = 0;
        if (!matcher.find()) {
            return obj;
        }
        final StringBuilder sb = new StringBuilder();
        matcher.reset();
        while (matcher.find()) {
            String ch = ((String) obj).substring(matcher.start(), matcher.end());
            sb.append((String) obj, charSplitStart, matcher.start())
                    .append('\\').append("\\".equals(ch) ? "\\\\\\" : ch);
            charSplitStart = matcher.end();
        }
        if (sb.length() == 0) {
            return obj;
        } else {
            return sb.toString();
        }
    }

    public static String findDuplicateEntry(DuplicateKeyException exception) {
        if (exception == null || exception.getMessage() == null) {
            return null;
        }
        try {
            final Matcher matcher = PATTERN_FIND_DUPLICATE_ENTRY.matcher(exception.getMessage());
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }
}
