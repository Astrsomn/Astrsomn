package org.astrsomn.core.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.base.BaseEnum;


/**
 * AI Agent
 */
public interface AiAgentEnum {

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
    enum IsDefaultEnum  {
        YES(1, "Yes"),
        NO(0, "No");

        private final Integer code;
        private final String desc;
    }
}
