package com.astrsomn.common.base;


import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;


@Data
public class PageResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    
    private long total;

    
    private long pageSize;

    
    private long pageNum;

    
    private long pages;

    
    private List<T> list;


    
    private boolean hasNext;

    
    public PageResponse(long total, long pageSize, int pageNum, List<T> list) {
        this.total = total;
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.pages = (int) Math.ceil((double) total / pageSize);
        this.list = list;
    }

    
    public PageResponse() {
    }



    
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