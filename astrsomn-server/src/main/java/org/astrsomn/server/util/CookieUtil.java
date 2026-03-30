package org.astrsomn.server.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    private static final String DEFAULT_PATH = "/";
    private static final int DEFAULT_MAX_AGE = 7 * 24 * 60 * 60; // 7天

    public static void addCookie(HttpServletResponse response, String name, String value) {
        addCookie(response, name, value, DEFAULT_MAX_AGE, DEFAULT_PATH, false, false);
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        addCookie(response, name, value, maxAge, DEFAULT_PATH, false, false);
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge, String path) {
        addCookie(response, name, value, maxAge, path, false, false);
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge, 
                                 String path, boolean httpOnly, boolean secure) {
        // TODO: 添加Cookie
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        response.addCookie(cookie);
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        // TODO: 获取Cookie值
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void removeCookie(HttpServletResponse response, String name) {
        // TODO: 删除Cookie
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        cookie.setPath(DEFAULT_PATH);
        response.addCookie(cookie);
    }
}
