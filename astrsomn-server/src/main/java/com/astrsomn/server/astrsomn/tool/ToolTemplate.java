package com.astrsomn.server.astrsomn.tool;

import com.astrsomn.api.runtime.common.entity.AiAgentEntity;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.utils.JsonUtil;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.service.ai.AiAgentService;
import com.astrsomn.starter.runtime.langchain.aop.annotation.AstroToolGroup;
import com.astrsomn.starter.runtime.langchain.template.TemplateRenderService;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@AstroToolGroup(value = "toolTemplate", description = "HTML模板渲染工具组")
@Component
@RequiredArgsConstructor
public class ToolTemplate {

    private final TemplateRenderService templateRenderService;
    private final AiAgentService aiAgentService;


    @Tool("根据模板Key和JSON参数渲染HTML模板，返回渲染后的HTML内容")
    public String renderTemplate(String templateKey, String paramsJson) {
        if (StringUtils.isBlank(templateKey)) {
            return "Error: templateKey cannot be empty";
        }

        Map<String, Object> params = parseParams(paramsJson);
        try {
            return templateRenderService.renderTemplate(templateKey, params);
        } catch (BusinessException e) {
            return "Error: " + e.getMessage();
        }
    }


    @Tool("根据Agent Key和JSON参数渲染该Agent关联的所有HTML模板，返回所有模板的渲染结果")
    public String renderAgentTemplates(String agentKey, String paramsJson) {
        if (StringUtils.isBlank(agentKey)) {
            return "Error: agentKey cannot be empty";
        }

        // Resolve agent and get templateKeys
        AiAgentEntity agent = aiAgentService.lambdaQuery()
                .eq(AiAgentEntity::getAgentKey, agentKey)
                .one();

        if (Objects.isNull(agent)) {
            return "Error: Agent not found for agentKey: " + agentKey;
        }

        String templateKeys = agent.getTemplateKeys();
        if (StringUtils.isBlank(templateKeys)) {
            return "No templates configured for agent: " + agentKey;
        }

        Map<String, Object> params = parseParams(paramsJson);
        try {
            Map<String, String> results = templateRenderService
                    .renderTemplatesByKeys(templateKeys, params);

            if (results.isEmpty()) {
                return "No enabled templates found for agent: " + agentKey;
            }

            StringBuilder sb = new StringBuilder();
            results.forEach((key, content) -> {
                sb.append("=== ").append(key).append(" ===\n");
                sb.append(content).append("\n\n");
            });
            return sb.toString().trim();
        } catch (BusinessException e) {
            return "Error: " + e.getMessage();
        }
    }


    /**
     * Parse JSON string to Map<String, Object>.
     * Returns empty map on null/blank input or parse failure.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseParams(String paramsJson) {
        if (StringUtils.isBlank(paramsJson)) {
            return Collections.emptyMap();
        }
        try {
            Map<String, Object> result = JsonUtil.fromJson(paramsJson, Map.class);
            return Objects.nonNull(result) ? result : Collections.emptyMap();
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
