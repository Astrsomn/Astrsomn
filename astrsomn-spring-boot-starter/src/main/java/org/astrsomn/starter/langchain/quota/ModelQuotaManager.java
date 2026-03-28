package org.astrsomn.starter.langchain.quota;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.mapper.AiConversationMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ModelQuotaManager {

    private final AiConversationMapper conversationMapper;
    // 本地缓存：ModelKey -> 今日消耗总量
    private final Map<String, Long> dailyUsageCache = new ConcurrentHashMap<>();

    // 初始化：每天凌晨或启动时，汇总今日 Token 消耗
    @PostConstruct
    public void initQuota() {
        refreshUsage();
    }

    @Scheduled(fixedRate = 60000)
    public void refreshUsage() {
        // SQL: SELECT MODEL_KEY, SUM(CONSUME_TOKENS) FROM AI_CONVERSATION WHERE CREATE_TIME >= TODAY GROUP BY MODEL_KEY
        List<Map<String, Object>> stats = conversationMapper.selectTodayUsage();
        stats.forEach(map -> {
            dailyUsageCache.put((String) map.get("MODEL_KEY"), ((Number) map.get("TOTAL")).longValue());
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
