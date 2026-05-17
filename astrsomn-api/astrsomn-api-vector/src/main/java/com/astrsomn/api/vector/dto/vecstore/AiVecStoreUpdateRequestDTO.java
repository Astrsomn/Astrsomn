package com.astrsomn.api.vector.dto.vecstore;

import com.astrsomn.api.vector.entity.AiVecStoreEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class AiVecStoreUpdateRequestDTO extends AiVecStoreEntity {

    /** 关联账号 Key，用于更新实例的 accountKey */
    @TableField(exist = false)
    private String accountKey;

}