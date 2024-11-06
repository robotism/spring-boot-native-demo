package com.gankcode.springboot.web.http.pageable;

import com.gankcode.springboot.web.utils.HttpUtils;
import io.undertow.servlet.spec.HttpServletRequestImpl;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Pattern;


public final class PageRequestUtils {

    public static Map<String, Deque<String>> getLinkedQueryParameters(HttpServletRequestImpl request) {
        final String queryString = HttpUtils.urlDecode(request.getExchange().getQueryString());
        final String[] queries = queryString.split(Pattern.quote("&"));
        final LinkedHashMap<String, Deque<String>> map = new LinkedHashMap<>(queries.length);

        for (String query : queries) {
            if (!StringUtils.hasText(query)) {
                continue;
            }
            final int pos = query.indexOf('=');
            final String key = pos < 0 ? query : query.substring(0, pos).trim();
            final String value = pos < 0 ? "" : query.substring(pos + 1).trim();

            final Deque<String> deque = map.computeIfAbsent(key, f -> new ArrayDeque<>());
            deque.add(value);
        }
        return map;
    }

    public static List<String> getLinkedQueryParameterNames(HttpServletRequestImpl request) {
        final String queryString = HttpUtils.urlDecode(request.getExchange().getQueryString());
        final String[] queries = queryString.split(Pattern.quote("&"));
        final List<String> list = new ArrayList<>(queries.length);

        for (String query : queries) {
            if (!StringUtils.hasText(query)) {
                continue;
            }
            list.add(query.split("=")[0].trim());
        }
        return list;
    }

}
