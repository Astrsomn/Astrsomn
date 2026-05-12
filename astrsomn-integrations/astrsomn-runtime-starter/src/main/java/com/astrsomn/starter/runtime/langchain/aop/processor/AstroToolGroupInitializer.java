package com.astrsomn.starter.runtime.langchain.aop.processor;

import com.astrsomn.starter.runtime.mapper.AstAiToolMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import com.astrsomn.api.runtime.common.constant.AiModelEnum;
import com.astrsomn.api.runtime.common.entity.AiToolEntity;
import com.astrsomn.starter.runtime.langchain.aop.annotation.AstroToolGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class AstroToolGroupInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final String LOG_PREFIX = "[Astrsomn] [工具组扫描器] ====> ";
    private static final String ENABLED = AiModelEnum.StatusEnum.ENABLED.getCode();

    private AstAiToolMapper astAiToolMapper;
    
    @Autowired(required = false)
    public void setAiToolMapper(AstAiToolMapper astAiToolMapper) {
        this.astAiToolMapper = astAiToolMapper;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (astAiToolMapper == null) {
            log.warn("{} AiToolMapper 未注入，跳过工具组扫描", LOG_PREFIX);
            return;
        }
        delayedScanAndRegisterTools(event.getApplicationContext());
    }

    /**
     * 延迟扫描并注册工具组，等待数据库表创建完成
     */
    private void delayedScanAndRegisterTools(ConfigurableApplicationContext context) {
        try {
            doScanAndRegisterTools(context);
        } catch (Exception e) {
            log.warn("{} 工具组扫描失败，将在延迟后重试 | 异常: {}", LOG_PREFIX, e.getMessage());
            try {
                TimeUnit.SECONDS.sleep(2);
                doScanAndRegisterTools(context);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.error("{} 工具组扫描重试被中断", LOG_PREFIX);
            } catch (Exception e2) {
                log.error("{} 工具组扫描重试失败 | 异常: {}", LOG_PREFIX, e2.getMessage());
            }
        }
    }

    /**
     * 执行工具组扫描和注册
     */
    private void doScanAndRegisterTools(ConfigurableApplicationContext context) {
        Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(AstroToolGroup.class);
        
        beansWithAnnotation.forEach((beanName, bean) -> {
            Class<?> beanClass = ClassUtils.getUserClass(bean);
            AstroToolGroup groupAnno = beanClass.getAnnotation(AstroToolGroup.class);
            scanAndRegisterTools(bean, beanName, groupAnno);
        });
    }

    /**
     * 扫描并注册工具组内的所有工具方法
     */
    private void scanAndRegisterTools(Object bean, String beanName, AstroToolGroup groupAnno) {
        Class<?> beanClass = ClassUtils.getUserClass(bean);

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

        Optional<AiToolEntity> existingOpt = Optional.ofNullable(astAiToolMapper.selectOne(
                new LambdaQueryWrapper<AiToolEntity>().eq(AiToolEntity::getToolKey, toolKey)));

        AiToolEntity entity = existingOpt.orElseGet(AiToolEntity::new);

        // 填充/更新元数据
        fillToolEntity(entity, bean, beanName, groupAnno, method, toolAnno, toolKey);

        boolean success = existingOpt.isPresent()
                ? astAiToolMapper.updateById(entity) > 0
                : astAiToolMapper.insert(entity) > 0;

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