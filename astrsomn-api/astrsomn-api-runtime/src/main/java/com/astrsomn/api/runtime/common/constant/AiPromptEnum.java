package com.astrsomn.api.runtime.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.common.base.BaseEnum;


public interface AiPromptEnum {

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
    enum IsDefaultEnum implements BaseEnum {

        
        YES("Y", "Yes"),

        
        NO("N", "No");

        private final String code;
        private final String desc;

        @Override
        public String getCode() {
            return this.code;
        }
    }
}