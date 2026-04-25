package com.astrsomn.starter.context;

/**
 * 请求级「当前工作空间环境」编码，由网关/前端请求头解析后写入，供租户插件与写入填充使用。
 */
public final class EnvScope {

    private static final ThreadLocal<String> CODE = new ThreadLocal<>();

    private EnvScope() {
    }

    public static void set(String envCode) {
        CODE.set(envCode);
    }

    public static String get() {
        return CODE.get();
    }

    public static void clear() {
        CODE.remove();
    }
}
