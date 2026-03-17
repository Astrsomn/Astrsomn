package org.astrsomn.core.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.base.BaseEnum;


/**
 * AI 智能体配置相关字典枚举
 */
public interface AiAgentConfigEnum {

    @Getter
    @AllArgsConstructor
    enum StatusEnum implements BaseEnum {
        ENABLED("ENABLED", "启用"),
        DISABLED("DISABLED", "禁用");

        private final String code;
        private final String desc;
    }

    @Getter
    @AllArgsConstructor
    enum MemoryModeEnum implements BaseEnum {
        SHORT_TERM("SHORT_TERM", "短期记忆"),
        LONG_TERM("LONG_TERM", "长期记忆"),
        HYBRID("HYBRID", "混合记忆");

        private final String code;
        private final String desc;
    }
}
