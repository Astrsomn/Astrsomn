package com.astrsomn.api.vector.entity;

import com.astrsomn.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
@TableName("AI_VEC_DOC")
public class AiVecDocEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    @TableField("COLLECTION_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long collectionId;


    @TableField("DOC_ID_IN_STORE")
    private String docIdInStore;


    @TableField("CONTENT_SUMMARY")
    private String contentSummary;


    @TableField("SYNC_STATUS")
    private String syncStatus;


    @TableField("FILE_PATH")
    private String filePath;


    @TableField("FILE_RECORD_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long fileRecordId;


    @TableField("ORIGINAL_FILE_NAME")
    private String originalFileName;


    @TableField("VECTORIZE_PROGRESS")
    private Integer vectorizeProgress;


    @TableField("VECTORIZE_MSG")
    private String vectorizeMsg;


    @TableField("VECTORIZE_TASK_ID")
    private String vectorizeTaskId;


    @TableField("TOTAL_SEGMENTS")
    private Integer totalSegments;


    @TableField("DONE_SEGMENTS")
    private Integer doneSegments;

    @TableField("FOLDER_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long folderId;

}