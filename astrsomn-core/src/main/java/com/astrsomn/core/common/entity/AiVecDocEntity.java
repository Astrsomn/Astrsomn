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
@TableName("AI_VEC_DOC")
public class AiVecDocEntity extends BaseEntity<Long> {

    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 所属集合 ID
     */
    @TableField("COLLECTION_ID")
    private Long collectionId;

    /**
     * 向量库中的实际唯一标识（如 UUID 或 Long）
     */
    @TableField("DOC_ID_IN_STORE")
    private String docIdInStore;

    /**
     * 文本摘要或路径
     */
    @TableField("CONTENT_SUMMARY")
    private String contentSummary;

    /**
     * 同步状态（待向量化、已入库、已失效）
     */
    @TableField("SYNC_STATUS")
    private String syncStatus;

    /**
     * 文件相对路径
     */
    @TableField("FILE_PATH")
    private String filePath;

    /**
     * 对应文件记录 ID
     */
    @TableField("FILE_RECORD_ID")
    private Long fileRecordId;

    /**
     * 原始文件名
     */
    @TableField("ORIGINAL_FILE_NAME")
    private String originalFileName;



}
