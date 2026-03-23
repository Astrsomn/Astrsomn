package org.astrsomn.starter.langchain.tool;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.service.tool.*;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.constant.AiToolEnum;
import org.astrsomn.core.common.entity.AiToolEntity;
import org.astrsomn.core.mapper.AiToolMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DynamicToolProvider implements ToolProvider {

    private final AiToolMapper aiToolMapper;
    private final ApplicationContext applicationContext;

    // 缓存：避免每次对话都进行反射查找，Key 可以是 "beanName:methodName"
    private final Map<String, Method> methodCache = new ConcurrentHashMap<>();

    private List<String> toolIds = new ArrayList<>();

    public DynamicToolProvider(AiToolMapper aiToolMapper, ApplicationContext applicationContext) {
        this.aiToolMapper = aiToolMapper;
        this.applicationContext = applicationContext;
    }

    public void initialize(List<String> toolKeys) {
        this.toolIds = toolKeys;
    }

    @Override
    public ToolProviderResult provideTools(ToolProviderRequest request) {
        if (toolIds.isEmpty()) {
            return ToolProviderResult.builder().build();
        }

        List<AiToolEntity> toolConfigs = aiToolMapper.selectList(new LambdaQueryWrapper<AiToolEntity>()
                .in(AiToolEntity::getId, toolIds)
                .eq(AiToolEntity::getEnableFlag, AiToolEnum.EnableFlagEnum.ENABLED.getCode()));

        ToolProviderResult.Builder builder = ToolProviderResult.builder();

        for (AiToolEntity config : toolConfigs) {
            try {
                Object bean = applicationContext.getBean(config.getBeanName());
                // 1. 获取并缓存 Method 对象
                Method method = getOrCacheMethod(bean, config.getMethodName());

                if (method == null) {
                    log.warn("未找到工具方法: {}.{}", config.getBeanName(), config.getMethodName());
                    continue;
                }

                // 2. 核心改进：利用框架自动生成包含参数 Schema 的 Specification
                // 如果你的方法上有 @Tool 注解，它会自动读取描述；如果没有，会基于方法名生成
                ToolSpecification spec = ToolSpecifications.toolSpecificationFrom(method);

                // 如果数据库中有自定义描述，可以覆盖它
                if (config.getDescription() != null) {
                    spec = spec.toBuilder().description(config.getDescription()).build();
                }

                // 3. 核心改进：使用 DefaultToolExecutor 自动处理动态参数映射
                // 它能自动将 Map<String, Object> 转换为方法所需的具体参数类型
                ToolExecutor executor = new DefaultToolExecutor(bean, method);

                builder.add(spec, executor);

            } catch (Exception e) {
                log.error("加载工具 [{}] 失败", config.getToolName(), e);
            }
        }
        return builder.build();
    }

    /**
     * 根据 Bean 和方法名获取方法对象，支持缓存
     */
    private Method getOrCacheMethod(Object bean, String methodName) {
        String cacheKey = bean.getClass().getName() + ":" + methodName;
        return methodCache.computeIfAbsent(cacheKey, k -> {
            // 查找该 Bean 中所有同名方法（这里简化处理，取第一个找到的）
            // 如果有重载方法，建议在数据库中明确参数类型
            return Arrays.stream(bean.getClass().getMethods())
                    .filter(m -> m.getName().equals(methodName))
                    .findFirst()
                    .orElse(null);
        });
    }
}
