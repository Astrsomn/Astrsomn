package com.astrsomn.starter.langchain.quota;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import com.astrsomn.core.common.dto.conversation.AiConversationUsageDTO;
import com.astrsomn.core.mapper.AiConversationMapper;
import com.astrsomn.starter.config.AstrsomnProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ModelQuotaManager {

    private final AstrsomnProperties astrsomnProperties;
    private final AiConversationMapper conversationMapper;

    private final Map<String, Long> dailyUsageCache = new ConcurrentHashMap<>();


    @PostConstruct
    public void initQuota() {
        refreshUsage();
    }

    @Scheduled(fixedRate = 60000)
    public void refreshUsage() {
        String env = astrsomnProperties.getEnvCode();
        List<AiConversationUsageDTO> stats = conversationMapper.selectTodayUsage(env);
        stats.forEach(dto -> {
            dailyUsageCache.put(dto.getModelKey(), dto.getTotal());
        });
    }

    public boolean isExceeded(String modelKey, Long limit) {
        Long used = dailyUsageCache.getOrDefault(modelKey, 0L);
        return used >= limit;
    }

    public void addUsage(String modelKey, Integer tokens) {
        dailyUsageCache.merge(modelKey, tokens.longValue(), Long::sum);
    }


}
