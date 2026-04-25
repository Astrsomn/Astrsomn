package com.astrsomn.starter.langchain.aop.processor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.astrsomn.core.common.constant.AiModelEnum;
import com.astrsomn.core.common.entity.AiToolEntity;
import com.astrsomn.starter.mapper.AiToolMapper;
import com.astrsomn.starter.langchain.aop.annotation.AstroToolGroup;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AstroToolGroupInitializer implements BeanPostProcessor {

    private static final String LOG_PREFIX = "[Astrsomn] [工具组扫描器] ====> ";
    private static final String ENABLED = AiModelEnum.StatusEnum.ENABLED.getCode();

    private final AiToolMapper aiToolMapper;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Class<?> beanClass = bean.getClass();

        // 识别带有 @AstroToolGroup 注解的 Bean
        if (beanClass.isAnnotationPresent(AstroToolGroup.class)) {
            AstroToolGroup groupAnno = beanClass.getAnnotation(AstroToolGroup.class);
            scanAndRegisterTools(bean, beanName, groupAnno);
        }

        return bean;
    }

    /**
     * 扫描并注册工具组内的所有工具方法
     */
    private void scanAndRegisterTools(Object bean, String beanName, AstroToolGroup groupAnno) {
        Class<?> beanClass = bean.getClass();

        var toolMethods = Arrays.stream(beanClass.getMethods())
                .filter(m -> m.isAnnotationPresent(Tool.class))
                .toList();

        if (toolMethods.isEmpty()) {
            log.warn("{} 类 {} 未找到带 @Tool 注解的方法", LOG_PREFIX, beanClass.getName());
            return;
        }

        log.info("{} 发现工具组: {} | 包含方法数: {}", LOG_PREFIX, beanClass.getSimpleName(), toolMethods.size());

        toolMethods.forEach(method -> {
            try {
                upsertTool(bean, beanName, groupAnno, method);
            } catch (Exception e) {
                log.error("{} 注册工具方法异常 | 方法: {} | 错误: {}", LOG_PREFIX, method.getName(), e.getMessage());
            }
        });
    }

    /**
     * 执行工具信息的更新或插入（Upsert）
     */
    private void upsertTool(Object bean, String beanName, AstroToolGroup groupAnno, Method method) {
        Tool toolAnno = method.getAnnotation(Tool.class);
        String toolKey = generateToolKey(groupAnno, beanName, method);

        Optional<AiToolEntity> existingOpt = Optional.ofNullable(aiToolMapper.selectOne(
                new LambdaQueryWrapper<AiToolEntity>().eq(AiToolEntity::getToolKey, toolKey)));

        AiToolEntity entity = existingOpt.orElseGet(AiToolEntity::new);

        // 填充/更新元数据
        fillToolEntity(entity, bean, beanName, groupAnno, method, toolAnno, toolKey);

        boolean success = existingOpt.isPresent()
                ? aiToolMapper.updateById(entity) > 0
                : aiToolMapper.insert(entity) > 0;

        if (success) {
            log.info("{} 工具{}成功 | Key: {}", LOG_PREFIX, existingOpt.isPresent() ? "更新" : "创建", toolKey);
        }
    }

    /**
     * 填充工具实体数据
     */
    private void fillToolEntity(AiToolEntity entity, Object bean, String beanName,
                                AstroToolGroup group, Method method, Tool tool, String toolKey) {
        // 优先取 Tool 注解名，否则取方法名
        String toolName = StringUtils.hasText(tool.name()) ? tool.name() : method.getName();

        // 优先取 Tool 注解描述，否则取 Group 描述
        String description = (tool.value().length > 0 && StringUtils.hasText(tool.value()[0]))
                ? tool.value()[0]
                : group.description();

        entity.setToolName(toolName);
        entity.setToolKey(toolKey);
        entity.setDescription(description);
        entity.setBeanName(beanName);
        entity.setMethodName(method.getName());
        entity.setClassName(bean.getClass().getName());
        entity.setType(group.type());
        entity.setStatus(ENABLED);
    }

    /**
     * 生成唯一的工具标识符 (GroupValue:MethodName)
     */
    private String generateToolKey(AstroToolGroup group, String beanName, Method method) {
        String prefix = StringUtils.hasText(group.value()) ? group.value() : beanName;
        return prefix + ":" + method.getName();
    }
}