package com.astrsomn.api.runtime.common.dto.vecdoc;

import lombok.Data;
import com.astrsomn.api.runtime.common.entity.AiVecDocEntity;

import java.io.Serializable;

@Data
public class AiVecDocQueryRequestDTO extends AiVecDocEntity implements Serializable {

    private Long collectionId;
    private String docIdInStore;
    private String syncStatus;
}
