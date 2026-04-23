package com.astrsomn.core.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.core.common.base.BaseEnum;

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
    enum SourceTypeEnum implements BaseEnum {
        /**
         * 本地模型
         */
        USER_CUSTOM("user_custom", "用户自定义模型"),
        /**
         * 插件模型
         */
        PLUGIN("plugin", "插件模型");

        private final String code;

        private final String desc;

        @Override
        public String getCode() {
            return this.code;
        }

        /**
         * 获取所有源类型的列表
         */
        public static List<String> getAllCodes() {
            return Arrays.stream(values())
                    .map(SourceTypeEnum::getCode)
                    .collect(Collectors.toList());
        }
    }
}