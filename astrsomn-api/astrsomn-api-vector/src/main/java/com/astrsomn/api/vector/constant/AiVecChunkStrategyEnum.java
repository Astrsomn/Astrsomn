package com.astrsomn.api.vector.constant;

import com.astrsomn.common.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AiVecChunkStrategyEnum implements BaseEnum {

    RECURSIVE("RECURSIVE", "递归分割"),

    FIXED_SIZE("FIXED_SIZE", "固定大小"),

    PARAGRAPH("PARAGRAPH", "按段落"),

    SENTENCE("SENTENCE", "按句子");

    private final String code;
    private final String desc;

    public static AiVecChunkStrategyEnum fromCodeOrDefault(String raw) {
        if (raw == null || raw.isBlank()) {
            return RECURSIVE;
        }
        for (AiVecChunkStrategyEnum v : values()) {
            if (v.code.equalsIgnoreCase(raw.trim())) {
                return v;
            }
        }
        return RECURSIVE;
    }
}
