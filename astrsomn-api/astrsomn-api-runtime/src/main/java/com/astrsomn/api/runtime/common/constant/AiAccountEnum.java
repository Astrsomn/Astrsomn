package com.astrsomn.api.runtime.common.constant;

import com.astrsomn.common.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

public interface AiAccountEnum {

    @Getter
    @AllArgsConstructor
    enum StatusEnum implements BaseEnum {


        ENABLED("enabled", "Enabled"),


        DISABLED("disabled", "Disable");

        private String code;

        private String desc;

    }

}