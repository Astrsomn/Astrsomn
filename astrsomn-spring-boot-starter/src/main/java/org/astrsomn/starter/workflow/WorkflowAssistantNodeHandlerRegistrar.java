package org.astrsomn.starter.workflow;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.langchain.AstroChatAssistant;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.starter.langchain.factory.AstroAssistantFactory;
import org.astrsomn.workflow.core.context.WorkflowContext;
import org.astrsomn.workflow.core.model.FlowNode;
import org.astrsomn.workflow.core.model.FlowNodeType;
import org.astrsomn.workflow.core.spi.NodeHandlerRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * 将工作流 {@link FlowNodeType#TASK} 与 {@link AstroAssistantFactory} 绑定：节点 {@code data.agentKey} 非空时，
 * 使用库中 Agent 配置创建 {@link AstroChatAssistant} 并执行 {@link AstroChatAssistant#chat(String, String)}，
 * 结果写入 {@code data.outputVar}（默认 {@code lastAssistantReply}）。
 * <p>
 * 需在图中为 TASK 配置变量：{@code promptVar}（默认 {@code userMessage}）对应 {@link WorkflowContext} 中的用户输入；
 * {@code memoryVar}（默认 {@code memoryKey}）可选，缺省则用 {@link WorkflowContext#getExecutionId()}。
 * <p>
 * 依赖 {@link NodeHandlerRegistry} Bean（由 {@code astrsomn-workflow-spring-boot-starter} 提供）。
 */
@Component
@RequiredArgsConstructor
@ConditionalOnBean(NodeHandlerRegistry.class)
public class WorkflowAssistantNodeHandlerRegistrar {

    private final NodeHandlerRegistry nodeHandlerRegistry;
    private final AstroAssistantFactory astroAssistantFactory;

    @PostConstruct
    void registerAssistantTaskHandler() {
        nodeHandlerRegistry.registerByType(FlowNodeType.TASK, this::invokeAssistantIfConfigured);
    }

    private void invokeAssistantIfConfigured(FlowNodeType type, FlowNode node, WorkflowContext ctx) throws Exception {
        Map<String, Object> data = node.getData();
        if (data == null || !data.containsKey("agentKey")) {
            return;
        }
        String agentKey = String.valueOf(data.get("agentKey"));
        if (agentKey.isBlank()) {
            return;
        }

        String promptVar = stringProp(data, "promptVar", "userMessage");
        String userMessage = Optional.ofNullable(ctx.getVar(promptVar)).map(String::valueOf).orElse("");
        if (userMessage.isBlank()) {
            throw new IllegalStateException(
                    "TASK node requires context variable '" + promptVar + "' for user message, nodeId=" + node.getId());
        }

        String memoryVar = stringProp(data, "memoryVar", "memoryKey");
        String memoryKey = Optional.ofNullable(ctx.getVar(memoryVar)).map(String::valueOf).orElse(null);
        if (memoryKey == null || memoryKey.isBlank()) {
            memoryKey = ctx.getExecutionId();
        }

        AstroChatParam<AstroChatAssistant> param = AstroChatParam.<AstroChatAssistant>builder()
                .serviceClass(AstroChatAssistant.class)
                .agentKey(agentKey)
                .userMessage(userMessage)
                .memoryKey(memoryKey)
                .build();

        AstroChatAssistant assistant = astroAssistantFactory.createAssistant(param);
        String reply = assistant.chat(param.getUserMessage(), param.getMemoryKey());

        String outputVar = stringProp(data, "outputVar", "lastAssistantReply");
        ctx.putVar(outputVar, reply);
    }

    private static String stringProp(Map<String, Object> data, String key, String defaultValue) {
        Object v = data.get(key);
        return v != null ? String.valueOf(v) : defaultValue;
    }
}
