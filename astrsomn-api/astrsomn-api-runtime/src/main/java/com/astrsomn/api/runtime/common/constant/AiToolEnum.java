package com.astrsomn.api.runtime.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.common.base.BaseEnum;



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

        
        ENABLED("enabled", "Enabled"),

        
        DISABLED("disabled", "Disable");

        private String code;

        private String desc;

    }
}