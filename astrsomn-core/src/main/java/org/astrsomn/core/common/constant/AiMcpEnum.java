package org.astrsomn.core.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.base.BaseEnum;

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
    enum EnabledEnum implements BaseEnum {
        YES("Y", "YES" ),
        NO("N", "NO");

        private final String code;
        private final String desc;

    }
}
