package org.astrsomn.core.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.astrsomn.core.common.base.BaseEntity;

@Data
@TableName("RAG_DOCUMENT")
public class RagDocument extends BaseEntity<Long> {
}
