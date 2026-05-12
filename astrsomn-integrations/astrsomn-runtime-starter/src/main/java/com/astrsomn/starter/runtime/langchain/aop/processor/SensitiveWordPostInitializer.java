package com.astrsomn.starter.runtime.langchain.aop.processor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import com.astrsomn.api.runtime.common.entity.AiSensitiveWordEntity;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.mapper.AstAiSensitiveWordMapper;
import com.astrsomn.starter.runtime.langchain.quota.SensitiveWordProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SensitiveWordPostInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final String LOG_PREFIX = "[Astrsomn] [词库初始化器] ====> ";
    private static final String STATUS_ENABLED = "ENABLED";

    private AstAiSensitiveWordMapper aiSensitiveWordMapper;
    private SensitiveWordProvider sensitiveWordProvider;
    
    @Autowired(required = false)
    public void setAiSensitiveWordMapper(AstAiSensitiveWordMapper aiSensitiveWordMapper) {
        this.aiSensitiveWordMapper = aiSensitiveWordMapper;
    }
    
    @Autowired(required = false)
    public void setSensitiveWordProvider(SensitiveWordProvider sensitiveWordProvider) {
        this.sensitiveWordProvider = sensitiveWordProvider;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (aiSensitiveWordMapper == null || sensitiveWordProvider == null) {
            log.warn("{} 必要依赖未注入，跳过词库初始化", LOG_PREFIX);
            return;
        }
        delayedLoadDatabaseWords();
    }

    /**
     * 延迟加载词库，等待数据库表创建完成
     */
    private void delayedLoadDatabaseWords() {
        try {
            loadDatabaseWords();
        } catch (Exception e) {
            log.warn("{} 词库初始化失败，将在延迟后重试 | 异常: {}", LOG_PREFIX, e.getMessage());
            try {
                TimeUnit.SECONDS.sleep(2);
                loadDatabaseWords();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.error("{} 词库初始化重试被中断", LOG_PREFIX);
            } catch (Exception e2) {
                log.error("{} 词库初始化重试失败 | 异常: {}", LOG_PREFIX, e2.getMessage());
            }
        }
    }

    /**
     * 从数据库抓取并刷新词库
     */
    private void loadDatabaseWords() {
        log.info("{} 开始注入词库...", LOG_PREFIX);

        try {
            List<String> words = aiSensitiveWordMapper.selectList(new LambdaQueryWrapper<AiSensitiveWordEntity>()
                            .eq(AiSensitiveWordEntity::getDeleted, false)
                            .eq(AiSensitiveWordEntity::getStatus, STATUS_ENABLED))
                    .stream()
                    .map(AiSensitiveWordEntity::getWord)
                    .filter(StringUtils::isNotBlank)
                    .toList();

            sensitiveWordProvider.refreshWords(words);

            log.info("{} 词库注入成功 | 数量: {} 条", LOG_PREFIX, words.size());
        } catch (Exception e) {
            log.error("{} 词库初始化失败 | 请检查数据库连接 | 异常: {}", LOG_PREFIX, e.getMessage(), e);
        }
    }
}