package com.astrsomn.starter.runtime.plugin;

import java.net.URL;
import java.net.URLClassLoader;

public class PluginClassLoader extends URLClassLoader {

    public PluginClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> c = findLoadedClass(name);

            if (c == null) {
                // 核心契约类强制由父加载器（主程序）加载，确保接口一致性
                if (isCoreClass(name)) {
                    return super.loadClass(name, resolve);
                }

                try {
                    // 插件优先逻辑：先尝试从插件自身的 JAR 包中寻找类
                    c = findClass(name);
                } catch (ClassNotFoundException e) {
                    // 插件内未找到时，回归双亲委派标准逻辑
                    c = super.loadClass(name, resolve);
                }
            }

            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }

    private boolean isCoreClass(String name) {
        return name.startsWith("com.astrsomn.core")
                || name.startsWith("dev.langchain4j")
                || name.startsWith("org.slf4j");
    }
}