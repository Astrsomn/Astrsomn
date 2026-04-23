package com.astrsomn.core.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.core.common.base.BaseEnum;

/**
 * The Enum for AiMcpConfig
 */
public interface AiMcpEnum {

    @Getter
    @AllArgsConstructor
    enum TypeEnum implements BaseEnum {
        STDIO("STDIO", "STDIO"),
        SSE("SSE", "SSE"),
        STEAMABLE("STEAMABLE", "STEAMABLE");

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
