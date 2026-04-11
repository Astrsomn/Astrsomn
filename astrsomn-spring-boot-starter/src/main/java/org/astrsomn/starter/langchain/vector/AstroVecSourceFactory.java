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
import java.util.Objects;
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

    /** 已启用向量源 id → 运行时连接句柄（单源单客户端复用） */
    private final ConcurrentHashMap<Long, VecSource> activeSources = new ConcurrentHashMap<>();

    /** 与 {@link #activeSources} 对应的连接配置指纹，用于判断是否需要重建句柄 */
    private final ConcurrentHashMap<Long, String> activeSourceFingerprints = new ConcurrentHashMap<>();

    private static final String STATUS_ENABLED = "ENABLED";

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
        String pluginJar = pluginDriverOwningJar.get(provider);
        log.info(
                "[Astro] bindSource: driverClass={}, fromPluginJar={}",
                driver.getClass().getName(),
                pluginJar != null ? pluginJar : "(classpath)");
        log.info("[Astro] bindSource: invoking driver.bindSource(entity) ...");
        VecSource bound = driver.bindSource(entity);
        log.info("[Astro] bindSource: got VecSource class={}", bound.getClass().getName());
        return bound;
    }

    /**
     * 获取已注册且仍为启用状态的向量源句柄（未调用过 {@link #registerOrRefresh} 则为空）。
     */
    public Optional<VecSource> tryGetActiveSource(Long sourceId) {
        if (sourceId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(activeSources.get(sourceId));
    }

    /**
     * 按持久化实体维护运行时连接：启用则绑定并缓存；禁用则关闭并移除。
     * 配置变更时先 shutdown 再重建。
     */
    public synchronized void registerOrRefresh(AiVecSourceEntity entity) {
        if (entity == null || entity.getId() == null) {
            return;
        }
        Long id = entity.getId();
        if (!STATUS_ENABLED.equals(StringUtils.defaultIfBlank(StringUtils.trimToNull(entity.getStatus()), ""))) {
            removeActiveSource(id);
            return;
        }
        String fp = connectionFingerprint(entity);
        String prevFp = activeSourceFingerprints.get(id);
        VecSource current = activeSources.get(id);
        if (current != null && Objects.equals(fp, prevFp)) {
            return;
        }
        shutdownQuietly(current);
        VecSource next = bindSource(entity);
        activeSources.put(id, next);
        activeSourceFingerprints.put(id, fp);
        log.info("[Astro] VecSource registered: id={} provider={}", id, entity.getProvider());
    }

    /**
     * 移除并释放指定向量源的运行时句柄（删除数据源或禁用时调用）。
     */
    public synchronized void removeActiveSource(Long sourceId) {
        if (sourceId == null) {
            return;
        }
        VecSource removed = activeSources.remove(sourceId);
        activeSourceFingerprints.remove(sourceId);
        shutdownQuietly(removed);
        if (removed != null) {
            log.info("[Astro] VecSource removed from cache: id={}", sourceId);
        }
    }

    private static void shutdownQuietly(VecSource source) {
        if (source == null) {
            return;
        }
        try {
            source.shutdown();
        } catch (Exception e) {
            log.warn("[Astro] VecSource shutdown failed: {}", e.getMessage());
        }
    }

    private static String connectionFingerprint(AiVecSourceEntity e) {
        return String.join(
                "\u0001",
                nz(e.getProvider()),
                nz(e.getHost()),
                nz(e.getPort()),
                nz(e.getUsername()),
                nz(e.getPassword()),
                nz(e.getDatabaseName()),
                nz(e.getToken()),
                nz(e.getConfigJson()));
    }

    private static String nz(String s) {
        return StringUtils.defaultIfBlank(StringUtils.trimToNull(s), "");
    }

    /**
     * 测试向量源连接
     */
    public boolean testConnection(AiVecSourceEntity entity) {
        if (entity == null) {
            log.warn("[Astro] testConnection: entity is null");
            return false;
        }
        long t0 = System.nanoTime();
        String provider = entity.getProvider();
        String host = entity.getHost();
        String port = entity.getPort();
        log.info(
                "[Astro] testConnection begin: provider={}, host={}, port={}, configJsonBlank={}, tokenConfigured={}",
                provider,
                host,
                port,
                StringUtils.isBlank(entity.getConfigJson()),
                StringUtils.isNotBlank(entity.getToken()));
        VecSource source = null;
        try {
            source = bindSource(entity);
            log.info("[Astro] testConnection: bindSource done, calling VecSource.testConnection() ...");
            boolean success = source.testConnection();
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000L;
            if (success) {
                log.info(
                        "[Astro] testConnection ok in {}ms: provider={}, host={}, port={}",
                        elapsedMs,
                        provider,
                        host,
                        port);
            } else {
                log.warn(
                        "[Astro] testConnection returned false in {}ms: provider={}, host={}, port={}",
                        elapsedMs,
                        provider,
                        host,
                        port);
            }
            return success;
        } catch (RuntimeException e) {
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000L;
            log.error(
                    "[Astro] testConnection failed in {}ms: provider={}, host={}, port={}",
                    elapsedMs,
                    provider,
                    host,
                    port,
                    e);
            throw e;
        } catch (Exception e) {
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000L;
            log.error(
                    "[Astro] testConnection failed in {}ms: provider={}, host={}, port={}",
                    elapsedMs,
                    provider,
                    host,
                    port,
                    e);
            throw new RuntimeException(e);
        } finally {
            shutdownQuietly(source);
        }
    }
}
