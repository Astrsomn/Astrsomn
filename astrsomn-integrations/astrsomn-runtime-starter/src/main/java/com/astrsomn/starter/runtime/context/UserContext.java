package com.astrsomn.starter.runtime.context;


public class UserContext {

    private static ThreadLocal<String> username = new ThreadLocal<>();
    private static ThreadLocal<Long> userId = new ThreadLocal<>();

    
    public static String getUsername() {
        return username.get();
    }

    
    public static void setUsername(String name) {
        username.set(name);
    }

    
    public static Long getUserId() {
        return userId.get();
    }

    
    public static void setUserId(Long id) {
        userId.set(id);
    }

    
    public static void clear() {
        username.remove();
        userId.remove();
    }
}
