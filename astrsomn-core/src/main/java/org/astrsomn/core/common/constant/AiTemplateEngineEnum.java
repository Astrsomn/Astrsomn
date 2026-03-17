package org.astrsomn.core.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.base.BaseEnum;
/**
 * AI 模板引擎相关字典枚举
 */
public interface AiTemplateEngineEnum {

    @Getter
    @AllArgsConstructor
    enum CategoryEnum implements BaseEnum {
        RAG("RAG", "RAG"),
        CODE_REVIEW("CODE_REVIEW", "代码审查"),
        SQL("SQL", "SQL"),
        OTHER("OTHER", "其他");

        private final String code;
        private final String desc;
    }

    /** 模板类型：FreeMarker(.ftl)、StringTemplate(.st) */
    @Getter
    @AllArgsConstructor
    enum TemplateTypeEnum implements BaseEnum {
        FREEMARKER("FREEMARKER", "FreeMarker (.ftl)"),
        STRING_TEMPLATE("STRING_TEMPLATE", "StringTemplate (.st)");

        private final String code;
        private final String desc;
    }

    @Getter
    @AllArgsConstructor
    enum EnabledEnum implements BaseEnum {
        YES(1, "启用"),
        NO(0, "禁用");

        private final Integer code;
        private final String desc;

        @Override
        public String getCode() {
            return String.valueOf(code);
        }
    }

    @Getter
    @AllArgsConstructor
    enum SystemBuiltinEnum implements BaseEnum {
        YES(1, "内置"),
        NO(0, "自定义");

        private final Integer code;
        private final String desc;

        @Override
        public String getCode() {
            return String.valueOf(code);
        }
    }
}
