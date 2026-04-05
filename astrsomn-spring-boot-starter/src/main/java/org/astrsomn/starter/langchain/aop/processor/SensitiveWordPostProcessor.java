package org.astrsomn.starter.langchain.aop.processor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.core.common.entity.AiSensitiveWordEntity;
import org.astrsomn.core.mapper.AiSensitiveWordMapper;
import org.astrsomn.starter.langchain.quota.SensitiveWordProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SensitiveWordPostProcessor implements BeanPostProcessor {

    private final AiSensitiveWordMapper aiSensitiveWordMapper;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 只针对 SensitiveWordProvider 进行处理
        if (bean instanceof SensitiveWordProvider provider) {
            log.info("====> [Astrsomn] 检测到 SensitiveWordProvider，开始自动注入词库...");

            try {
                // 1. 从数据库查询所有启用的敏感词
                List<AiSensitiveWordEntity> entities = aiSensitiveWordMapper.selectList(
                        new LambdaQueryWrapper<AiSensitiveWordEntity>()
                                .eq(AiSensitiveWordEntity::getStatus, "ENABLED")
                );

                List<String> words = entities.stream()
                        .map(AiSensitiveWordEntity::getWord)
                        .filter(StringUtils::isNotBlank)
                        .toList();

                // 2. 调用 provider 的刷新方法构建 DFA 树
                provider.refreshWords(words);

                log.info("====> [Astrsomn] 敏感词库自动注入成功，总计: {} 条", words.size());
            } catch (Exception e) {
                // 启动阶段如果数据库连接有问题，记录错误但不阻塞容器启动
                log.error("====> [Astrsomn] 敏感词库初始化失败，请检查数据库连接", e);
            }
        }
        return bean;
    }
}