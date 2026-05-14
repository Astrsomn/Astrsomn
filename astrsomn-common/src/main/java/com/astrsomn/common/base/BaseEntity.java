package com.astrsomn.common.base;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
public class BaseEntity<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1L;

    
    private T id;

    
    private LocalDateTime createTime;

    
    private LocalDateTime updateTime;

    
    private String createUser;

    
    private String updateUser;

    
    private Boolean deleted = false;

    
    private String envCode;
}