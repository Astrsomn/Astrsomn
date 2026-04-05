package org.astrsomn.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessExceptionEnum {





    INSTANCE_NOT_FOUND("IN_0001", "查询实例为空", "INSTANCE_NOT_FOUND");



    private final String code;

    private final String zh_message;

    private final String en_message;

}
