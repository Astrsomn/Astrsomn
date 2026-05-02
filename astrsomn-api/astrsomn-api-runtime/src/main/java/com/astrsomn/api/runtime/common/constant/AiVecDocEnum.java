package com.astrsomn.api.runtime.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.common.base.BaseEnum;
import com.astrsomn.common.utils.StringUtils;

import java.util.Optional;

/**
 * AI 向量文档相关枚举。
 */
public interface AiVecDocEnum {

    @Getter
    @AllArgsConstructor
    enum SyncStatus implements BaseEnum {

        /**
         * 待向量化（仅元数据/文件已落库）
         */
        PENDING("PENDING", "待向量化"),

        /**
         * 已入库（向量侧已写入）
         */
        STORED("STORED", "已入库"),

        /**
         * 已失效
         */
        INVALID("INVALID", "已失效");

        private final String code;
        private final String desc;

        public static Optional<SyncStatus> fromCode(String raw) {
            String c = StringUtils.trimToNull(raw);
            if (c == null) {
                return Optional.empty();
            }
            for (SyncStatus v : values()) {
                if (v.code.equalsIgnoreCase(c)) {
                    return Optional.of(v);
                }
            }
            return Optional.empty();
        }
    }
}
