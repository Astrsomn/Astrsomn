package org.astrsomn.core.common.utils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 集合工具类
 * 提供集合判空、转换、提取等常用操作
 */
public class CollectionUtils {

    /**
     * 判断集合是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断集合是否不为空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 判断 Map 是否为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 判断 Map 是否不为空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 安全地获取集合的第一项
     */
    public static <T> T getFirst(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }
        if (collection instanceof List) {
            return ((List<T>) collection).get(0);
        }
        return collection.iterator().next();
    }

    /**
     * 将集合转换为指定属性的 List
     * 例如: 把 List<User> 转为 List<Long> (用户ID列表)
     */
    public static <T, R> List<R> transform(Collection<T> collection, Function<? super T, ? extends R> mapper) {
        if (isEmpty(collection)) {
            return Collections.emptyList();
        }
        return collection.stream()
                .map(mapper)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 将 List 转换为 Map
     * 例如: 把 List<User> 转为 Map<Long, User> (ID作为Key)
     */
    public static <K, V> Map<K, V> toMap(Collection<V> collection, Function<? super V, ? extends K> keyMapper) {
        if (isEmpty(collection)) {
            return Collections.emptyMap();
        }
        return collection.stream()
                .collect(Collectors.toMap(keyMapper, Function.identity(), (existing, replacement) -> existing));
    }

    /**
     * 强转集合（防止 null）
     * 如果为 null 则返回一个不可变的空集合，避免外部遍历时抛出 NPE
     */
    public static <T> Collection<T> emptyIfNull(Collection<T> collection) {
        return collection == null ? Collections.emptyList() : collection;
    }

    /**
     * 判断两个集合是否有交集
     */
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
