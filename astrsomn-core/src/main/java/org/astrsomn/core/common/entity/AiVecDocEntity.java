package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.astrsomn.core.common.base.BaseEntity;

@Data
@TableName("AI_VEC_DOC")
public class AiVecDocEntity extends BaseEntity<Long> {

    @TableField("ID")
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



}
