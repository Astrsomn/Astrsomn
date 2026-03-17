package org.astrsomn.core.common.base;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类
 * @param <T> ID类型
 */
@Data
public class BaseEntity<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private T id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 是否删除
     */
    private Boolean deleted = false;
    

}
