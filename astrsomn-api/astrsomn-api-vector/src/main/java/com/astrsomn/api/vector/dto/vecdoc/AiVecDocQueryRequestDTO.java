package com.astrsomn.api.vector.dto.vecdoc;

import com.astrsomn.api.vector.entity.AiVecDocEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class AiVecDocQueryRequestDTO extends AiVecDocEntity implements Serializable {

    private Long collectionId;
    private String docIdInStore;
    private String syncStatus;
}