package com.astrsomn.core.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.core.common.base.BaseEnum;

public interface AiAccountEnum {

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
