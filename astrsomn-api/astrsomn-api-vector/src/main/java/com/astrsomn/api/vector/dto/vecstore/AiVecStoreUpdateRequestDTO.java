package com.astrsomn.api.vector.dto.vecstore;

import com.astrsomn.api.vector.entity.AiVecStoreEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class AiVecStoreUpdateRequestDTO extends AiVecStoreEntity {


    @TableField(exist = false)
    private String accountKey;

}