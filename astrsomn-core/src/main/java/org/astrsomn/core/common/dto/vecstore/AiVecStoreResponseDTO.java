package org.astrsomn.core.common.dto.vecstore;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.astrsomn.core.common.entity.AiVecStoreEntity;

@Data
public class AiVecStoreResponseDTO extends AiVecStoreEntity {

    @TableField(exist = false)
    private String sourceName;

    @TableField(exist = false)
    private String sourceProvider;

    @TableField(exist = false)
    private String instanceName;
}
