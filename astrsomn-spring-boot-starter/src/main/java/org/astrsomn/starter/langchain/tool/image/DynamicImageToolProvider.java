package org.astrsomn.starter.langchain.tool.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonStringSchema;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.service.tool.ToolExecutor;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderRequest;
import dev.langchain4j.service.tool.ToolProviderResult;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.langchain.ChatStreamEnum;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public class DynamicImageToolProvider implements ToolProvider {

    private final ImageModel imageModel;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ToolProviderResult provideTools(ToolProviderRequest request) {

        JsonStringSchema promptSchema = JsonStringSchema.builder()
                .description("需要生成图片的详细文字描述，例如：'极简主义风格的办公室设计图'")
                .build();


        JsonObjectSchema parameters = JsonObjectSchema.builder()
                .addProperty("prompt", promptSchema)
                .required(Collections.singletonList("prompt"))
                .build();


        ToolSpecification spec = ToolSpecification.builder()
                .name("image_generateImage")
                .addMetadata("type", ChatStreamEnum.AstroEventType.IMAGE.getCode())
                .description("调用 AI 绘画模型生成一张图片。")
                .parameters(parameters)
                .build();

        ToolExecutor executor = (toolExecutionRequest, memoryId) -> {
            try {

                Map<String, Object> arguments = objectMapper.readValue(
                        toolExecutionRequest.arguments(), Map.class);

                String promptValue = (String) arguments.get("prompt");

                return imageModel.generate(promptValue).content().url().toString();
            } catch (Exception e) {
                return "绘画引擎执行失败: " + e.getMessage();
            }
        };

        return ToolProviderResult.builder()
                .add(spec, executor)
                .build();
    }
}
