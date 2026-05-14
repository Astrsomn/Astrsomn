package com.astrsomn.common.base;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class BasePageRequest<T> extends BaseRequest {


    private static final long serialVersionUID = 1L;


    private Integer pageNo = 1;


    private Integer pageSize = 10;


    private T param;

    public BasePageRequest() {
    }

    public BasePageRequest(T t) {
        this.param = t;
    }


}