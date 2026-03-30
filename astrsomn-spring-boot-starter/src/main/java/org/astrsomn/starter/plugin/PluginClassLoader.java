package org.astrsomn.starter.plugin;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * 插件类加载器：实现“插件优先”逻辑，打破双亲委派
 */
public class PluginClassLoader extends URLClassLoader {

    public PluginClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // 1. 检查类是否已经加载
            Class<?> c = findLoadedClass(name);

            if (c == null) {
                // 2. 关键：如果是核心契约类（Core 模块里的），必须交给父加载器
                // 否则会出现 ClassCastException，因为接口和实现类必须由同一个“祖先”加载
                if (name.startsWith("org.astrsomn.core")) {
                    return super.loadClass(name, resolve);
                }

                try {
                    // 3. 尝试插件优先：先从插件自己的 JAR 包里找
                    c = findClass(name);
                } catch (ClassNotFoundException e) {
                    // 4. 插件里没有，再按常规逻辑走（交给父类）
                    c = super.loadClass(name, resolve);
                }
            }

            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }
}