package org.astrsomn.core.common.base;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础请求DTO
 */
@Data
public class BaseRequest implements Serializable {
    private static final long serialVersionUID = 1L;


    private String createUser;


    private String updateUser;


    private LocalDateTime createTime;


    private LocalDateTime updateTime;
}
