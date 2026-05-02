package com.astrsomn.common.base;


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
