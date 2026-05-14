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
@TableName("AI_VEC_SEGMENT")
public class AiVecSegmentEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    
    @TableField("DOC_ID")
    private Long docId;

    
    @TableField("COLLECTION_ID")
    private Long collectionId;

    
    @TableField("VECTOR_ID")
    private String vectorId;

    
    @TableField("SEGMENT_CONTENT")
    private String segmentContent;

    
    @TableField("WORD_COUNT")
    private Long wordCount;

    
    @TableField("CHUNK_INDEX")
    private Long chunkIndex;

    
    @TableField("METADATA_JSON")
    private String metadataJson;


}