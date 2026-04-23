package com.astrsomn.core.common.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Base entity class containing common fields for all entities.
 *
 * @param <T> The type of the primary key ID.
 */
@Data
public class BaseEntity<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary key ID.
     */
    private T id;

    /**
     * The timestamp when the record was created.
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * The timestamp when the record was last updated.
     */
    @TableField(value = "UPDATE_TIME", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * The identifier or name of the user who created the record.
     */
    @TableField(value = "CREATE_USER", fill = FieldFill.INSERT)
    private String createUser;

    /**
     * The identifier or name of the user who last modified the record.
     */
    @TableField(value = "UPDATE_USER", fill = FieldFill.INSERT_UPDATE)
    private String updateUser;

    /**
     * Logical deletion flag.
     * False indicates active, True indicates deleted.
     */
    @TableField("DELETED")
    private Boolean deleted = false;

    /**
     * The environment code (e.g., DEV, TEST, PROD).
     */
    @TableField(value = "ENV_CODE", fill = FieldFill.INSERT)
    private String envCode;
}