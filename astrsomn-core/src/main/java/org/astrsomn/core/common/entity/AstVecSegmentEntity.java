package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.astrsomn.core.common.base.BaseEntity;

@Data
@TableName("AST_VEC_SEGMENT")
public class AstVecSegmentEntity extends BaseEntity<Long> {


    @TableField("ID")
    private Long id;

    /**
     * 关联原始文档 ID 指向你的 AST_DOCUMENT
     */
    @TableField("DOC_ID")
    private Long docId;

    /**
     * 关联向量集合 ID 知道这段存到了哪个物理库
     */
    @TableField("COLLECTION_ID")
    private Long collectionId;

    /**
     * 向量库里的唯一标识 LangChain4j 生成的 UUID
     */
    @TableField("VECTOR_ID")
    private String vectorId;

    /**
     * 切片文本内容 冗余存储，方便后台预览
     */
    @TableField("SEGMENT_CONTENT")
    private String segmentContent;

    /**
     * 字符数 用于统计和 Token 计算
     */
    @TableField("WORD_COUNT")
    private Long wordCount;

    /**
     * 切片序号 第几段，方便排序还原全文
     */
    @TableField("CHUNK_INDEX")
    private Long chunkIndex;

    /**
     * 增强元数据 存储页码、作者、时间等
     */
    @TableField("METADATA_JSON")
    private String metadataJson;


}
