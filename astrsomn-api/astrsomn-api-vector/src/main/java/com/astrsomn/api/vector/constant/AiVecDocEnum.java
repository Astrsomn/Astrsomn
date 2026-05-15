package com.astrsomn.api.vector.constant;

import com.astrsomn.common.base.BaseEnum;
import com.astrsomn.common.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;


public interface AiVecDocEnum {

    @Getter
    @AllArgsConstructor
    enum SyncStatus implements BaseEnum {


        PENDING("PENDING", "待向量化"),


        STORED("STORED", "已入库"),


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