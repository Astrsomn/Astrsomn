package org.astrsomn.core.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.base.BaseEnum;

public interface SystemUserEnum {


    @Getter
    @AllArgsConstructor
    enum AdminEnum implements BaseEnum{

        YES("Y", "Yes"),
        NO("N", "No");

        private String code;

        private String desc;
    }



}
