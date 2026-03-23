package org.astrsomn.server.util;

import java.util.HashMap;
import java.util.Map;

public class UserContext {

    private static final ThreadLocal<Map<String, Object>> CONTEXT = ThreadLocal.withInitial(HashMap::new);

    private static final String USER_ID = "userId";
    private static final String USERNAME = "username";
    private static final String USER_ROLE = "userRole";
    private static final String TOKEN = "token";
    private static final String CLIENT_IP = "clientIp";

    public static void setUserId(Long userId) {
        CONTEXT.get().put(USER_ID, userId);
    }

    public static Long getUserId() {
        return (Long) CONTEXT.get().get(USER_ID);
    }

    public static void setUsername(String username) {
        CONTEXT.get().put(USERNAME, username);
    }

    public static String getUsername() {
        return (String) CONTEXT.get().get(USERNAME);
    }

    public static void setUserRole(String userRole) {
        CONTEXT.get().put(USER_ROLE, userRole);
    }

    public static String getUserRole() {
        return (String) CONTEXT.get().get(USER_ROLE);
    }

    public static void setToken(String token) {
        CONTEXT.get().put(TOKEN, token);
    }

    public static String getToken() {
        return (String) CONTEXT.get().get(TOKEN);
    }

    public static void setClientIp(String clientIp) {
        CONTEXT.get().put(CLIENT_IP, clientIp);
    }

    public static String getClientIp() {
        return (String) CONTEXT.get().get(CLIENT_IP);
    }

    public static void set(String key, Object value) {
        CONTEXT.get().put(key, value);
    }

    public static Object get(String key) {
        return CONTEXT.get().get(key);
    }

    public static void clear() {
        CONTEXT.remove();
    }

    public static Map<String, Object> getAll() {
        return new HashMap<>(CONTEXT.get());
    }
}
