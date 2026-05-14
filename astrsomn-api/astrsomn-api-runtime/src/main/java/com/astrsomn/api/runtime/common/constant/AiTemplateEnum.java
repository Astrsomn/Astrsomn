package com.astrsomn.api.runtime.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.common.base.BaseEnum;


public interface AiTemplateEnum {


    @Getter
    @AllArgsConstructor
    enum TemplateTypeEnum implements BaseEnum {
        
        FREEMARKER("freeMarker", "FreeMarker (.ftl)"),

        
        STRING_TEMPLATE("stringTemplate", "StringTemplate (.st)");

        private final String code;
        private final String desc;
    }

    @Getter
    @AllArgsConstructor
    enum StatusEnum implements BaseEnum {
        
        ENABLED("enabled", "Enabled"),

        
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