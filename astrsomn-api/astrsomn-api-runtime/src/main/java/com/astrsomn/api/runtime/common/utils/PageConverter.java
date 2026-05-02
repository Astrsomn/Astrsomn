package com.astrsomn.api.runtime.common.utils;

import com.astrsomn.common.base.PageResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Collections;

public class PageConverter {
    /**
     * 将 MyBatis-Plus 的 IPage 转换为 Common 模块的 PageResponse
     */
    public static <R> PageResponse<R> toResponse(IPage<R> page) {
        if (page == null) {
            return new PageResponse<>(0, 10, 1, Collections.emptyList());
        }
        PageResponse<R> response = new PageResponse<>(
                page.getTotal(),
                page.getSize(),
                (int) page.getCurrent(),
                page.getRecords()
        );
        response.setHasNext(page.getCurrent() < page.getPages());
        return response;
    }
}