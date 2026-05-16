package com.astrsomn.starter.runtime.langchain.cache;

import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@Component
public class AssistantCacheManager {


    private static final int MAX_ENTRIES = 500;

    private final Map<String, Object> assistantCache = Collections.synchronizedMap(
            new LinkedHashMap<String, Object>(MAX_ENTRIES, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
                    return size() > MAX_ENTRIES;
                }
            }
    );


    @SuppressWarnings("unchecked")
    public <T> T getOrCreate(AstroChatParam<T> param, Supplier<T> creator) {
        String configHash = generateConfigHash(param);
        String cacheKey = param.getMemoryKey() + ":" + configHash;
        T instance = (T) assistantCache.get(cacheKey);
        if (instance == null) {
            synchronized (assistantCache) {
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

    private String generateConfigHash(AstroChatParam<?> param) {
        StringBuilder sb = new StringBuilder();
        sb.append(param.getAgentKey());
        sb.append(param.getConversationSetting().isEnableDeepThinking());
        sb.append(param.getPromptSetting().getPromptKey());
        sb.append(param.getServiceClass().getName());

        if (param.getToolSetting() != null) {
            List<String> tools = param.getToolSetting().getToolKeys();
            if (tools != null) {
                tools.stream().sorted().forEach(sb::append);
            }
            List<String> mcps = param.getToolSetting().getMcpKeys();
            if (mcps != null) {
                mcps.stream().sorted().forEach(sb::append);
            }
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashInBytes = md.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte b : hashInBytes) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(sb.toString().hashCode());
        }
    }


    public void invalidate(String memoryKey) {
        synchronized (assistantCache) {
            assistantCache.keySet().removeIf(key -> key.startsWith(memoryKey + ":"));
        }
    }
}