package com.astrsomn.api.runtime.common.dto.vecsegment;

import lombok.Data;
import com.astrsomn.api.runtime.common.entity.AiVecSegmentEntity;

import java.io.Serializable;

@Data
public class AiVecSegmentQueryRequestDTO extends AiVecSegmentEntity implements Serializable {

    private Long docId;
    private Long collectionId;
    private String vectorId;
}
