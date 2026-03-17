package org.astrsomn.core.common.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页响应DTO
 * @param <T> 数据类型
 */
@Data
public class PageResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 每页大小
     */
    private long pageSize;

    /**
     * 当前页码
     */
    private long pageNum;

    /**
     * 总页数
     */
    private long pages;

    /**
     * 数据列表
     */
    private List<T> list;


    /**
     * 是否有下一页
     */
    private boolean hasNext;

    /**
     * 构造方法
     * @param total 总记录数
     * @param pageSize 每页大小
     * @param pageNum 当前页码
     * @param list 数据列表
     */
    public PageResponse(long total, long pageSize, int pageNum, List<T> list) {
        this.total = total;
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.pages = (int) Math.ceil((double) total / pageSize);
        this.list = list;
    }

    /**
     * 构造方法
     */
    public PageResponse() {
    }

    /**
     * 通用构建方法：从 MyBatis-Plus 的 IPage 构建 PageResponse
     */
    public static <R> PageResponse<R> buildResponse(IPage<R> page) {
        if (page == null) {
            return empty();
        }

        PageResponse<R> response = new PageResponse<>();
        response.pageNum = page.getCurrent();
        response.pageSize = page.getSize();
        response.total = page.getTotal();
        response.pages = page.getPages();
        response.list = page.getRecords() != null ? page.getRecords() : Collections.emptyList();
        response.hasNext = page.getCurrent() < page.getPages();
        return response;
    }

    /**
     * 构建空分页响应
     */
    public static <R> PageResponse<R> empty() {
        PageResponse<R> response = new PageResponse<>();
        response.pageNum = 1;
        response.pageSize = 10;
        response.total = 0;
        response.pages = 0;
        response.list = Collections.emptyList();
        response.hasNext = false;
        return response;
    }
}
