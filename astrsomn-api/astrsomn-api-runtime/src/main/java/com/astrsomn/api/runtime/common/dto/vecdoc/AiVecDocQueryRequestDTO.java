package com.astrsomn.api.runtime.common.dto.vecdoc;

import com.astrsomn.api.runtime.common.entity.AiVecDocEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class AiVecDocQueryRequestDTO extends AiVecDocEntity implements Serializable {

    private Long collectionId;
    private String docIdInStore;
    private String syncStatus;
}