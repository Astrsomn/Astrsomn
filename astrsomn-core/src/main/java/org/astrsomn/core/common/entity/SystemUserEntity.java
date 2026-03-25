package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.astrsomn.core.common.base.BaseEntity;

@Data
@TableName("SYSTEM_USER")
public class SystemUserEntity extends BaseEntity<Long> {

    /**
     * id
     */
    @TableId(value = "ID", type = IdType.ASSIGN_ID)
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
     * Admin flag
     */
    @TableField("ADMIN_FLAG")
    private String adminFlag;

    /**
     * Email
     */
    @TableField("EMAIL")
    private String email;
}
