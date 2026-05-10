package com.astrsomn.api.runtime.common.langchain.buildParam;


import com.astrsomn.api.runtime.common.langchain.buildParam.setting.*;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;


import java.util.List;

@Data
@Accessors(chain = true)
@Builder
public class AstroChatParam<T> {

    // --- 1. 核心会话上下文 ---

    /**
     * 接入层用户文本（与 {@link com.astrsomn.api.runtime.common.langchain.AstroChatRequest#getUserMessage()} 对应），用于落库预览等。
     */
    private String userMessageText;

    /**
     * 图片等资源 URL 列表（与 {@link com.astrsomn.api.runtime.common.langchain.AstroChatRequest#getFileUrlList()} 对应）。
     */
    private List<String> fileUrlList;

    /**
     * LangChain4j 用户消息；运行时责任链从 {@link #userMessageText} 与 {@link #fileUrlList} 组装，也可由调用方直接传入。
     */
    private UserMessage userMessage;
    /**
     * 智能体Key
     */
    private String agentKey;

    /**
     * 选用的模型 Key
     */
    private String modelKey;

    /**
     * 推理模型 Key
     */
    private String instanceKey;

    /**
     * 会话记忆 Key (UUID)
     */
    private String memoryKey;


    /**
     * 历史消息最大保留数 (属于记忆管理策略，也可单独拆分，这里暂留)
     */
    @Builder.Default
    private Integer maxHistoryMessages = 10;

    // --- 2. 组合策略对象 (核心改动) ---

    /**
     * 模型推理参数配置 (温度、TopP等)
     */
    @Builder.Default
    private ChatSetting chatSetting = new ChatSetting();

    /**
     * 功能开关 (联网、流式等)
     */
    @Builder.Default
    private ConversationSetting conversationSetting = new ConversationSetting();

    /**
     * 知识与工具挂载策略
     */
    @Builder.Default
    private ToolSetting toolSetting = new ToolSetting();

    /**
     * 提示词工程策略
     */
    @Builder.Default
    private PromptSetting promptSetting = new PromptSetting();

    /**
     * RAG
     */
    @Builder.Default
    private RagSetting ragSetting = new RagSetting();

    /**
     * Image
     */
    @Builder.Default
    private ImageSetting imageSetting = new ImageSetting();

    /**
     * 嵌入模型（向量）调用参数
     */
    @Builder.Default
    private EmbeddingSetting embeddingSetting = new EmbeddingSetting();

    /**
     * 模型链接参数
     */
    @Builder.Default
    private ModelSetting modelSetting = new ModelSetting();
    
    /**
     * 模型监听器
     */
    private List<ChatModelListener> chatModelListeners;

    /**
     * 是否持久化历史消息（Builder Playground 场景下可关闭）
     */
    @Builder.Default
    private boolean enableHistorySave = true;
    
    /**
     * 最终执行接口
     */
    private final Class<T> serviceClass;



    public static <T> AstroChatParam<T> of(Class<T> serviceClass, String agentKey) {
        return AstroChatParam.<T>builder()
                .serviceClass(serviceClass)
                .agentKey(agentKey)
                .build();
    }


}
