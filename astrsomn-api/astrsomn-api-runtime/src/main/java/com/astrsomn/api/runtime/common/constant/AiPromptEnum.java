package com.astrsomn.api.runtime.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.common.base.BaseEnum;


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

    @Getter
    @AllArgsConstructor
    enum IsDefaultEnum implements BaseEnum {

        /**
         * Is default
         */
        YES("Y", "Yes"),

        /**
         * Is not default
         */
        NO("N", "No");

        private final String code;
        private final String desc;

        @Override
        public String getCode() {
            return this.code;
        }
    }
}
