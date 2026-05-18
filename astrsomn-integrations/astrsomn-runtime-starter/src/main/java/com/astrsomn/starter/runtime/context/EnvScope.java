package com.astrsomn.starter.runtime.context;


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
