package org.astrsomn.starter.plugin;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.langchain.extension.ModelProviderHandler;
import org.astrsomn.starter.langchain.factory.AstroModelFactory;
import org.springframework.stereotype.Component;


import java.io.File;
import java.net.URL;
import java.util.ServiceLoader;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class AstrsomnPluginManager {

    private final AstroModelFactory astroModelFactory;
    private final String pluginPath = "./plugins";

    // 用于记录已加载的插件及其加载器，方便后续做卸载或热更新
    private final Map<String, PluginClassLoader> pluginCache = new ConcurrentHashMap<>();
    private final Map<String, List<ModelProviderHandler>> pluginHandlers = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        reloadPlugins();
    }

    public void reloadPlugins() {
        File dir = new File(pluginPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            log.info("创建插件目录: {}, 结果: {}", pluginPath, created);
        }

        File[] jarFiles = dir.listFiles((d, name) -> name.endsWith(".jar"));
        if (jarFiles == null) return;

        for (File jar : jarFiles) {
            // 如果已经加载过该文件，可以跳过（简单去重逻辑）
            if (pluginCache.containsKey(jar.getName())) continue;

            try {
                loadPlugin(jar);
            } catch (Exception e) {
                log.error("插件加载失败: " + jar.getName(), e);
            }
        }
    }

    public void applyPlugin(String jarName) throws Exception {
        if (pluginCache.containsKey(jarName)) {
            log.info("插件 {} 已加载，跳过重复应用", jarName);
            return;
        }
        File jar = new File(pluginPath, jarName);
        if (!jar.exists()) {
            throw new IllegalArgumentException("插件文件不存在: " + jar.getAbsolutePath());
        }
        loadPlugin(jar);
    }

    public void unloadPlugin(String jarName) {
        List<ModelProviderHandler> handlers = pluginHandlers.remove(jarName);
        if (handlers != null) {
            handlers.forEach(handler -> astroModelFactory.unregisterHandler(handler.getProvider()));
        }
        PluginClassLoader loader = pluginCache.remove(jarName);
        if (loader != null) {
            try {
                loader.close();
            } catch (Exception e) {
                log.warn("关闭插件类加载器失败: {}", jarName, e);
            }
        }
    }

    private void loadPlugin(File jar) throws Exception {
        URL[] urls = { jar.toURI().toURL() };

        // 使用自定义的 PluginClassLoader
        // 传入当前线程的 ContextClassLoader 作为父加载器
        PluginClassLoader classLoader = new PluginClassLoader(urls, Thread.currentThread().getContextClassLoader());

        // 使用 SPI 发现实现类
        ServiceLoader<ModelProviderHandler> serviceLoader =
                ServiceLoader.load(ModelProviderHandler.class, classLoader);

        boolean found = false;
        List<ModelProviderHandler> loadedHandlers = new ArrayList<>();
        for (ModelProviderHandler handler : serviceLoader) {
            log.info("🚀 成功从外部加载插件: [{}] 厂商: {}, 版本: {}",
                    jar.getName(), handler.getProvider().getCode(), handler.getVersion());

            // 注册到工厂
            astroModelFactory.registerHandler(handler);
            loadedHandlers.add(handler);
            found = true;
        }

        if (found) {
            pluginCache.put(jar.getName(), classLoader);
            pluginHandlers.put(jar.getName(), loadedHandlers);
        } else {
            log.warn("文件 {} 中未发现有效的 ModelProviderHandler SPI 配置", jar.getName());
            classLoader.close(); // 没找到就关闭加载器释放资源
        }
    }
}