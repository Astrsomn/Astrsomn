package com.astrsomn.starter.runtime.langchain.vector;

import com.astrsomn.api.vector.constant.AiVecSourceEnum;
import com.astrsomn.api.vector.entity.AiVecSourceEntity;
import com.astrsomn.api.runtime.common.langchain.extension.vector.VecDriver;
import com.astrsomn.api.runtime.common.langchain.extension.vector.VecSource;
import com.astrsomn.api.vector.exception.AstVecSourceErrorEnum;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Component
public class AstroVecSourceFactory {

    private static final String LOG_PREFIX = "[Astrsomn] [向量工厂] ====> ";

    /**
     * 变量名	                级别  	核心职责    	存储内容
     * classpathDrivers	        静态底座	提供基础能力	原生驱动类
     * pluginDriverOverrides	动态扩展	插件能力覆盖	插件驱动类
     * pluginDriverOwningJar	管理辅助	插件卸载追踪	驱动 -> 插件包名
     * activeSources	        运行性能	避免重复连接	连接句柄对象
     * activeSourceFingerprints	状态一致性	感知配置变更	配置信息的字符串
     */
    private final Map<String, VecDriver> classpathDrivers;
    private final ConcurrentHashMap<String, VecDriver> pluginDriverOverrides = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> pluginDriverOwningJar = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, VecSource> activeSources = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, String> activeSourceFingerprints = new ConcurrentHashMap<>();

    public AstroVecSourceFactory() {
        this.classpathDrivers = loadClasspathDrivers();
    }

    /**
     * SPI 加载本地 Classpath 驱动
     */
    private static Map<String, VecDriver> loadClasspathDrivers() {
        ServiceLoader<VecDriver> loader = ServiceLoader.load(VecDriver.class);

        return StreamSupport.stream(loader.spliterator(), false)
                .filter(driver -> {
                    String key = StringUtils.trimToNull(driver.getExtensionKey());
                    if (Objects.isNull(key)) {
                        log.warn("{} 跳过无效 VecDriver (Key为空): {}", LOG_PREFIX, driver.getClass().getName());
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toMap(
                        VecDriver::getExtensionKey,
                        driver -> {
                            log.info("{} 加载 Classpath 驱动: {} ({})", LOG_PREFIX, driver.getExtensionKey(), driver.getClass().getSimpleName());
                            return driver;
                        },
                        (existing, replacement) -> {
                            log.warn("{} 发现重复的 Classpath 驱动: {}", LOG_PREFIX, existing.getExtensionKey());
                            return existing;
                        }
                ));
    }

    /**
     * 注册插件驱动
     */
    public void applyPluginDrivers(String jarName, List<VecDriver> drivers) {
        if (StringUtils.isBlank(jarName) || Objects.isNull(drivers)) return;

        drivers.stream()
                .filter(d -> Objects.nonNull(StringUtils.trimToNull(d.getExtensionKey())))
                .forEach(driver -> {
                    String key = driver.getExtensionKey();
                    pluginDriverOverrides.put(key, driver);
                    pluginDriverOwningJar.put(key, jarName);
                    log.info("{} 注册插件驱动 | Key: {} | Jar: {}", LOG_PREFIX, key, jarName);
                });
    }

    /**
     * 移除指定 Jar 关联的插件驱动
     */
    public void removePluginDrivers(String jarName) {
        if (StringUtils.isBlank(jarName)) return;

        pluginDriverOwningJar.entrySet().removeIf(entry -> {
            if (Objects.equals(jarName, entry.getValue())) {
                String key = entry.getKey();
                pluginDriverOverrides.remove(key);
                log.info("{} 移除插件驱动 | Key: {} | Jar: {}", LOG_PREFIX, key, jarName);
                return true;
            }
            return false;
        });
    }

    /**
     * 解析驱动：优先插件，后本地
     */
    public Optional<VecDriver> resolveDriver(String extensionCode) {
        return Optional.ofNullable(StringUtils.trimToNull(extensionCode))
                .map(p -> Optional.ofNullable(pluginDriverOverrides.get(p))
                        .orElseGet(() -> classpathDrivers.get(p)));
    }

    /**
     * 绑定数据源句柄
     */
    public VecSource bindSource(AiVecSourceEntity entity) {
        return Optional.ofNullable(entity)
                .map(e -> StringUtils.trimToNull(e.getExtensionCode()))
                .flatMap(this::resolveDriver)
                .map(driver -> {
                    String provider = entity.getExtensionCode();
                    String jar = pluginDriverOwningJar.getOrDefault(provider, "classpath");
                    log.info("{} 绑定句柄 | 驱动: {} | 来源: {}", LOG_PREFIX, driver.getClass().getSimpleName(), jar);
                    return driver.bindSource(entity);
                })
                .orElseThrow(() -> new BusinessException(AstVecSourceErrorEnum.VEC_DRIVER_NOT_FOUND,
                        "未找到对应的向量驱动: " + (entity != null ? entity.getExtensionCode() : "null")));
    }

    /**
     * 维护运行时连接缓存
     */
    public synchronized void registerOrRefresh(AiVecSourceEntity entity) {
        if (Objects.isNull(entity) || Objects.isNull(entity.getId())) return;

        Long id = entity.getId();
        if (!entity.getStatus().equals(AiVecSourceEnum.StatusEnum.ENABLED.getCode())) {
            removeActiveSource(id);
            return;
        }

        String fingerprint = generateFingerprint(entity);
        VecSource existing = activeSources.get(id);

        // 指纹未变且实例存在则跳过
        if (Objects.nonNull(existing) && Objects.equals(fingerprint, activeSourceFingerprints.get(id))) {
            return;
        }

        shutdownQuietly(existing);
        VecSource next = bindSource(entity);
        activeSources.put(id, next);
        activeSourceFingerprints.put(id, fingerprint);
        log.info("{} 缓存已刷新 | ID: {} | Provider: {}", LOG_PREFIX, id, entity.getExtensionCode());
    }

    /**
     * 移除缓存并释放资源
     */
    public synchronized void removeActiveSource(Long sourceId) {
        Optional.ofNullable(sourceId)
                .map(activeSources::remove)
                .ifPresent(removed -> {
                    activeSourceFingerprints.remove(sourceId);
                    shutdownQuietly(removed);
                    log.info("{} 缓存已移除 | ID: {}", LOG_PREFIX, sourceId);
                });
    }

    /**
     * 获取活跃句柄
     */
    public Optional<VecSource> tryGetActiveSource(Long sourceId) {
        return Optional.ofNullable(sourceId).map(activeSources::get);
    }

    /**
     * 测试连接（不影响长连接缓存）
     */
    public boolean testConnection(AiVecSourceEntity entity) {
        if (Objects.isNull(entity)) return false;

        long start = System.currentTimeMillis();
        VecSource tempSource = null;
        try {
            tempSource = bindSource(entity);
            boolean healthy = tempSource.testConnection();
            log.info("{} 连接测试{} | 耗时: {}ms | Provider: {}",
                    LOG_PREFIX, healthy ? "成功" : "失败", (System.currentTimeMillis() - start), entity.getExtensionCode());
            return healthy;
        } catch (Exception e) {
            log.error("{} 连接测试异常 | Provider: {} | 错误: {}", LOG_PREFIX, entity.getExtensionCode(), e.getMessage());
            return false;
        } finally {
            shutdownQuietly(tempSource);
        }
    }

    private void shutdownQuietly(VecSource source) {
        Optional.ofNullable(source).ifPresent(s -> {
            try {
                s.shutdown();
            } catch (Exception e) {
                log.warn("{} 句柄关闭异常: {}", LOG_PREFIX, e.getMessage());
            }
        });
    }

    private String generateFingerprint(AiVecSourceEntity e) {
        return StreamOf(e.getExtensionCode(), e.getHost(), e.getPort(), e.getUsername(),
                e.getPassword(), e.getDatabaseName(), e.getToken(), e.getConfigJson())
                .map(StringUtils::normalize)
                .collect(Collectors.joining("\u0001"));
    }


    /**
     * 辅助快速构建流
     */
    private java.util.stream.Stream<String> StreamOf(String... args) {
        return Arrays.stream(args);
    }
}