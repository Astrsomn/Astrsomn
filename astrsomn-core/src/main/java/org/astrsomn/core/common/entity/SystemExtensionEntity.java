package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.astrsomn.core.common.base.BaseEntity;

@Data
@TableName("SYSTEM_EXTENSION")
public class SystemExtensionEntity extends BaseEntity<Long> {

    @TableField("ID")
    private Long id;

    @TableField("EXTENSION_KEY")
    private String extensionKey;

    @TableField("EXTENSION_NAME")
    private String extensionName;

    @TableField("TYPE")
    private String type;

    @TableField("VERSION")
    private String version;

    @TableField("AUTHOR")
    private String author;

    @TableField("DESCRIPTION")
    private String description;

    @TableField("JAR_NAME")
    private String jarName;

    @TableField("APPLIED")
    private String applied;

    @TableField("STATUS")
    private String status;

    /**
     * 公用：厂商/提供方 code（如模型扩展同步 AI_MODEL 时对应 AiModelEnum.ProviderEnum），与 extensionKey 可不同。
     */
    @TableField("PROVIDER_CODE")
    private String providerCode;

}
