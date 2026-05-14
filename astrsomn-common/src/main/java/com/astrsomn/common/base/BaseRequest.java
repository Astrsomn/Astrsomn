package com.astrsomn.common.base;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
public class BaseRequest implements Serializable {
    private static final long serialVersionUID = 1L;


    private String createUser;


    private String updateUser;


    private LocalDateTime createTime;


    private LocalDateTime updateTime;
}