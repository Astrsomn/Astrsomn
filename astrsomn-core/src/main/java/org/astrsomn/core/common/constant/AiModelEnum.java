package org.astrsomn.core.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.base.BaseEnum;

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
    enum CapabilitiesEnum implements BaseEnum {

        /**
         * Capability to generate natural language text.
         */
        TEXT_GENERATION("text_generation", "Generates natural language text"),

        /**
         * Capability to analyze and interpret image content (Vision).
         */
        IMAGE_RECOGNITION("image_recognition", "Analyzes and interprets image content"),

        /**
         * Capability to generate images from text prompts.
         */
        IMAGE_GENERATION("image_generation", "Generates images from text prompts"),

        /**
         * Capability for advanced multi-step logical reasoning and chain-of-thought processing.
         */
        DEEP_REASONING("deep_reasoning", "Performs advanced multi-step logical reasoning"),

        /**
         * Configuration to control the randomness/creativity of the output.
         * Higher values make output more random, lower values more deterministic.
         */
        TEMPERATURE_SETTING("temperature_setting", "Controls output randomness and creativity"),

        /**
         * Configuration for Nucleus Sampling (Top-p).
         * Limits token sampling to the smallest set of tokens whose cumulative probability exceeds p.
         */
        TOP_P_SETTING("top_p_setting", "Nucleus sampling probability threshold"),

        /**
         * Configuration to penalize tokens based on whether they appear in the generated text so far.
         * Encourages the model to talk about new topics.
         */
        PRESENCE_PENALTY_SETTING("presence_penalty_setting", "Penalizes repetition of new tokens based on presence"),

        /**
         * Configuration to penalize tokens based on their existing frequency in the generated text.
         * Reduces verbatim repetition of the same line.
         */
        FREQUENCY_PENALTY_SETTING("frequency_penalty_setting", "Penalizes token repetition based on frequency"),

        /**
         * Configuration to limit the maximum number of tokens in the generated response.
         */
        MAX_TOKEN_SETTING("max_token_setting", "Maximum number of tokens to generate"),

        /**
         * Configuration to define specific sequences that will stop the generation process.
         */
        STOP_SEQUENCES_SETTING("stop_sequences_setting", "Custom sequences to stop text generation"),

        /**
         * Configuration to set a seed for reproducible output generation.
         */
        SEED_SETTING("seed_setting", "Seed for reproducible deterministic output");

        private final String code;

        private final String desc;

        @Override
        public String getCode() {
            return this.code;
        }
    }

}
