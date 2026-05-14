package com.astrsomn.api.runtime.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.common.base.BaseEnum;



public interface AiAgentEnum {

    @Getter
    @AllArgsConstructor
    enum StatusEnum implements BaseEnum {
        
        ENABLED("enabled", "Enabled"),

        
        DISABLED("disabled", "Disable");

        private final String code;
        private final String desc;
    }

    @Getter
    @AllArgsConstructor
    enum MemoryModeEnum implements BaseEnum {
        SHORT_TERM("shortTerm", "Short Term"),
        LONG_TERM("longTerm", "Long Term"),
        HYBRID("hybrid", "hybrid");

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
    }
}