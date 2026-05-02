package com.astrsomn.starter.runtime.langchain.aop.processor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import com.astrsomn.api.runtime.common.entity.AiSensitiveWordEntity;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.mapper.AiSensitiveWordMapper;
import com.astrsomn.starter.runtime.langchain.quota.SensitiveWordProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SensitiveWordPostInitializer implements BeanPostProcessor, BeanFactoryAware {

    private static final String LOG_PREFIX = "[Astrsomn] [词库初始化器] ====> ";
    private static final String STATUS_ENABLED = "ENABLED";

    private BeanFactory beanFactory;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof SensitiveWordProvider provider) {
            loadDatabaseWords(provider);
        }
        return bean;
    }

    /**
     * 从数据库抓取并刷新词库
     */
    private void loadDatabaseWords(SensitiveWordProvider provider) {
        log.info("{} 检测到 Provider，开始注入词库...", LOG_PREFIX);

        try {
            List<String> words = beanFactory.getBean(AiSensitiveWordMapper.class).selectList(new LambdaQueryWrapper<AiSensitiveWordEntity>()
                            .eq(AiSensitiveWordEntity::getDeleted, false)
                            .eq(AiSensitiveWordEntity::getStatus, STATUS_ENABLED))
                    .stream()
                    .map(AiSensitiveWordEntity::getWord)
                    .filter(StringUtils::isNotBlank)
                    .toList();

            provider.refreshWords(words);

            log.info("{} 词库注入成功 | 数量: {} 条", LOG_PREFIX, words.size());
        } catch (Exception e) {
            log.error("{} 词库初始化失败 | 请检查数据库连接 | 异常: {}", LOG_PREFIX, e.getMessage(), e);
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}