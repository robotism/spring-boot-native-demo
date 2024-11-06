
package com.gankcode.springboot.i18n.config;


import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A properties implementation that remembers the order of its entries.
 */
public class LinkedProperties extends Properties {

    private final Map<Object, Object> map = new LinkedHashMap<>();


    @Override
    public synchronized int hashCode() {
        return map.hashCode();
    }

    @Override
    public synchronized boolean equals(Object o) {
        if (o instanceof LinkedProperties) {
            return map.equals(((LinkedProperties) o).map);
        }
        return false;
    }

    @Override
    public synchronized Object put(Object key, Object value) {
        return map.put(key, value);
    }

    @Override
    public synchronized Object get(Object key) {
        return map.get(key);
    }

    @Override
    public synchronized void clear() {
        map.clear();
    }


    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public synchronized boolean contains(Object value) {
        return containsValue(value);
    }

    @Override
    public synchronized boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public synchronized Enumeration elements() {
        return new IteratorEnumeration(map.values().iterator());
    }

    @Override
    public Set entrySet() {
        return map.entrySet();
    }


    @Override
    public synchronized boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public synchronized Enumeration keys() {
        return new IteratorEnumeration(map.keySet().iterator());
    }

    @Override
    public Set keySet() {
        return map.keySet();
    }

    @Override
    public Enumeration propertyNames() {
        return new IteratorEnumeration(map.keySet().iterator());
    }

    @Override
    public Set<String> stringPropertyNames() {
        final Set<String> set = new LinkedHashSet<>();
        map.keySet().forEach(o -> set.add(String.valueOf(o)));
        return set;
    }

    @Override
    public synchronized void putAll(Map t) {
        map.putAll(t);
    }

    @Override
    public synchronized Object remove(Object key) {
        return map.remove(key);
    }

    @Override
    public synchronized int size() {
        return map.size();
    }

    @Override
    public Collection values() {
        return map.values();
    }

    @Override
    public synchronized Object getOrDefault(Object key, Object defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return String.valueOf(map.getOrDefault(key, defaultValue));
    }

    @Override
    public synchronized Object putIfAbsent(Object key, Object value) {
        return map.putIfAbsent(key, value);
    }

    @Override
    public synchronized Object compute(Object key, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return map.compute(key, remappingFunction);
    }

    @Override
    public synchronized Object computeIfPresent(Object key, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return map.computeIfPresent(key, remappingFunction);
    }

    @Override
    public synchronized Object computeIfAbsent(Object key, Function<? super Object, ?> mappingFunction) {
        return map.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public synchronized boolean remove(Object key, Object value) {
        return map.remove(key, value);
    }

    @Override
    public synchronized boolean replace(Object key, Object oldValue, Object newValue) {
        return map.replace(key, oldValue, newValue);
    }

    @Override
    public synchronized Object replace(Object key, Object value) {
        return map.replace(key, value);
    }

    @Override
    public synchronized void replaceAll(BiFunction<? super Object, ? super Object, ?> function) {
        map.replaceAll(function);
    }

    @Override
    public synchronized Object merge(Object key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return map.merge(key, value, remappingFunction);
    }

    @Override
    public String getProperty(String key) {
        final Object oval = get(key);
        final String sval = (oval instanceof String) ? (String) oval : null;
        return ((sval == null) && (defaults != null)) ? defaults.getProperty(key) : sval;
    }
}
