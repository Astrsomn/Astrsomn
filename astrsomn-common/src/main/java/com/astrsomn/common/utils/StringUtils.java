package com.astrsomn.common.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


public class StringUtils {

    public static final String EMPTY = "";


    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }


    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }


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


    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }


    public static String substring(String str, int start, int end) {
        if (str == null) return EMPTY;
        if (end < 0) end = str.length() + end;
        if (start < 0) start = str.length() + start;
        if (end > str.length()) end = str.length();
        if (start > end) return EMPTY;
        return str.substring(start, end);
    }


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


    public static boolean isAnyEmpty(Object obj) {
        if (obj == null) return true;
        if (obj instanceof CharSequence) return isEmpty((CharSequence) obj);
        if (obj instanceof Collection) return ((Collection<?>) obj).isEmpty();
        if (obj instanceof Map) return ((Map<?, ?>) obj).isEmpty();
        if (obj instanceof Object[]) return ((Object[]) obj).length == 0;
        return false;
    }


    public static String defaultIfBlank(String str, String defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }


    public static String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String result = s.trim();
        return result.isEmpty() ? null : result;
    }


    public static boolean equals(String s1, String s2) {
        return Objects.equals(s1, s2);
    }


    public static boolean equalsIgnoreCase(String s1, String s2) {
        return (s1 == s2) || (s1 != null && s1.equalsIgnoreCase(s2));
    }

    public static String trim(String extensionKey) {
        if (extensionKey == null) {
            return null;
        }
        return extensionKey.trim();
    }


    public static String normalize(String s) {
        return Optional.ofNullable(s).map(String::trim).orElse("");
    }
}