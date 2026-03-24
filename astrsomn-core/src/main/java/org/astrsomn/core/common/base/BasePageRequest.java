package org.astrsomn.core.common.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serializable;

/**
 * 基础分页请求DTO
 */
@Data
public class BasePageRequest<T> extends BaseRequest implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    private Integer pageNo = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;


    /**
     * 查询参数
     */
    private T param;

    public BasePageRequest() {
    }

    public BasePageRequest(T t) {
        this.param = t;
    }


    public IPage buildPage() {

        long current = (pageNo == null || pageNo < 1) ? 1L : pageNo.longValue();
        long size = (pageSize == null || pageSize < 1) ? 10L : pageSize.longValue();

        return new Page<>(current, size);
    }
}
