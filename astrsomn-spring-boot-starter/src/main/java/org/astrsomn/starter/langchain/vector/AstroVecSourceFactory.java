package org.astrsomn.starter.langchain.vector;

import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.entity.AiVecSourceEntity;
import org.astrsomn.core.common.langchain.extension.vector.VecDriver;
import org.astrsomn.core.common.langchain.extension.vector.VecSource;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.core.exception.base.BusinessException;
import org.astrsomn.core.exception.constant.AstVecSourceErrorEnum;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 按 {@link AiVecSourceEntity} 解析 {@link VecDriver} 并实例化 {@link VecSource}（一条数据源一条连接句柄，可多次调用以得到多条独立连接）。
 * <p>
 * classpath 通过 Java SPI 加载驱动；插件 jar 中的驱动在 {@link org.astrsomn.starter.plugin.AstrsomnPluginManager} 中注册后覆盖同 extensionKey 的 classpath 实现。
 */
@Slf4j
@Component
public class AstroVecSourceFactory {

    private final Map<String, VecDriver> classpathDrivers;
    private final ConcurrentHashMap<String, VecDriver> pluginDriverOverrides = new ConcurrentHashMap<>();
    /** extensionKey → 提供该实现的插件 jar 文件名 */
    private final ConcurrentHashMap<String, String> pluginDriverOwningJar = new ConcurrentHashMap<>();

    public AstroVecSourceFactory() {
        this.classpathDrivers = loadClasspathDrivers();
    }

    private static Map<String, VecDriver> loadClasspathDrivers() {
        Map<String, VecDriver> map = new HashMap<>();
        ServiceLoader<VecDriver> loader = ServiceLoader.load(VecDriver.class);
        for (VecDriver driver : loader) {
            String key = StringUtils.trimToNull(driver.getExtensionKey());
            if (key == null) {
                log.warn("[Astro] Skip VecDriver with blank extensionKey: {}", driver.getClass().getName());
                continue;
            }
            if (map.containsKey(key)) {
                log.warn("[Astro] Duplicate classpath VecDriver skipped: {}", key);
                continue;
            }
            map.put(key, driver);
            log.info("[Astro] Loaded classpath VecDriver: {} ({})", key, driver.getClass().getSimpleName());
        }
        return Collections.unmodifiableMap(map);
    }

    /**
     * 插件加载完成后注册其中的向量驱动（同 extensionKey 覆盖 classpath）。
     */
    public void applyPluginDrivers(String jarName, List<VecDriver> drivers) {
        if (StringUtils.isBlank(jarName) || drivers == null || drivers.isEmpty()) {
            return;
        }
        for (VecDriver driver : drivers) {
            String key = StringUtils.trimToNull(driver.getExtensionKey());
            if (key == null) {
                continue;
            }
            pluginDriverOverrides.put(key, driver);
            pluginDriverOwningJar.put(key, jarName);
            log.info("[Astro] Plugin VecDriver registered: key={} jar={}", key, jarName);
        }
    }

    /**
     * 插件卸载时移除由该 jar 注册的驱动覆盖。
     */
    public void removePluginDrivers(String jarName) {
        if (StringUtils.isBlank(jarName)) {
            return;
        }
        for (Iterator<Map.Entry<String, String>> it = pluginDriverOwningJar.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, String> e = it.next();
            if (jarName.equals(e.getValue())) {
                String key = e.getKey();
                it.remove();
                pluginDriverOverrides.remove(key);
                log.info("[Astro] Plugin VecDriver removed: key={} jar={}", key, jarName);
            }
        }
    }

    /**
     * 按厂商/扩展键解析驱动（与 {@link AiVecSourceEntity#getProvider()} 一致）。
     */
    public Optional<VecDriver> resolveDriver(String providerCode) {
        String p = StringUtils.trimToNull(providerCode);
        if (p == null) {
            return Optional.empty();
        }
        VecDriver fromPlugin = pluginDriverOverrides.get(p);
        if (fromPlugin != null) {
            return Optional.of(fromPlugin);
        }
        return Optional.ofNullable(classpathDrivers.get(p));
    }

    /**
     * 根据持久化实体创建一条新的数据源连接句柄；可针对多条 {@link AiVecSourceEntity} 或同一实体的多次连接分别调用。
     */
    public VecSource bindSource(AiVecSourceEntity entity) {
        if (entity == null) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_PARAM_ERROR, "AiVecSourceEntity 不能为空");
        }
        String provider = StringUtils.trimToNull(entity.getProvider());
        if (provider == null) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_PARAM_ERROR, "向量数据源 provider 不能为空");
        }
        VecDriver driver =
                resolveDriver(provider)
                        .orElseThrow(
                                () ->
                                        new BusinessException(
                                                AstVecSourceErrorEnum.VEC_DRIVER_NOT_FOUND,
                                                "未注册向量驱动: provider="
                                                        + provider
                                                        + "（请引入对应 astrsomn-vector-* 模块或加载含 VecDriver SPI 的插件）"));
        return driver.bindSource(entity);
    }
}
