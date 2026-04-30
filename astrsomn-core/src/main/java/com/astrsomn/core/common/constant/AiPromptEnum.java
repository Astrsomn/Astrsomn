package com.astrsomn.core.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.commn.base.BaseEnum;


public interface AiPromptEnum {

    @Getter
    @AllArgsConstructor
    enum StatusEnum implements BaseEnum {

        /**
         * Enabled
         */
        ENABLED("enabled", "Enabled"),

        /**
         * Disabled
         */
        DISABLED("disabled", "Disabled");

        private final String code;
        private final String desc;
    }
    /**
     * Enumerates the supported application scenarios or use cases.
     */
    @Getter
    @AllArgsConstructor
    public enum SceneEnum implements BaseEnum {

        /**
         * General purpose conversation and chat.
         */
        CHAT("chat", "General Conversation"),

        /**
         * Text summarization and condensation.
         */
        SUMMARIZE("summarize", "Text Summarization"),

        /**
         * Language translation between different locales.
         */
        TRANSLATE("translate", "Language Translation"),

        /**
         * Question answering and knowledge retrieval.
         */
        QA("qa", "Question Answering"),

        /**
         * Creative writing, content generation, and drafting.
         */
        WRITING("writing", "Creative Writing & Content Generation"),

        /**
         * Code generation, completion, debugging, and explanation.
         */
        CODE("code", "Code Generation & Assistance"),

        /**
         * Data analysis, interpretation, and insight extraction.
         */
        DATA_ANALYSIS("data_analysis", "Data Analysis & Insights"),

        /**
         * Customer support, helpdesk, and service automation.
         */
        CUSTOMER_SERVICE("customer_service", "Customer Support & Service"),

        /**
         * Other unspecified or custom scenarios.
         */
        OTHER("other", "Other");

        private final String code;
        private final String desc;

        @Override
        public String getCode() {
            return this.code;
        }
    }
}
