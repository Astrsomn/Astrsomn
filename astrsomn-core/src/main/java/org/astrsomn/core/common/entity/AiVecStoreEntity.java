package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.astrsomn.core.common.base.BaseEntity;

@Data
@TableName("AI_VEC_STORE")
public class AiVecStoreEntity extends BaseEntity<Long> {

    @TableField("ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField("SOURCE_ID")
    private Long sourceId;

    @TableField("COLLECTION_NAME")
    private String collectionName;

    @TableField("DIMENSION")
    private Long dimension;

    @TableField("DISTANCE_METRIC")
    private String distanceMetric;

    @TableField("METADATA_SCHEMA")
    private String metadataSchema;

    @TableField("MODEL_KEY")
    private String modelKey;
}
