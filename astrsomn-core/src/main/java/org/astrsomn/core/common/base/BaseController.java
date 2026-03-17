package org.astrsomn.core.common.base;



import java.util.List;

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
}
