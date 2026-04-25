package com.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import com.astrsomn.commn.base.BaseEntity;

@Data
@TableName("SYS_USER")
public class SystemUserEntity extends BaseEntity<Long> {

    /**
     * id
     */
    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * Username
     */
    @TableField("USERNAME")
    private String username;

    /**
     * Password
     */
    @TableField("PASSWORD")
    private String password;

    /**
     * Admin flag（兼容旧数据；与 {@link #userRole} 同步：管理员类为 Y）
     */
    @TableField("ADMIN_FLAG")
    private String adminFlag;

    /**
     * 角色：SUPER_ADMIN / ENV_ADMIN / USER
     */
    @TableField("USER_ROLE")
    private String userRole;

    /**
     * Email
     */
    @TableField("EMAIL")
    private String email;
}
