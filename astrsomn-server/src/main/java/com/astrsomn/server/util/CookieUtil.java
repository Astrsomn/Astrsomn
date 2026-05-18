package com.astrsomn.server.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    @Value("${cookie.default-path:/}")
    private String defaultPath;

    @Value("${cookie.default-max-age:604800}")
    private int defaultMaxAge;

    public void addCookie(HttpServletResponse response, String name, String value) {
        addCookie(response, name, value, defaultMaxAge, defaultPath, false, false);
    }

    public void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        addCookie(response, name, value, maxAge, defaultPath, false, false);
    }

    public void addCookie(HttpServletResponse response, String name, String value, int maxAge, String path) {
        addCookie(response, name, value, maxAge, path, false, false);
    }

    public void addCookie(HttpServletResponse response, String name, String value, int maxAge,
                          String path, boolean httpOnly, boolean secure) {

        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        response.addCookie(cookie);
    }

    public String getCookieValue(HttpServletRequest request, String name) {
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

    public void removeCookie(HttpServletResponse response, String name) {
        // TODO: 删除Cookie
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        cookie.setPath(defaultPath);
        response.addCookie(cookie);
    }
}
