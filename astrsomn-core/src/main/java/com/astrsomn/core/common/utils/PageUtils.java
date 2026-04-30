package com.astrsomn.core.common.utils;

import com.astrsomn.commn.base.BasePageRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public class PageUtils {

    /**
     * 根据请求对象构建 MyBatis-Plus 的 Page 对象
     */
    public static <E> IPage<E> buildPage(BasePageRequest<?> request) {
        long current = (request.getPageNo() == null || request.getPageNo() < 1)
                ? 1L : request.getPageNo().longValue();
        long size = (request.getPageSize() == null || request.getPageSize() < 1)
                ? 10L : request.getPageSize().longValue();

        // 生产级建议：增加最大分页限制保护数据库
        if (size > 100) size = 100L;

        return new Page<>(current, size);
    }
}