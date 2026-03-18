package org.astrsomn.core.common.base;

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
    @TableField("CREATE_TIME")
    private LocalDateTime createTime;

    /**
     * The timestamp when the record was last updated.
     */
    @TableField("UPDATE_TIME")
    private LocalDateTime updateTime;

    /**
     * The identifier or name of the user who created the record.
     */
    @TableField("CREATE_USER")
    private String createUser;

    /**
     * The identifier or name of the user who last modified the record.
     */
    @TableField("UPDATE_USER")
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
    @TableField("ENV_CODE")
    private String envCode;
}