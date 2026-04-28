package com.astrsomn.starter.context;

/**
 * 用户上下文工具类，用于获取当前登录用户信息。
 * <p>
 * 默认实现返回空，具体项目可以通过 @Primary 注解覆盖此实现。
 */
public class UserContext {

    private static ThreadLocal<String> username = new ThreadLocal<>();
    private static ThreadLocal<Long> userId = new ThreadLocal<>();

    /**
     * 获取当前用户名
     */
    public static String getUsername() {
        return username.get();
    }

    /**
     * 设置当前用户名
     */
    public static void setUsername(String name) {
        username.set(name);
    }

    /**
     * 获取当前用户ID
     */
    public static Long getUserId() {
        return userId.get();
    }

    /**
     * 设置当前用户ID
     */
    public static void setUserId(Long id) {
        userId.set(id);
    }

    /**
     * 清除上下文
     */
    public static void clear() {
        username.remove();
        userId.remove();
    }
}
