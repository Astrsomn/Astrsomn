package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.astrsomn.core.common.base.BaseEntity;

@Data
@TableName("AST_VEC_SOURCE")
public class AstVecSourceEntity extends BaseEntity<Long> {

    @TableField("ID")
    private Long id;

    @TableField("NAME")
    private String name;

    @TableField("PROVIDER")
    private String provider;

    @TableField("HOST")
    private String host;

    @TableField("PORT")
    private String port;

    @TableField("USERNAME")
    private String username;

    @TableField("PASSWORD")
    private String password;

    @TableField("DATABASE")
    private String database;

    /**
     * API Key Pinecone/DashVector
     */
    @TableField("TOKEN")
    private String token;

    @TableField("CONFIG_JSON")
    private String configJson;

    @TableField("STATUS")
    private String status;
}
