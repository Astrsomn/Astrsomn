package org.astrsomn.core.common.langchain.buildParam;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AstrsomnChatRequest {

    // --- 1. 核心会话上下文 ---

    /**
     * 用户当前输入的消息
     */
    private String message;

    /**
     * 选用的模型 ID
     */
    private Long modelId;

    /**
     * 会话记忆 ID (前端生成或后端分配)
     */
    private String memoryId;

    /**
     * 历史消息最大保留数 (属于记忆管理策略，也可单独拆分，这里暂留)
     */
    private Integer maxHistoryMessages = 10;

    // --- 2. 组合策略对象 (核心改动) ---

    /**
     * 模型推理参数配置 (温度、TopP等)
     */
    private ModelInferenceConfig inferenceConfig;

    /**
     * 功能开关 (联网、流式等)
     */
    private ChatFeatureFlags features;

    /**
     * 知识与工具挂载策略
     */
    private ToolStrategy toolStrategy;

    /**
     * 提示词工程策略
     */
    private PromptStrategy promptStrategy;

    // --- 3. 技术元数据 (通常不需要前端传，或由框架自动注入) ---
    // 注意：Class clazz 这种运行时类型信息通常不应该出现在 DTO/Param 中，
    // 除非是极其特殊的反射场景。建议移除或通过其他方式传递。
    private Class<?> assistantClass = AstrsomnChatRequest.class;

}
