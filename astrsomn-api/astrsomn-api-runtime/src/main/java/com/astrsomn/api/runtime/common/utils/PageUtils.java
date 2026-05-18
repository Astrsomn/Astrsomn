package com.astrsomn.api.runtime.common.utils;

import com.astrsomn.common.base.BasePageRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public class PageUtils {


    public static <E> IPage<E> buildPage(BasePageRequest<?> request) {
        long current = (request.getPageNo() == null || request.getPageNo() < 1)
                ? 1L : request.getPageNo().longValue();
        long size = (request.getPageSize() == null || request.getPageSize() < 1)
                ? 10L : request.getPageSize().longValue();


        if (size > 100) size = 100L;

        return new Page<>(current, size);
    }
}