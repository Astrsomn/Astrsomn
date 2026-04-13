package org.astrsomn.core.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.base.BaseEnum;




/**
 * AI 模型参数与能力枚举定义
 * 对齐 LangChain4j 各个 Model 接口的 Builder 属性与能力检查
 */
public interface AiModelParamEnum {

    /**
     * 聊天模型能力位
     * 用于标识模型是否支持特定的交互模式
     */
    @Getter
    @AllArgsConstructor
    enum ChatCapabilitiesEnum implements BaseEnum {
        STREAMING("streaming", "流式输出 (StreamingChatLanguageModel)"),
        TOOLS("tools", "工具/函数调用 (ToolSpecifications)"),
        VISION("vision", "视觉理解 (ImageContent)"),
        JSON_MODE("json_mode", "JSON 模式 (ResponseFormat)"),
        DEEP_REASONING("deep_reasoning", "深度推理 (如 DeepSeek-R1 / O1)"),
        CONTEXT_CACHING("context_caching", "上下文缓存 (Context Caching)");

        private final String code;
        private final String desc;
    }

    /**
     * 向量模型能力位
     */
    @Getter
    @AllArgsConstructor
    enum EmbeddingCapabilityEnum implements BaseEnum {
        TEXT_EMBEDDING("text_embedding", "文本向量化"),
        IMAGE_EMBEDDING("image_embedding", "图像向量化");

        private final String code;
        private final String desc;
    }

    /**
     * 图像模型能力位
     */
    @Getter
    @AllArgsConstructor
    enum ImageCapabilitiesEnum implements BaseEnum {
        TEXT_TO_IMAGE("text_to_image", "文生图"),
        IMAGE_TO_IMAGE("image_to_image", "图生图"),
        IMAGE_EDITING("image_editing", "图像编辑/修复");

        private final String code;
        private final String desc;
    }

    /**
     * 聊天模型推理超参数
     * 对应各 Provider Builder 的 set 方法
     */
    @Getter
    @AllArgsConstructor
    enum ChatParamEnum implements BaseEnum {
        TEMPERATURE("temperature", "采样温度"),
        TOP_P("top_p", "核采样 (Top-P)"),
        TOP_K("top_k", "Top-K 采样"),
        MAX_TOKENS("max_tokens", "最大生成 Token 数"),
        STOP_SEQUENCES("stop_sequences", "停止词列表"),
        SEED("seed", "随机种子"),
        PRESENCE_PENALTY("presence_penalty", "话题存在惩罚"),
        FREQUENCY_PENALTY("frequency_penalty", "频率惩罚"),
        LOGIT_BIAS("logit_bias", "Token 偏好偏差");

        private final String code;
        private final String desc;
    }

    /**
     * 向量模型参数
     */
    @Getter
    @AllArgsConstructor
    enum EmbeddingParamEnum implements BaseEnum {
        DIMENSIONS("dimensions", "向量输出维度"),
        MODEL_NAME("model_name", "模型名称"),
        USER("user", "终端用户标识");

        private final String code;
        private final String desc;
    }

    /**
     * 图像生成参数
     */
    @Getter
    @AllArgsConstructor
    enum ImageParamEnum implements BaseEnum {
        SIZE("size", "图片尺寸 (如 1024x1024)"),
        QUALITY("quality", "质量 (standard/hd)"),
        STYLE("style", "风格 (vivid/natural)"),
        RESPONSE_FORMAT("response_format", "响应格式 (url/b64_json)");

        private final String code;
        private final String desc;
    }

    /**
     * 系统与 HTTP 级配置
     * 对应 OkHttpClient 或 SDK 基础配置
     */
    @Getter
    @AllArgsConstructor
    enum SystemConfigParam implements BaseEnum {
        BASE_URL("base_url", "接口基础地址"),
        API_KEY("api_key", "令牌/密钥"),
        TIMEOUT_SECONDS("timeout_seconds", "超时时间 (秒)"),
        MAX_RETRIES("max_retries", "最大重试次数"),
        LOG_REQUESTS("log_requests", "启用请求日志"),
        LOG_RESPONSES("log_responses", "启用响应日志"),
        PROXY_URL("proxy_url", "代理服务器地址");

        private final String code;
        private final String desc;
    }
}