package com.astrsomn.starter.runtime.langchain.tool.template;

import com.astrsomn.api.runtime.common.langchain.ChatStreamEnum;
import com.astrsomn.common.utils.JsonUtil;
import com.astrsomn.starter.runtime.langchain.template.TemplateRenderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonStringSchema;
import dev.langchain4j.service.tool.ToolExecutor;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderRequest;
import dev.langchain4j.service.tool.ToolProviderResult;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Dynamic tool provider that creates one ToolSpecification per template key.
 * Each tool is named {@code renderHtml_{templateKey}} so the SSE stream
 * classifier recognises it as an HTML-type event.
 */
public class DynamicTemplateToolProvider implements ToolProvider {

    private final List<String> templateKeys;
    private final TemplateRenderService templateRenderService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DynamicTemplateToolProvider(List<String> templateKeys, TemplateRenderService templateRenderService) {
        this.templateKeys = templateKeys;
        this.templateRenderService = templateRenderService;
    }

    @Override
    public ToolProviderResult provideTools(ToolProviderRequest request) {
        ToolProviderResult.Builder resultBuilder = ToolProviderResult.builder();

        JsonStringSchema paramsSchema = JsonStringSchema.builder()
                .description("JSON object with template parameters, e.g. {\"name\":\"Alice\"}")
                .build();

        JsonObjectSchema parameters = JsonObjectSchema.builder()
                .addProperty("paramsJson", paramsSchema)
                .required(Collections.singletonList("paramsJson"))
                .build();

        for (String templateKey : templateKeys) {
            String toolName = "renderHtml_" + templateKey;

            ToolSpecification spec = ToolSpecification.builder()
                    .name(toolName)
                    .addMetadata("type", ChatStreamEnum.AstroEventType.HTML.getCode())
                    .description("Render HTML template '" + templateKey
                            + "' with the provided JSON parameters and return the rendered HTML content.")
                    .parameters(parameters)
                    .build();

            ToolExecutor executor = (toolExecutionRequest, memoryId) -> {
                try {
                    Map<String, Object> arguments = objectMapper.readValue(
                            toolExecutionRequest.arguments(), Map.class);
                    String paramsJson = (String) arguments.get("paramsJson");
                    Map<String, Object> params = parseParams(paramsJson);
                    return templateRenderService.renderTemplate(templateKey, params);
                } catch (Exception e) {
                    return "Error rendering template '" + templateKey + "': " + e.getMessage();
                }
            };

            resultBuilder.add(spec, executor);
        }

        return resultBuilder.build();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseParams(String paramsJson) {
        if (paramsJson == null || paramsJson.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            Map<String, Object> result = JsonUtil.fromJson(paramsJson, Map.class);
            return result != null ? result : Collections.emptyMap();
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
