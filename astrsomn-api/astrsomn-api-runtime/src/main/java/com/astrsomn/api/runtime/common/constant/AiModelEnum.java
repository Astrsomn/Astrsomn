package com.astrsomn.api.runtime.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.common.base.BaseEnum;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface AiModelEnum {



    @Getter
    @AllArgsConstructor
    enum ProviderEnum implements BaseEnum {

        
        OPENAI("openai", "OpenAI"),

        
        XAI("xai", "xAI"),

        
        ANTHROPIC("anthropic", "Anthropic"),

        
        GOOGLE("google", "Google"),

        
        ALIBABA("alibaba", "Alibaba"),

        
        ZHIPU("zhipu", "ZhiPu"),

        
        MOONSHOT("moonshot", "Moonshot"),

        
        BAIDU("baidu", "Baidu"),

        
        BAICHUAN("baichuan", "Baichuan"),

        
        MINIMAX("minimax", "MiniMax"),

        
        YI("yi", "Yi"),

        
        SILICON_FLOW("siliconflow", "Siliconflow"),

        
        TENCENT("tencent", "Tencent"),

        
        DEEPSEEK("deepseek", "Deepseek"),

        
        LOCAL("ollama", "Ollama"),

        
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

        
        ENABLED("enabled", "Enabled"),

        
        DISABLED("disabled", "Disable");

        private String code;

        private String desc;

    }

    @Getter
    @AllArgsConstructor
    enum ModelTypeEnum implements BaseEnum {
        
        CHAT_MODEL("chat", "聊天模型"),

        
        EMBEDDING_MODEL("embedding", "向量模型"),

        
        IMAGE_MODEL("image", "图像模型");



        private String code;


        private String desc;

        
        public static List<String> getAllCodes() {
            return Arrays.stream(values())
                    .map(ModelTypeEnum::getCode)
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @AllArgsConstructor
    enum SourceTypeEnum implements BaseEnum {
        
        USER_CUSTOM("user_custom", "用户自定义模型"),
        
        PLUGIN("plugin", "插件模型");

        private final String code;

        private final String desc;

        @Override
        public String getCode() {
            return this.code;
        }

        
        public static List<String> getAllCodes() {
            return Arrays.stream(values())
                    .map(SourceTypeEnum::getCode)
                    .collect(Collectors.toList());
        }
    }
}