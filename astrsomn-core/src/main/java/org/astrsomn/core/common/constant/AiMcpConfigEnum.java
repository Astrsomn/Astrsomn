package org.astrsomn.core.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.base.BaseEnum;

/**
 * AI MCP 配置相关字典枚举
 */
public interface AiMcpConfigEnum {

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
        YES(1, "是"),
        NO(0, "否");

        private final Integer code;
        private final String desc;

        @Override
        public String getCode() {
            return String.valueOf(code);
        }
    }
}
