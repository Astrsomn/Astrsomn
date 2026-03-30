package org.astrsomn.starter.plugin;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.langchain.handler.ModelProviderHandler;
import org.astrsomn.starter.langchain.factory.core.AiModelFactory;
import org.springframework.stereotype.Component;


import java.io.File;
import java.net.URL;
import java.util.ServiceLoader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class AstrsomnPluginManager {

    private final AiModelFactory aiModelFactory;
    private final String pluginPath = "./plugins";

    // 用于记录已加载的插件及其加载器，方便后续做卸载或热更新
    private final Map<String, PluginClassLoader> pluginCache = new ConcurrentHashMap<>();

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

    private void loadPlugin(File jar) throws Exception {
        URL[] urls = { jar.toURI().toURL() };

        // 使用自定义的 PluginClassLoader
        // 传入当前线程的 ContextClassLoader 作为父加载器
        PluginClassLoader classLoader = new PluginClassLoader(urls, Thread.currentThread().getContextClassLoader());

        // 使用 SPI 发现实现类
        ServiceLoader<ModelProviderHandler> serviceLoader =
                ServiceLoader.load(ModelProviderHandler.class, classLoader);

        boolean found = false;
        for (ModelProviderHandler handler : serviceLoader) {
            log.info("🚀 成功从外部加载插件: [{}] 厂商: {}, 版本: {}",
                    jar.getName(), handler.getProvider(), handler.getVersion());

            // 注册到工厂
            aiModelFactory.registerHandler(handler);
            found = true;
        }

        if (found) {
            pluginCache.put(jar.getName(), classLoader);
        } else {
            log.warn("文件 {} 中未发现有效的 ModelProviderHandler SPI 配置", jar.getName());
            classLoader.close(); // 没找到就关闭加载器释放资源
        }
    }
}