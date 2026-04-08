package org.astrsomn.starter.langchain.aop.processor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.entity.AiToolEntity;
import org.astrsomn.core.mapper.AiToolMapper;
import org.astrsomn.starter.langchain.aop.annotation.AstroToolGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AstroToolGroupProcessor implements BeanPostProcessor {

    @Autowired
    private AiToolMapper aiToolMapper;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Class<?> beanClass = bean.getClass();

        if (beanClass.isAnnotationPresent(AstroToolGroup.class)) {
            AstroToolGroup toolGroup = beanClass.getAnnotation(AstroToolGroup.class);
            processToolGroup(bean, beanName, toolGroup);
        }

        return bean;
    }

    private void processToolGroup(Object bean, String beanName, AstroToolGroup toolGroup) {
        Class<?> beanClass = bean.getClass();

        List<Method> toolMethods = Arrays.stream(beanClass.getMethods())
                .filter(method -> method.isAnnotationPresent(Tool.class))
                .collect(Collectors.toList());

        if (toolMethods.isEmpty()) {
            log.warn("AstroToolGroup 类 {} 没有找到任何带有 @Tool 注解的方法", beanClass.getName());
            return;
        }

        log.info("发现 AstroToolGroup 类 {} 包含 {} 个工具方法", beanClass.getName(), toolMethods.size());

        for (Method method : toolMethods) {
            try {
                Tool toolAnnotation = method.getAnnotation(Tool.class);
                processToolMethod(bean, beanName, toolGroup, method, toolAnnotation);
            } catch (Exception e) {
                log.error("处理工具方法 {} 时发生错误", method.getName(), e);
            }
        }
    }

    private void processToolMethod(Object bean, String beanName, AstroToolGroup toolGroup,
                                  Method method, Tool toolAnnotation) {
        String toolName = StringUtils.hasText(toolAnnotation.name())
                ? toolAnnotation.name()
                : method.getName();
        String toolKey = toolGroup.value().isEmpty() ? 
                beanName + ":" + method.getName() : toolGroup.value() + ":" + method.getName();
        String[] toolDescriptions = toolAnnotation.value();
        String description = (toolDescriptions.length == 0 || !StringUtils.hasText(toolDescriptions[0]))
                ? toolGroup.description()
                : toolDescriptions[0];

        // 检查工具是否已存在
        LambdaQueryWrapper<AiToolEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AiToolEntity::getToolKey, toolKey);
        AiToolEntity existingTool = aiToolMapper.selectOne(queryWrapper);

        AiToolEntity toolEntity;
        if (existingTool != null) {
            // 更新现有工具
            toolEntity = existingTool;
            log.info("更新现有工具: {}", toolKey);
        } else {
            // 创建新工具
            toolEntity = new AiToolEntity();
            log.info("创建新工具: {}", toolKey);
        }

        toolEntity.setToolName(toolName);
        toolEntity.setToolKey(toolKey);
        toolEntity.setDescription(description);
        toolEntity.setBeanName(beanName);
        toolEntity.setMethodName(method.getName());
        toolEntity.setType(toolGroup.type());
        toolEntity.setStatus(AiModelEnum.StatusEnum.ENABLED.getCode());
        toolEntity.setClassName(bean.getClass().getName());

        if (existingTool != null) {
            aiToolMapper.updateById(toolEntity);
        } else {
            aiToolMapper.insert(toolEntity);
        }
    }
}