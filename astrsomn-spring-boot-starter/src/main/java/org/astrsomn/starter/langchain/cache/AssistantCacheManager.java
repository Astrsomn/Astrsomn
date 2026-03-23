package org.astrsomn.starter.langchain.cache;

import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.langchain.buildParam.AstroChatRequest;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.stereotype.Component;


import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Component
public class AssistantCacheManager {

    // 最大缓存实例数，防止内存溢出
    private static final int MAX_ENTRIES = 500;

    /**
     * 使用 Collections.synchronizedMap 包装一个开启了 AccessOrder 的 LinkedHashMap
     * accessOrder = true 确保了它是真正的 LRU（最近访问的排在后面，最久未访问的在前面）
     */
    private final Map<String, Object> assistantCache = Collections.synchronizedMap(
            new LinkedHashMap<String, Object>(MAX_ENTRIES, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
                    // 当缓存数量超过设定值时，自动移除最老（最久未访问）的实例
                    return size() > MAX_ENTRIES;
                }
            }
    );

    /**
     * 获取或创建 Assistant 实例
     */
    @SuppressWarnings("unchecked")
    public <T> T getOrCreate(AstroChatRequest<T> param, Supplier<T> creator) {
        // 1. 生成配置指纹
        String configHash = generateConfigHash(param);

        // 2. 复合 Key: memoryKey + configHash
        String cacheKey = param.getMemoryKey() + ":" + configHash;

        // 3. 这里的 get 方法会触发 LinkedHashMap 的 accessOrder 排序更新
        T instance = (T) assistantCache.get(cacheKey);

        if (instance == null) {
            synchronized (assistantCache) {
                // 二次检查，防止并发时重复创建
                instance = (T) assistantCache.get(cacheKey);
                if (instance == null) {
                    log.info("未命中缓存或配置已变更，正在为会话 [{}] 实例化 Assistant...", param.getMemoryKey());
                    instance = creator.get();
                    assistantCache.put(cacheKey, instance);
                }
            }
        }
        return instance;
    }

    /**
     * 生成配置摘要 (纯 JDK 实现，不依赖外部 JSON 库可用 StringBuilder 拼接)
     */
    private String generateConfigHash(AstroChatRequest<?> param) {
        StringBuilder sb = new StringBuilder();
        sb.append(param.getAgentKey());

        // 建议只放入影响 "Builder" 构建的关键参数
        if (param.getChatSetting() != null) {
            sb.append(param.getChatSetting().isEnableStream());
        }
        if (param.getToolSetting() != null) {
            // 假设 toolKeys 是 List<String>
            sb.append(param.getToolSetting().getToolKeys());
            sb.append(param.getToolSetting().getMcpKeys());
        }

        // 使用简单的字符串 Hash 或简单的 MD5（JDK 自带 MessageDigest）
        return String.valueOf(sb.toString().hashCode());
    }

    /**
     * 手动清理（可选）
     */
    public void invalidate(String memoryKey) {
        synchronized (assistantCache) {
            assistantCache.keySet().removeIf(key -> key.startsWith(memoryKey + ":"));
        }
    }
}