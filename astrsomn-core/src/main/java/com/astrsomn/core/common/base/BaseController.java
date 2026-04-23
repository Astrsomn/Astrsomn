package com.astrsomn.core.common.base;



import com.astrsomn.core.common.utils.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 基础控制器
 */
public class BaseController {

    /**
     * 成功响应
     * @return 响应对象
     */
    protected BaseResponse<?> success() {
        return BaseResponse.success();
    }

    /**
     * 成功响应
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 响应对象
     */
    protected <T> BaseResponse<T> success(T data) {
        return BaseResponse.success(data);
    }

    /**
     * 分页响应
     * @param total 总记录数
     * @param pageSize 每页大小
     * @param pageNum 当前页码
     * @param list 数据列表
     * @param <T> 数据类型
     * @return 响应对象
     */
    protected <T> BaseResponse<PageResponse<T>> successPage(long total, int pageSize, int pageNum, List<T> list) {
        PageResponse<T> pageResponse = new PageResponse<>(total, pageSize, pageNum, list);
        return BaseResponse.success(pageResponse);
    }

    /**
     * 失败响应
     * @param message 错误消息
     * @return 响应对象
     */
    protected BaseResponse<?> fail(String message) {
        return BaseResponse.fail(message);
    }

    /**
     * 失败响应
     * @param message 错误消息
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 响应对象
     */
    protected <T> BaseResponse<T> fail(String message, T data) {
        return BaseResponse.fail(message, data);
    }


    public static long[] parseLongIds(String idsStr, String delimiter) {
        if (StringUtils.isBlank(idsStr)) {
            return new long[0];
        }

        // 注意：如果分隔符是正则特殊字符（如 | .），split 需要转义，这里假设传入的是普通字符
        // 为了安全，可以使用 Pattern.quote 或者直接按字符分割
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

}
