package com.astrsomn.starter.runtime.plugin;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.astrsomn.api.runtime.common.langchain.extension.model.ModelProviderHandler;
import com.astrsomn.api.runtime.common.langchain.extension.vector.VecDriver;
import com.astrsomn.starter.runtime.langchain.factory.AstroModelFactory;
import com.astrsomn.starter.runtime.langchain.vector.AstroVecSourceFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
@Component
@RequiredArgsConstructor
public class AstrsomnPluginManager {

    private final AstroModelFactory astroModelFactory;
    private final AstroVecSourceFactory astroVecSourceFactory;
    private final String pluginPath = "./plugins";

    // 缓存已加载的插件及其类加载器，用于卸载与热更新
    private final Map<String, PluginClassLoader> pluginCache = new ConcurrentHashMap<>();
    private final Map<String, List<ModelProviderHandler>> pluginHandlers = new ConcurrentHashMap<>();
    private final Map<String, List<VecDriver>> pluginVecDrivers = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        reloadPlugins();
    }

    public String getPluginPath() {
        return pluginPath;
    }

    public File getPluginsDirectory() {
        File dir = new File(pluginPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            log.info("创建插件目录: {}, 结果: {}", pluginPath, created);
        }
        return dir;
    }

    public void reloadPlugins() {
        File dir = getPluginsDirectory();

        Optional.ofNullable(dir.listFiles((d, name) -> name.endsWith(".jar")))
                .ifPresent(jarFiles -> Arrays.stream(jarFiles)
                        .filter(jar -> !pluginCache.containsKey(jar.getName()))
                        .forEach(jar -> {
                            try {
                                loadPlugin(jar);
                            } catch (Exception e) {
                                log.error("插件加载失败: {}", jar.getName(), e);
                            }
                        }));
    }

    public void applyPlugin(String jarName) throws Exception {
        if (pluginCache.containsKey(jarName)) {
            log.info("插件 {} 已加载，跳过", jarName);
            return;
        }

        File jar = new File(pluginPath, jarName);
        if (!jar.exists()) {
            throw new IllegalArgumentException("插件文件不存在: " + jar.getAbsolutePath());
        }
        loadPlugin(jar);
    }

    public void unloadPlugin(String jarName) {
        // 从模型工厂注销处理器
        Optional.ofNullable(pluginHandlers.remove(jarName))
                .ifPresent(handlers -> handlers.forEach(h -> astroModelFactory.unregisterHandler(h.getProvider())));

        pluginVecDrivers.remove(jarName);
        astroVecSourceFactory.removePluginDrivers(jarName);

        // 关闭并移除类加载器释放资源
        Optional.ofNullable(pluginCache.remove(jarName))
                .ifPresent(loader -> {
                    try {
                        loader.close();
                    } catch (Exception e) {
                        log.warn("关闭插件类加载器失败: {}", jarName, e);
                    }
                });
    }

    /**
     * 自该 jar 通过 SPI 加载的 {@link VecDriver} 实例（卸载前可用于解析元数据）。
     */
    public List<VecDriver> getVecDriversForJar(String jarName) {
        if (jarName == null) {
            return List.of();
        }
        return pluginVecDrivers.getOrDefault(jarName, List.of());
    }

    private void loadPlugin(File jar) throws Exception {
        URL[] urls = { jar.toURI().toURL() };
        // 传入 ContextClassLoader 作为父加载器以保证类可见性
        PluginClassLoader classLoader = new PluginClassLoader(urls, Thread.currentThread().getContextClassLoader());

        // 通过 SPI 发现并实例化插件实现
        ServiceLoader<ModelProviderHandler> serviceLoader = ServiceLoader.load(ModelProviderHandler.class, classLoader);

        List<ModelProviderHandler> loadedHandlers = new ArrayList<>();
        for (ModelProviderHandler handler : serviceLoader) {
            log.info("🚀 成功加载插件: [{}] 厂商: {}, 版本: {}",
                    jar.getName(), handler.getProvider().getCode(), handler.getVersion());

            astroModelFactory.registerHandler(handler);
            loadedHandlers.add(handler);
        }

        List<VecDriver> loadedVecDrivers = new ArrayList<>();
        ServiceLoader<VecDriver> vecLoader = ServiceLoader.load(VecDriver.class, classLoader);
        for (VecDriver vecDriver : vecLoader) {
            log.info("成功加载向量驱动 SPI: [{}] extensionKey={}", jar.getName(), vecDriver.getExtensionKey());
            loadedVecDrivers.add(vecDriver);
        }

        if (!loadedHandlers.isEmpty() || !loadedVecDrivers.isEmpty()) {
            pluginCache.put(jar.getName(), classLoader);
            pluginHandlers.put(jar.getName(), loadedHandlers);
            pluginVecDrivers.put(jar.getName(), loadedVecDrivers);
            astroVecSourceFactory.applyPluginDrivers(jar.getName(), loadedVecDrivers);
        } else {
            log.warn("文件 {} 未发现 ModelProviderHandler 或 VecDriver 的 SPI 配置", jar.getName());
            classLoader.close();
        }
    }
}