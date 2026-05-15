package com.astrsomn.api.vector.constant;

import com.astrsomn.common.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

public interface AiVecSourceEnum {


    @Getter
    @AllArgsConstructor
    enum StatusEnum implements BaseEnum {


        ENABLED("enabled", "Enabled"),


        DISABLED("disabled", "Disable");

        private String code;

        private String desc;

    }
}