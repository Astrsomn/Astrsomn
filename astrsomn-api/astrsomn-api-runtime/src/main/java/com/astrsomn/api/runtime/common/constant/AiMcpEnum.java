package com.astrsomn.api.runtime.common.constant;

import com.astrsomn.common.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;


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


        ENABLED("enabled", "Enabled"),


        DISABLED("disabled", "Disable");

        private String code;

        private String desc;

    }
}