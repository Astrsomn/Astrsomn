package com.astrsomn.core.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.astrsomn.core.common.base.BaseEnum;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 枚举工具类
 */
public class EnumUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 将枚举列表转换为 JSON 字符串
     * @param enums 枚举列表
     * @return JSON 字符串
     */
    public static String toCapabilitiesJson(List<? extends BaseEnum> enums) {
        try {
            List<String> codes = enums.stream()
                .map(BaseEnum::getCode)
                .collect(Collectors.toList());
            return OBJECT_MAPPER.writeValueAsString(codes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert enums to JSON", e);
        }
    }
}