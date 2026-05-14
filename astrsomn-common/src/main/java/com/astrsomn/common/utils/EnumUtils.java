package com.astrsomn.common.utils;

import com.astrsomn.common.base.BaseEnum;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;


public class EnumUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


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