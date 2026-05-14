package com.astrsomn.api.runtime.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import com.astrsomn.common.base.BaseEntity;

@Data
@TableName("SYS_USER")
public class SystemUserEntity extends BaseEntity<Long> {

    
    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    
    @TableField("USERNAME")
    private String username;

    
    @TableField("PASSWORD")
    private String password;

    
    @TableField("ADMIN_FLAG")
    private String adminFlag;

    
    @TableField("USER_ROLE")
    private String userRole;

    
    @TableField("EMAIL")
    private String email;
}