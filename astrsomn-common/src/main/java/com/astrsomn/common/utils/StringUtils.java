package com.astrsomn.common.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * 字符串工具类
 * 侧重于高性能与业务常用逻辑
 */
public class StringUtils {

    public static final String EMPTY = "";

    /**
     * 判断字符串是否为空 (null 或 长度为0)
     */
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * 判断字符串是否不为空
     */
    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * 判断字符串是否为空白 (null, 长度为0, 或全是空格)
     */
    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否不为空白
     */
    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    /**
     * 安全截取字符串，防止越界
     */
    public static String substring(String str, int start, int end) {
        if (str == null) return EMPTY;
        if (end < 0) end = str.length() + end;
        if (start < 0) start = str.length() + start;
        if (end > str.length()) end = str.length();
        if (start > end) return EMPTY;
        return str.substring(start, end);
    }

    /**
     * 下划线转驼峰 (例如: user_name -> userName)
     */
    public static String toCamelCase(String s) {
        if (s == null) return null;
        s = s.toLowerCase();
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '_') {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰转下划线 (例如: userName -> user_name)
     */
    public static String toUnderlineCase(String s) {
        if (s == null) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append("_").append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 集合/Map 判空辅助（虽然不是String，但工具类常备）
     */
    public static boolean isAnyEmpty(Object obj) {
        if (obj == null) return true;
        if (obj instanceof CharSequence) return isEmpty((CharSequence) obj);
        if (obj instanceof Collection) return ((Collection<?>) obj).isEmpty();
        if (obj instanceof Map) return ((Map<?, ?>) obj).isEmpty();
        if (obj instanceof Object[]) return ((Object[]) obj).length == 0;
        return false;
    }

    /**
     * 获取默认值
     */
    public static String defaultIfBlank(String str, String defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }

    /**
     * 修剪字符串：
     * 1. 如果输入为 null，返回 null
     * 2. 如果 trim 后为空字符串 ""，返回 null
     * 3. 否则返回 trim 后的结果
     */
    public static String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String result = s.trim();
        return result.isEmpty() ? null : result;
    }

    /**
     * 安全比较两个字符串是否相等
     * 1. 都是 null 返回 true
     * 2. 其中一个是 null 返回 false
     * 3. 都不为 null 则比较内容
     */
    public static boolean equals(String s1, String s2) {
        // 技巧：利用 Objects.equals 或手动逻辑
        return (s1 == s2) || (s1 != null && s1.equals(s2));
    }


    /**
     * 忽略大小写比较两个字符串是否相等
     */
    public static boolean equalsIgnoreCase(String s1, String s2) {
        return (s1 == s2) || (s1 != null && s1.equalsIgnoreCase(s2));
    }

    public static String trim(String extensionKey) {
        // 1. 先进行判空检查，防止抛出 NullPointerException
        if (extensionKey == null) {
            return null;
        }
        // 2. 调用 String 类自带的 trim() 方法，去除首尾空白符（如空格、\n、\r 等）
        return extensionKey.trim();
    }


    public static String normalize(String s) {
        return Optional.ofNullable(s).map(String::trim).orElse("");
    }
}
