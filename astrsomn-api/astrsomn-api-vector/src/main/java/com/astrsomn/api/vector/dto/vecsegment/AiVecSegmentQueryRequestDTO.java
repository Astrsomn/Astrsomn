package com.astrsomn.api.vector.dto.vecsegment;

import com.astrsomn.api.runtime.common.entity.AiVecSegmentEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class AiVecSegmentQueryRequestDTO extends AiVecSegmentEntity implements Serializable {

    private Long docId;
    private Long collectionId;
    private String vectorId;
}