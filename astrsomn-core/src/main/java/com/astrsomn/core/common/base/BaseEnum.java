package com.astrsomn.core.common.base;

public interface BaseEnum {
    /**
     * 获取枚举的 code（唯一标识，用于存储和传输）
     */
    String getCode();

    /**
     * 获取枚举的描述（用于展示）
     */
    String getDesc();

}
