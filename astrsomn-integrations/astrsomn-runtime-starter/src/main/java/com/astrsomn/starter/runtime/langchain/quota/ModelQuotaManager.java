package com.astrsomn.starter.runtime.langchain.quota;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.astrsomn.api.runtime.common.dto.chat.message.AiChatUsageDTO;
import com.astrsomn.starter.runtime.mapper.AiChatMessageMapper;
import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import com.astrsomn.starter.runtime.context.EnvRuntime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ModelQuotaManager {

    private final AstrsomnProperties astrsomnProperties;
    private final AiChatMessageMapper conversationMapper;

    private final Map<String, Long> dailyUsageCache = new ConcurrentHashMap<>();


    @PostConstruct
    public void initQuota() {
        refreshUsage();
    }

    @Scheduled(fixedRate = 60000)
    public void refreshUsage() {
        String env = EnvRuntime.resolveEffectiveEnvCode(astrsomnProperties);
        try {
            List<AiChatUsageDTO> stats = conversationMapper.selectTodayUsage(env);
            stats.forEach(dto -> dailyUsageCache.put(dto.getModelKey(), dto.getTotal()));
        } catch (Exception ex) {
            // Keep the server bootable even when schema/data are not initialized yet.
            log.warn("Skip quota refresh because usage table is unavailable (env={}): {}", env, ex.getMessage());
        }
    }

    public boolean isExceeded(String modelKey, Long limit) {
        Long used = dailyUsageCache.getOrDefault(modelKey, 0L);
        return used >= limit;
    }

    public void addUsage(String modelKey, Integer tokens) {
        dailyUsageCache.merge(modelKey, tokens.longValue(), Long::sum);
    }


}
