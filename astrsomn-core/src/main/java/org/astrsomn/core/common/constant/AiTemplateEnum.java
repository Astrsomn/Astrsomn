package org.astrsomn.core.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.base.BaseEnum;

/**
 * AI Template Enum
 */
public interface AiTemplateEnum {


    @Getter
    @AllArgsConstructor
    enum TemplateTypeEnum implements BaseEnum {
        /**
         * Freemarker
         */
        FREEMARKER("freeMarker", "FreeMarker (.ftl)"),

        /**
         * StringTemplate
         */
        STRING_TEMPLATE("stringTemplate", "StringTemplate (.st)");

        private final String code;
        private final String desc;
    }

    @Getter
    @AllArgsConstructor
    enum EnabledEnum implements BaseEnum {
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

    @Getter
    @AllArgsConstructor
    enum SystemBuiltinEnum implements BaseEnum {

        YES("Y", "IS Builtin"),

        NO("N", "NOT Builtin");

        private final String code;
        private final String desc;

        @Override
        public String getCode() {
            return String.valueOf(code);
        }
    }
}
