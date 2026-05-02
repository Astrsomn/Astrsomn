package com.astrsomn.api.runtime.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.common.base.BaseEnum;


/**
 * AI 工具配置相关字典枚举
 */
public interface AiToolEnum {

    @Getter
    @AllArgsConstructor
    enum TypeEnum implements BaseEnum {
        HTML("html", "Html"),
        METHOD("method", "Method");

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
}
