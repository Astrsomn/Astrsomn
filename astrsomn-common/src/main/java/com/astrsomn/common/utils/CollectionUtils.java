package com.astrsomn.common.utils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class CollectionUtils {

    
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    
    public static <T> T getFirst(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }
        if (collection instanceof List) {
            return ((List<T>) collection).get(0);
        }
        return collection.iterator().next();
    }

    
    public static <T, R> List<R> transform(Collection<T> collection, Function<? super T, ? extends R> mapper) {
        if (isEmpty(collection)) {
            return Collections.emptyList();
        }
        return collection.stream()
                .map(mapper)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    
    public static <K, V> Map<K, V> toMap(Collection<V> collection, Function<? super V, ? extends K> keyMapper) {
        if (isEmpty(collection)) {
            return Collections.emptyMap();
        }
        return collection.stream()
                .collect(Collectors.toMap(keyMapper, Function.identity(), (existing, replacement) -> existing));
    }

    
    public static <T> Collection<T> emptyIfNull(Collection<T> collection) {
        return collection == null ? Collections.emptyList() : collection;
    }

    
    public static boolean containsAny(Collection<?> source, Collection<?> candidates) {
        if (isEmpty(source) || isEmpty(candidates)) {
            return false;
        }
        for (Object candidate : candidates) {
            if (source.contains(candidate)) {
                return true;
            }
        }
        return false;
    }
}