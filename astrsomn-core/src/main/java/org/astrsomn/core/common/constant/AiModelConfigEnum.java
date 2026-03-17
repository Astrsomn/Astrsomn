package org.astrsomn.core.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.base.BaseEnum;

public interface AiModelConfigEnum {

    @Getter
    @AllArgsConstructor
    enum ModelType implements BaseEnum {

        /**
         * 聊天模型（Chat）
         */
        CHAT("chat", "聊天"),

        /**
         * 向量模型（Embedding）
         */
        VECTOR("vector", "向量"),

        /**
         * 视觉模型（Vision）
         */
        VISION("vision", "视觉"),

        /**
         * 图片生成模型（Image Generation）
         */
        IMAGE("image", "图片");

        private String code;

        private String desc;

    }

    @Getter
    @AllArgsConstructor
    public enum ProviderEnum implements BaseEnum {

        /**
         * OpenAI（GPT 系列）
         */
        OPENAI("openai", "OpenAI", "Y"),

        /**
         * xAI（Grok 系列）
         */
        XAI("xai", "xAI", "N"),

        /**
         * Anthropic（Claude 系列）
         */
        ANTHROPIC("anthropic", "Anthropic", "N"),

        /**
         * Google（Gemini 系列）
         */
        GOOGLE("google", "Google", "Y"),

        /**
         * 阿里巴巴（通义千问 / Qwen）
         */
        ALIBABA("alibaba", "阿里巴巴", "Y"),

        /**
         * 智谱 AI（GLM 系列）
         */
        ZHIPU("zhipu", "智谱 AI", "Y"),

        /**
         * 月之暗面（Kimi）
         */
        MOONSHOT("moonshot", "月之暗面", "N"),

        /**
         * 百度（文心一言 / ERNIE Bot）
         */
        BAIDU("baidu", "百度", "N"),

        /**
         * 百川智能（Baichuan）
         */
        BAICHUAN("baichuan", "百川智能", "N"),

        /**
         * MiniMax（abab 系列）
         */
        MINIMAX("minimax", "MiniMax", "N"),

        /**
         * 零一万物（Yi 系列）
         */
        YI("yi", "零一万物", "N"),

        /**
         * 硅基流动（SiliconFlow，聚合平台）
         */
        SILICON_FLOW("siliconflow", "硅基流动", "N"),

        /**
         * 腾讯（混元 / HunYuan）
         */
        TENCENT("tencent", "腾讯", "N"),

        /**
         * DeepSeek（深度求索）
         */
        DEEPSEEK("deepseek", "深度求索", "Y"),

        /**
         * 本地开源模型（Ollama / vLLM 等）
         */
        LOCAL("local", "本地模型", "N"),

        /**
         * 百度千帆
         */
        QIANFAN("qianfan", "百度千帆", "Y");

        private final String code;
        private final String desc;
        private final String enable;

        /**
         * 根据 code 获取枚举（忽略大小写）
         */
        public static ProviderEnum fromCode(String code) {
            if (code == null) return null;
            for (ProviderEnum provider : ProviderEnum.values()) {
                if (provider.code.equalsIgnoreCase(code)) {
                    return provider;
                }
            }
            return null;
        }

        @Override
        public String getCode() {
            return this.code;
        }

        @Override
        public String getDesc() {
            return this.desc;
        }
    }

    @Getter
    @AllArgsConstructor
    enum StatusEnum implements BaseEnum {

        /**
         * 启用
         */
        ENABLED("enabled", "启用"),

        /**
         * 禁用
         */
        DISABLED("disabled", "禁用");

        private String code;

        private String desc;

    }

    @Getter
    @AllArgsConstructor
    enum CapabilitiesEnum implements BaseEnum {

        /**
         * 生成文字
         */
        TEXT_GENERATION("text_generation", "生成文字"),

        /**
         * 识别图片
         */
        IMAGE_RECOGNITION("image_recognition", "识别图片"),

        /**
         * 生成图片
         */
        IMAGE_GENERATION("image_generation", "生成图片"),

        /**
         * 深度推理
         */
        DEEP_REASONING("deep_reasoning", "深度推理"),

        /**
         *
         */
        TEMPERATURE_SETTING("temperature_setting", "Temperature"),

        /**
         *
         */
        TOP_P_SETTING("top_p_setting", "Top-p"),

        /**
         * 存在惩罚
         */
        PRESENCE_PENALTY_SETTING("presence_penalty_setting", "存在惩罚"),

        /**
         * 频率惩罚
         */
        FREQUENCY_PENALTY_SETTING("frequency_penalty_setting", "频率惩罚"),

        /**
         * 最大Token数
         */
        MAX_TOKEN_SETTING("max_token_setting", "最大Token数"),

        /**
         * 停止序列
         */
        STOP_SEQUENCES_SETTING("stop_sequences_setting", "停止序列"),

        /**
         * 随机种子
         */
        SEED_SETTING("seed_setting", "随机种子");

        private String code;

        private String desc;

    }

}
