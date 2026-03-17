package org.astrsomn.core.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import opple.data.common.dto.constant.BaseEnum;

/**
 * AI 工具配置相关字典枚举
 */
public interface AiToolConfigEnum {

    @Getter
    @AllArgsConstructor
    enum TypeEnum implements BaseEnum {
        HTML("HTML", "HTML"),
        METHOD("METHOD", "方法");

        private final String code;
        private final String desc;
    }

    @Getter
    @AllArgsConstructor
    enum StatusEnum implements BaseEnum {
        ENABLED("ENABLED", "启用"),
        DISABLED("DISABLED", "禁用");

        private final String code;
        private final String desc;
    }
}
