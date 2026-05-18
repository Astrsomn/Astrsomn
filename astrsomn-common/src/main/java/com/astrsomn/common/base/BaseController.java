package com.astrsomn.common.base;


import com.astrsomn.common.utils.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


public class BaseController {


    public static long[] parseLongIds(String idsStr, String delimiter) {
        if (StringUtils.isBlank(idsStr)) {
            return new long[0];
        }



        return Arrays.stream(idsStr.split(Pattern.quote(delimiter)))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .mapToLong(s -> {
                    try {
                        return Long.parseLong(s);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid ID format: '" + s + "'", e);
                    }
                })
                .toArray();
    }

    protected BaseResponse<?> success() {
        return BaseResponse.success();
    }

    protected <T> BaseResponse<T> success(T data) {
        return BaseResponse.success(data);
    }

    protected <T> BaseResponse<PageResponse<T>> successPage(long total, int pageSize, int pageNum, List<T> list) {
        PageResponse<T> pageResponse = new PageResponse<>(total, pageSize, pageNum, list);
        return BaseResponse.success(pageResponse);
    }

    protected BaseResponse<?> fail(String message) {
        return BaseResponse.fail(message);
    }

    protected <T> BaseResponse<T> fail(String message, T data) {
        return BaseResponse.fail(message, data);
    }

}