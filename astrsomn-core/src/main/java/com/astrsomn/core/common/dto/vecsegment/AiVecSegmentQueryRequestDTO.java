package com.astrsomn.core.common.dto.vecsegment;

import lombok.Data;
import com.astrsomn.core.common.entity.AiVecSegmentEntity;

import java.io.Serializable;

@Data
public class AiVecSegmentQueryRequestDTO extends AiVecSegmentEntity implements Serializable {

    private Long docId;
    private Long collectionId;
    private String vectorId;
}
