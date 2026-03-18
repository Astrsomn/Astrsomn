package org.astrsomn.core.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.base.BaseEnum;


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
    enum EnableFlagEnum implements BaseEnum {
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
}
