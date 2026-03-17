package org.astrsomn.core.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.base.BaseEnum;

/**
 * AI 提示词相关字典枚举
 */
public interface AiPromptEnum {

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

        private final String code;
        private final String desc;
    }

    @Getter
    @AllArgsConstructor
    enum SceneEnum implements BaseEnum {

        /**
         * 通用对话
         */
        CHAT("chat", "通用对话"),

        /**
         * 摘要
         */
        SUMMARIZE("summarize", "摘要"),

        /**
         * 翻译
         */
        TRANSLATE("translate", "翻译"),

        /**
         * 问答
         */
        QA("qa", "问答"),

        /**
         * 写作
         */
        WRITING("writing", "写作"),

        /**
         * 代码
         */
        CODE("code", "代码"),

        /**
         * 数据分析
         */
        DATA_ANALYSIS("data_analysis", "数据分析"),

        /**
         * 客服
         */
        CUSTOMER_SERVICE("customer_service", "客服"),

        /**
         * 其他
         */
        OTHER("other", "其他");

        private final String code;
        private final String desc;
    }
}
