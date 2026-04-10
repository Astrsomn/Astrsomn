package org.astrsomn.core.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.base.BaseEnum;
import org.astrsomn.core.common.langchain.buildParam.setting.ChatSetting;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface AiModelEnum {



    @Getter
    @AllArgsConstructor
    enum ProviderEnum implements BaseEnum {

        /**
         * OpenAI（GPT ）
         */
        OPENAI("openai", "OpenAI"),

        /**
         * xAI（Grok ）
         */
        XAI("xai", "xAI"),

        /**
         * Anthropic（Claude ）
         */
        ANTHROPIC("anthropic", "Anthropic"),

        /**
         * Google（Gemini ）
         */
        GOOGLE("google", "Google"),

        /**
         * （Alibaba / Qwen）
         */
        ALIBABA("alibaba", "Alibaba"),

        /**
         * ZhiPu （GLM）
         */
        ZHIPU("zhipu", "ZhiPu"),

        /**
         * Moonshot
         */
        MOONSHOT("moonshot", "Moonshot"),

        /**
         * Baidu
         */
        BAIDU("baidu", "Baidu"),

        /**
         * Baichuan
         */
        BAICHUAN("baichuan", "Baichuan"),

        /**
         * MiniMax
         */
        MINIMAX("minimax", "MiniMax"),

        /**
         * Yi
         */
        YI("yi", "Yi"),

        /**
         * Siliconflow
         */
        SILICON_FLOW("siliconflow", "Siliconflow"),

        /**
         * Tencent
         */
        TENCENT("tencent", "Tencent"),

        /**
         * DeepSeek
         */
        DEEPSEEK("deepseek", "Deepseek"),

        /**
         * Ollama
         */
        LOCAL("ollama", "Ollama"),

        /**
         * Qianfan
         */
        QIANFAN("qianfan", "Qianfan");

        private final String code;
        private final String desc;

        public static ProviderEnum fromCode(String code) {
            if (code == null) return null;
            for (ProviderEnum provider : ProviderEnum.values()) {
                if (provider.code.equalsIgnoreCase(code)) {
                    return provider;
                }
            }
            return null;
        }
    }

    @Getter
    @AllArgsConstructor
    enum StatusEnum implements BaseEnum {

        /**
         * Enable
         */
        ENABLED("enabled", "Enabled"),

        /**
         * Disable
         */
        DISABLED("disabled", "Disable");

        private String code;

        private String desc;

    }

    @Getter
    @AllArgsConstructor
    enum ModelTypeEnum implements BaseEnum {
        /**
         *
         */
        CHAT_MODEL("chat", "聊天模型"),

        /**
         *
         */
        EMBEDDING_MODEL("embedding", "向量模型"),

        /**
         *
         */
        IMAGE_MODEL("image", "图像模型");



        private String code;


        private String desc;

        /**
         * 获取所有模型类型的列表
         */
        public static List<String> getAllCodes() {
            return Arrays.stream(values())
                    .map(ModelTypeEnum::getCode)
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @AllArgsConstructor
    enum ChatCapabilitiesEnum implements BaseEnum {

        /**
         * 文本生成
         */
        TEXT_GENERATION("text_generation", "文本生成"),

        /**
         * 深度推理
         */
        DEEP_REASONING("deep_reasoning", "深度推理"),

        /**
         * 函数调用
         */
        FUNCTION_CALLING("function_calling", "函数调用"),

        /**
         * 流式输出
         */
        STREAMING("streaming", "流式输出"),

        /**
         * JSON 模式
         */
        JSON_MODE("json_mode", "JSON 模式"),

        /**
         * 视觉能力
         */
        VISION("vision", "视觉能力"),

        /**
         * 联网搜索
         */
        NETWORK_SEARCH("network_search", "联网搜索");

        private final String code;

        private final String desc;

        @Override
        public String getCode() {
            return this.code;
        }

        /**
         * 获取所有聊天模型能力的列表
         */
        public static List<String> getAllCodes() {
            return Arrays.stream(values())
                    .map(ChatCapabilitiesEnum::getCode)
                    .collect(Collectors.toList());
        }
    }

    /**
     * 与 LangChain4j 流式 Chat 模型 Builder 可对齐的推理超参；code 建议写入 {@code AI_MODEL.capabilities} JSON 数组。
     * 工厂仅在 capabilities 包含对应 code 且 {@link ChatSetting} 中值非空时调用 SDK。
     * <p>
     * code 命名对齐常见 Chat Completions 字段（snake_case），便于与 OpenAI 兼容实现及文档对照。
     */
    @Getter
    @AllArgsConstructor
    enum InferenceParamEnum implements BaseEnum {

        TEMPERATURE("temperature", "采样温度，对应 OpenAiStreamingChatModel / QwenStreamingChatModel 等 .temperature()"),

        TOP_P("top_p", "核采样，对应 .topP()"),

        TOP_K("top_k", "Top-K 采样，对应 QwenStreamingChatModel 等 .topK()"),

        MAX_TOKENS("max_tokens", "最大生成 token，对应 .maxTokens()"),

        SEED("seed", "随机种子，对应 .seed()"),

        PRESENCE_PENALTY("presence_penalty", "存在惩罚，对应 OpenAiStreamingChatModel .presencePenalty()"),

        FREQUENCY_PENALTY("frequency_penalty", "频率惩罚，对应 OpenAiStreamingChatModel .frequencyPenalty()");

        private final String code;

        private final String desc;

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getDesc() {
            return desc;
        }

        /**
         * 判断模型 capabilities JSON 数组是否启用了本推理参数（含历史别名，避免存量数据失效）。
         */
        public boolean containedIn(List<String> capabilities) {
            if (capabilities == null) {
                return false;
            }
            if (capabilities.contains(code)) {
                return true;
            }
            return switch (this) {
                case TEMPERATURE ->
                        capabilities.contains("temperature_setting")
                                // 旧版 OpenAI 路径曾误用能力位表示温度
                                || capabilities.contains(
                                        ChatCapabilitiesEnum.TEXT_GENERATION.getCode());
                case TOP_P -> capabilities.contains("top_p_setting");
                case TOP_K -> capabilities.contains("top_k_setting");
                case MAX_TOKENS -> capabilities.contains("max_token_setting");
                case SEED -> capabilities.contains("seed_setting");
                case PRESENCE_PENALTY -> capabilities.contains("presence_penalty_setting");
                case FREQUENCY_PENALTY -> capabilities.contains("frequency_penalty_setting");
            };
        }

        /**
         * 获取所有推理参数的列表
         */
        public static List<String> getAllCodes() {
            return Arrays.stream(values())
                    .map(InferenceParamEnum::getCode)
                    .collect(Collectors.toList());
        }
    }

    /**
     * 文生图等图像模型参数；code 写入 {@code AI_MODEL.capabilities}，与 LangChain4j {@code OpenAiImageModel} Builder 字段对齐。
     */
    @Getter
    @AllArgsConstructor
    enum ImageGenParamEnum implements BaseEnum {

        SIZE("image_size", "尺寸，如 1024x1024，对应 .size()"),

        QUALITY("image_quality", "质量 standard/hd 等，对应 .quality()"),

        STYLE("image_style", "风格 vivid/natural（DALL·E 3），对应 .style()"),

        USER("image_user", "终端用户标识，对应 .user()"),

        RESPONSE_FORMAT("image_response_format", "返回 url 或 b64_json，对应 .responseFormat()"),

        MAX_RETRIES("image_max_retries", "失败重试次数，对应 .maxRetries()"),

        TIMEOUT_SECONDS("image_timeout_seconds", "HTTP 超时（秒），对应 .timeout(Duration)");

        private final String code;

        private final String desc;

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getDesc() {
            return desc;
        }

        public boolean containedIn(List<String> capabilities) {
            if (capabilities == null) {
                return false;
            }
            if (capabilities.contains(code)) {
                return true;
            }
            return switch (this) {
                case SIZE -> capabilities.contains("size_setting");
                case STYLE -> capabilities.contains("style_setting");
                default -> false;
            };
        }

        /**
         * 获取所有图像生成参数的列表
         */
        public static List<String> getAllCodes() {
            return Arrays.stream(values())
                    .map(ImageGenParamEnum::getCode)
                    .collect(Collectors.toList());
        }
    }

    /**
     * 嵌入模型参数；code 写入 {@code AI_MODEL.capabilities}，与 LangChain4j {@code OpenAiEmbeddingModel} Builder 对齐。
     */
    @Getter
    @AllArgsConstructor
    enum EmbeddingInferenceParamEnum implements BaseEnum {

        DIMENSIONS("embedding_dimensions", "输出向量维度，对应 .dimensions()"),

        USER("embedding_user", "终端用户标识，对应 .user()"),

        MAX_RETRIES("embedding_max_retries", "失败重试次数，对应 .maxRetries()"),

        MAX_SEGMENTS_PER_BATCH("embedding_max_segments_per_batch", "单批最大条数，对应 .maxSegmentsPerBatch()"),

        ENCODING_FORMAT("embedding_encoding_format", "编码格式，对应 .encodingFormat()"),

        TIMEOUT_SECONDS("embedding_timeout_seconds", "HTTP 超时（秒），对应 .timeout(Duration)");

        private final String code;

        private final String desc;

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getDesc() {
            return desc;
        }

        public boolean containedIn(List<String> capabilities) {
            return capabilities != null && capabilities.contains(code);
        }

        /**
         * 获取所有嵌入模型参数的列表
         */
        public static List<String> getAllCodes() {
            return Arrays.stream(values())
                    .map(EmbeddingInferenceParamEnum::getCode)
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @AllArgsConstructor
    enum EmbeddingCapabilitiesEnum implements BaseEnum {

        /**
         * 文本嵌入
         */
        TEXT_EMBEDDING("text_embedding", "文本嵌入"),

        /**
         * 图像嵌入
         */
        IMAGE_EMBEDDING("image_embedding", "图像嵌入"),

        /**
         * 语义搜索
         */
        SEMANTIC_SEARCH("semantic_search", "语义搜索");

        private final String code;

        private final String desc;

        @Override
        public String getCode() {
            return this.code;
        }

        /**
         * 获取所有嵌入模型能力的列表
         */
        public static List<String> getAllCodes() {
            return Arrays.stream(values())
                    .map(EmbeddingCapabilitiesEnum::getCode)
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @AllArgsConstructor
    enum ImageCapabilitiesEnum implements BaseEnum {

        /**
         * 图像识别
         */
        IMAGE_UNDERSTANDING("image_understanding", "图像识别"),

        /**
         * 图像生成
         */
        IMAGE_GENERATION("image_generation", "图像生成"),

        /**
         * 文本转图像
         */
        TEXT_TO_IMAGE("text_to_image", "文本转图像"),

        /**
         * 图像转图像
         */
        IMAGE_TO_IMAGE("image_to_image", "图像转图像"),

        /**
         * 图像编辑
         */
        IMAGE_EDITING("image_editing", "图像编辑"),

        /**
         * 图像修复
         */
        IMAGE_INPAINTING("image_inpainting", "图像修复");

        private final String code;

        private final String desc;

        @Override
        public String getCode() {
            return this.code;
        }

        /**
         * 获取所有图像模型能力的列表
         */
        public static List<String> getAllCodes() {
            return Arrays.stream(values())
                    .map(ImageCapabilitiesEnum::getCode)
                    .collect(Collectors.toList());
        }
    }


}
